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

	private byte exitCode;
	private boolean shouldThrowException;
	
	public FakeCucumberRuntimeFactory(byte exitCode) {
		super(new ArrayList<String>(), new ArrayList<String>());
		this.exitCode = exitCode;
	}
	
	public void throwAnExceptionOnRuntimeRun() {
		this.shouldThrowException = true;
	}
	
	@Override
	public Runtime getRuntime(List<String> additionalCucumberArgs) {
		RuntimeOptions runtimeOptions = new RuntimeOptions(additionalCucumberArgs);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(classLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new FakeCucumberRuntime(exitCode, shouldThrowException, resourceLoader, classFinder, classLoader, runtimeOptions);
	}
}
