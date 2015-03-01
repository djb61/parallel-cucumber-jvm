package com.bishnet.cucumber.parallel.runtime;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CucumberRuntimeFactoryTest {
	
	private List<String> cucumberArguments = new ArrayList<String>();

	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPaths() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(featurePaths);
		runtimeFactory.getRuntime(cucumberArguments);
	}
	
	@Test
	public void shouldReturnRuntimeWhenPassedValidFileSystemPathsAndClasspathFeatures() {
		List<String> featurePaths = new ArrayList<String>();
		featurePaths.add("/some/absolute/path");
		featurePaths.add("a/relative/path");
		featurePaths.add("classpath:");
		CucumberRuntimeFactory runtimeFactory = new CucumberRuntimeFactory(featurePaths);
		runtimeFactory.getRuntime(cucumberArguments);
	}
}
