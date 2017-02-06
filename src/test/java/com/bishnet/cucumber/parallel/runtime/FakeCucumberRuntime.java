package com.bishnet.cucumber.parallel.runtime;

import java.util.Collection;

import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;

import cucumber.runtime.Backend;
import cucumber.runtime.CucumberException;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;

public class FakeCucumberRuntime extends ThreadLoggedRuntime {

	private byte exitCode;
	private boolean shouldThrowExceptionOnRun;

	public FakeCucumberRuntime(byte exitCode, boolean shouldThrowExceptionOnRun, 
			ResourceLoader resourceLoader, ClassLoader classLoader,
			Collection<? extends Backend> backends, RuntimeOptions runtimeOptions,
			ThreadExecutionRecorder threadExecutionRecorder, RuntimeGlue runtimeGlue) {
		super(resourceLoader, classLoader, backends, runtimeOptions, threadExecutionRecorder, runtimeGlue);
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
