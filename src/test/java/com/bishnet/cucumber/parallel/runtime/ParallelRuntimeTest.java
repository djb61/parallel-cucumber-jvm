package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class ParallelRuntimeTest {

	@Test
	public void shouldReturnExitCodeOfZeroWhenNoScenariosMatchedSelectedFilters() throws InterruptedException, IOException {
		List<String> arguments = new ArrayList<>();
		arguments.add(
				"classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeatureWithExcludingTag.feature");
		arguments.add("--tags");
		arguments.add("@IncludedTag");
		arguments.add("--tags");
		arguments.add("~@ExcludedTag");
		ParallelRuntime runtime = new ParallelRuntime(arguments);
		assertThat(runtime.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnExitCodeOfZeroWhenNoScenariosFoundForExecution() throws InterruptedException, IOException {
		List<String> arguments = new ArrayList<>();
		arguments.add(
				"classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeatureWithNoScenarios.feature");
		ParallelRuntime runtime = new ParallelRuntime(arguments);
		assertThat(runtime.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnExitCodeOfZeroWhenNoFeaturesPassedForExecution() throws InterruptedException, IOException {
		List<String> arguments = new ArrayList<>();
		ParallelRuntime runtime = new ParallelRuntime(arguments);
		assertThat(runtime.run()).isEqualTo((byte) 0);
	}

}
