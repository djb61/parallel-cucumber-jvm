package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import cucumber.runtime.Runtime;
import org.junit.Test;

public class CucumberRuntimeFactoryTest {

	private List<String> cucumberArguments = new ArrayList<String>();
	private List<String> cucumberPassthroughArguments = new ArrayList<String>();
	private FakeCucumberBackendFactory cucumberBackendFactory = new FakeCucumberBackendFactory();
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPaths() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, classLoader);
		Runtime runtime = runtimeFactory.getRuntime(cucumberArguments);
		assertThat(runtime.getGlue()).isInstanceOf(ThreadSafeRuntimeGlue.class);
	}

	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPathsAndClasspathFeatures() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		featurePaths.add("classpath:");
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, classLoader);
		runtimeFactory.getRuntime(cucumberArguments);
	}

	@Test
	public void shouldInvokeBackendFactoryIfOneIsProvided() {
		List<String> featurePaths = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, classLoader);
		runtimeFactory.getRuntime(cucumberArguments);
		assertThat(cucumberBackendFactory.wasInvoked()).isTrue();
	}

	@Test
	public void shouldReturnDefaultRuntime(){
		List<String> featurePaths = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, classLoader);
		Runtime runtime = runtimeFactory.getRuntime(cucumberArguments);
		assertThat(runtime).isInstanceOf(Runtime.class);
		assertThat(runtime.getGlue()).isInstanceOf(ThreadSafeRuntimeGlue.class);
	}

	@Test
	public void shouldReturnThreadLoggedRuntimeWhenReportIsRequired(){
		List<String> featurePaths = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, true);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, classLoader);
		Runtime runtime = runtimeFactory.getRuntime(cucumberArguments);
		assertThat(runtime).isInstanceOf(ThreadLoggedRuntime.class);
		assertThat(runtime.getGlue()).isInstanceOf(ThreadSafeRuntimeGlue.class);
	}

}
