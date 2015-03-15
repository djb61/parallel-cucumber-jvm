package com.bishnet.cucumber.parallel.runtime;

import java.util.List;
import java.util.concurrent.Callable;

import cucumber.runtime.Runtime;

public class CucumberRuntimeCallable implements Callable<Byte> {

	private List<String> arguments;
	private CucumberRuntimeFactory runtimeFactory;

	public CucumberRuntimeCallable(List<String> arguments, CucumberRuntimeFactory runtimeFactory) {
		this.arguments = arguments;
		this.runtimeFactory = runtimeFactory;
	}

	@Override
	public Byte call() throws Exception {
		Runtime runtime = runtimeFactory.getRuntime(arguments);
		runtime.run();
		return runtime.exitStatus();
	}

}
