package com.bishnet.cucumber.parallel.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;

import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Reflections;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.UndefinedStepsTracker;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.xstream.LocalizedXStreams;

public class CucumberRuntimeFactory {

	private RuntimeConfiguration runtimeConfiguration;
	private CucumberBackendFactory cucumberBackendFactory;
	private ClassLoader cucumberClassLoader;
	private ThreadExecutionRecorder threadExecutionRecorder;

	public CucumberRuntimeFactory(RuntimeConfiguration runtimeConfiguration, ClassLoader cucumberClassLoader) {
		this(runtimeConfiguration, null, cucumberClassLoader);
	}

	public CucumberRuntimeFactory(RuntimeConfiguration runtimeConfiguration, CucumberBackendFactory cucumberBackendFactory,
								  ClassLoader cucumberClassLoader) {
		this(runtimeConfiguration, cucumberBackendFactory, cucumberClassLoader, null);
	}

	public CucumberRuntimeFactory(RuntimeConfiguration runtimeConfiguration, CucumberBackendFactory cucumberBackendFactory,
								  ClassLoader cucumberClassLoader, ThreadExecutionRecorder threadExecutionRecorder) {
		this.runtimeConfiguration = runtimeConfiguration;
		this.cucumberBackendFactory = cucumberBackendFactory;
		this.cucumberClassLoader = cucumberClassLoader;
		this.threadExecutionRecorder = threadExecutionRecorder;
	}

	public Runtime getRuntime(List<String> additionalCucumberArguments) {
		List<String> runtimeCucumberArguments = new ArrayList<String>(runtimeConfiguration.cucumberPassthroughArguments);
		runtimeCucumberArguments.addAll(additionalCucumberArguments);
		RuntimeOptions runtimeOptions = new RuntimeOptions(runtimeCucumberArguments);
		ResourceLoader resourceLoader = getResourceLoader();

		Runtime runtime = null;

		if (runtimeConfiguration.threadTimelineReportRequired){
			runtime = createThreadLoggedRuntime(runtimeOptions, resourceLoader);
		} else {
			runtime = createDefaultRuntime(runtimeOptions, resourceLoader);
		}

		return runtime;
	}

	private Runtime createDefaultRuntime(RuntimeOptions runtimeOptions, ResourceLoader resourceLoader) {
		
		RuntimeGlue runtimeGlue = buildThreadSafeRuntimeGlue();
		
		if (cucumberBackendFactory == null) {
			ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, cucumberClassLoader);
			return new Runtime(resourceLoader, cucumberClassLoader, loadBackends(resourceLoader, classFinder), runtimeOptions, runtimeGlue);
		} else {
			return new Runtime(resourceLoader, cucumberClassLoader, cucumberBackendFactory.getBackends(), runtimeOptions, runtimeGlue);
		}
	}

	private Runtime createThreadLoggedRuntime(RuntimeOptions runtimeOptions, ResourceLoader resourceLoader) {
		
		RuntimeGlue runtimeGlue = buildThreadSafeRuntimeGlue();
		
		if (cucumberBackendFactory == null) {
			ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, cucumberClassLoader);
			return new ThreadLoggedRuntime(resourceLoader, cucumberClassLoader, loadBackends(resourceLoader, classFinder), runtimeOptions, threadExecutionRecorder, runtimeGlue);
		} else {
			return new ThreadLoggedRuntime(resourceLoader, cucumberClassLoader, cucumberBackendFactory.getBackends(), runtimeOptions, threadExecutionRecorder, runtimeGlue);
		}
	}

	private ResourceLoader getResourceLoader() {
		List<Path> fileSystemFeaturePaths = getFileSystemFeaturePaths();
		if (fileSystemFeaturePaths.size() == 0)
			return new MultiLoader(cucumberClassLoader);
		URL[] urls = new URL[fileSystemFeaturePaths.size()];
		int index = 0;
		for (Path featurePath : fileSystemFeaturePaths) {
			try {
				urls[index] = featurePath.toUri().toURL();
			} catch (MalformedURLException e) {
				throw new CucumberException(e);
			}
			index++;
		}
		URLClassLoader featuresLoader = new URLClassLoader(urls, cucumberClassLoader);
		return new MultiLoader(featuresLoader);
	}

	private List<Path> getFileSystemFeaturePaths() {
		List<Path> fileSystemFeaturePaths = new ArrayList<Path>();
		for (String featurePath : runtimeConfiguration.featurePaths) {
			if (!featurePath.startsWith(MultiLoader.CLASSPATH_SCHEME))
				fileSystemFeaturePaths.add(Paths.get(featurePath));
		}
		return fileSystemFeaturePaths;
	}
	
	protected RuntimeGlue buildThreadSafeRuntimeGlue() {
		UndefinedStepsTracker undefinedStepsTracker = new UndefinedStepsTracker();
		return new ThreadSafeRuntimeGlue(undefinedStepsTracker, new LocalizedXStreams(cucumberClassLoader));
	}
	
	protected Collection<? extends Backend> loadBackends(ResourceLoader resourceLoader, ClassFinder classFinder) {
        Reflections reflections = new Reflections(classFinder);
        return reflections.instantiateSubclasses(Backend.class, "cucumber.runtime", new Class[]{ResourceLoader.class}, new Object[]{resourceLoader});
    }
}
