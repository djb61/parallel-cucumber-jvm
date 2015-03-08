package com.bishnet.cucumber.parallel.cli;

import java.util.Arrays;

import com.bishnet.cucumber.parallel.runtime.ParallelRuntime;

public class Main {

	public static void main(String[] argv) {
		ParallelRuntime parallelRuntime = new ParallelRuntime(Arrays.asList(argv));
		byte result = parallelRuntime.run();
		System.exit(result);
	}
}
