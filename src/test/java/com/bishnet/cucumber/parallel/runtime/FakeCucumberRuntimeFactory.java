package com.bishnet.cucumber.parallel.runtime;

import java.util.ArrayList;
import java.util.List;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;

public class FakeCucumberRuntimeFactory extends CucumberRuntimeFactory {

	private byte[] perInvocationExitCodes;
	private boolean[] perInvocationShouldThrowException;
	private int invocationCount;
	
	public FakeCucumberRuntimeFactory(byte[] perInvocationExitCodes, boolean[] perInvocationShouldThrowException) {
		super(new ArrayList<String>(), new ArrayList<String>());
		this.perInvocationExitCodes = perInvocationExitCodes;
		this.perInvocationShouldThrowException = perInvocationShouldThrowException;
	}
	
	@Override
	public Runtime getRuntime(List<String> additionalCucumberArgs) {
		RuntimeOptions runtimeOptions = new RuntimeOptions(additionalCucumberArgs);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(classLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
		byte exitCode = perInvocationExitCodes[invocationCount % perInvocationExitCodes.length];
		boolean shouldThrowException = perInvocationShouldThrowException[invocationCount % perInvocationExitCodes.length];
		invocationCount++;
        return new FakeCucumberRuntime(exitCode, shouldThrowException, resourceLoader, classFinder, classLoader, runtimeOptions);
	}
}
