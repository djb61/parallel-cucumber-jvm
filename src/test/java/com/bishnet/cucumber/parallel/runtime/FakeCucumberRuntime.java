package com.bishnet.cucumber.parallel.runtime;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;

public class FakeCucumberRuntime extends Runtime {

	private byte exitCode;
	private boolean shouldThrowExceptionOnRun;
	
	public FakeCucumberRuntime(byte exitCode, boolean shouldThrowExceptionOnRun, ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader, RuntimeOptions runtimeOptions) {
		super(resourceLoader, classFinder, classLoader, runtimeOptions);
		this.exitCode = exitCode;
		this.shouldThrowExceptionOnRun = shouldThrowExceptionOnRun;
	}
	
	@Override
	public void run() {
		if (shouldThrowExceptionOnRun)
			throw new CucumberException("Fake runtime was asked to throw an exception");
	}
	
	@Override
	public byte exitStatus() {
		return exitCode;
	}
}
