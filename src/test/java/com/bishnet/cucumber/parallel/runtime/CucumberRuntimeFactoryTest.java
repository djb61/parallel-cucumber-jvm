package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.ArrayList;
import java.util.List;

import cucumber.runtime.Runtime;
import org.junit.Test;

public class CucumberRuntimeFactoryTest {

	private List<String> cucumberArguments = new ArrayList<String>();

	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPaths() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, Thread.currentThread()
				.getContextClassLoader());
		runtimeFactory.getRuntime(cucumberArguments);
	}
	
	@Test
	public void shouldReturnRuntimeWhenPassedValidRerunFile() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("@C:/windows/absolute/path.rerun");
		featurePaths.add("@a/relative/path.rerun");
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, Thread.currentThread()
				.getContextClassLoader());
		runtimeFactory.getRuntime(cucumberArguments);
	}

	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPathsAndClasspathFeatures() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		featurePaths.add("classpath:");
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, Thread.currentThread()
				.getContextClassLoader());
		runtimeFactory.getRuntime(cucumberArguments);
	}

	@Test
	public void shouldInvokeBackendFactoryIfOneIsProvided() {
		List<String> featurePaths = new ArrayList<String>();
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		FakeCucumberBackendFactory cucumberBackendFactory = new FakeCucumberBackendFactory();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, Thread
				.currentThread().getContextClassLoader());
		runtimeFactory.getRuntime(cucumberArguments);
		assertThat(cucumberBackendFactory.wasInvoked()).isTrue();
	}

	@Test
	public void shouldReturnDefaultRuntime(){
		List<String> featurePaths = new ArrayList<String>();
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		FakeCucumberBackendFactory cucumberBackendFactory = new FakeCucumberBackendFactory();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, false, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, Thread
				.currentThread().getContextClassLoader());
		assertThat(runtimeFactory.getRuntime(cucumberArguments)).isInstanceOf(Runtime.class);
	}

	@Test
	public void shouldReturnThreadLoggedRuntimeWhenReportIsRequired(){
		List<String> featurePaths = new ArrayList<String>();
		List<String> cucumberPassthroughArguments = new ArrayList<String>();
		FakeCucumberBackendFactory cucumberBackendFactory = new FakeCucumberBackendFactory();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(0, cucumberPassthroughArguments, null, featurePaths,
				null, false, null, false, null, true, null, false);
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, Thread
				.currentThread().getContextClassLoader());
		assertThat(runtimeFactory.getRuntime(cucumberArguments)).isInstanceOf(ThreadLoggedRuntime.class);
	}

}
