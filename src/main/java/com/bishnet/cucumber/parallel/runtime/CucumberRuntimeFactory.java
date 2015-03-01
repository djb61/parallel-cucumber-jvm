package com.bishnet.cucumber.parallel.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;

public class CucumberRuntimeFactory {

	private List<String> featurePaths;
	private List<String> baseCucumberArguments;
	
	public CucumberRuntimeFactory(List<String> featurePaths, List<String> baseCucumberArguments) {
		this.featurePaths = featurePaths;
		this.baseCucumberArguments = baseCucumberArguments;
	}
	
	public Runtime getRuntime(List<String> additionalCucumberArguments) {
		List<String> runtimeCucumberArguments = new ArrayList<String>(baseCucumberArguments);
		runtimeCucumberArguments.addAll(additionalCucumberArguments);
		RuntimeOptions runtimeOptions = new RuntimeOptions(runtimeCucumberArguments);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = getResourceLoader();
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
	}
	
	private ResourceLoader getResourceLoader() {
		List<Path> fileSystemFeaturePaths = getFileSystemFeaturePaths();
		if (fileSystemFeaturePaths.size() == 0)
			return new MultiLoader(Thread.currentThread().getContextClassLoader());
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
		URLClassLoader featuresLoader = new URLClassLoader(urls);
		return new MultiLoader(featuresLoader);
	}
	
	private List<Path> getFileSystemFeaturePaths() {
		List<Path> fileSystemFeaturePaths = new ArrayList<Path>();
		for (String featurePath : featurePaths) {
			if (!featurePath.startsWith(MultiLoader.CLASSPATH_SCHEME))
				fileSystemFeaturePaths.add(Paths.get(featurePath));
		}
		return fileSystemFeaturePaths;
	}
}
