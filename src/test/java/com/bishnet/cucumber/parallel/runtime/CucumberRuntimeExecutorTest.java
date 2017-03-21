package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import gherkin.lexer.Ru;
import org.junit.Test;

import cucumber.runtime.CucumberException;

public class CucumberRuntimeExecutorTest {

	@Test
	public void shouldReturnZeroExitCodeWhenAllUnderlyingRuntimesReturnZero() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 2;
		List<String> arguments = setFeaturePath();
		CucumberRuntimeExecutor runtimeExecutor =
				getRuntimeExecutor(exitCodes, shouldThrowExceptions, false, false, arguments, numberOfThreads, false);
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnExitCodeOfOneWhenOneUnderlyingRuntimeReturnsOne() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 1 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 2;
		List<String> arguments = setFeaturePath();
		CucumberRuntimeExecutor runtimeExecutor =
				getRuntimeExecutor(exitCodes, shouldThrowExceptions, false, false, arguments, numberOfThreads, false);
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 1);
	}

	@Test
	public void shouldReturnExitCodeOfOneWhenAllUnderlyingRuntimesReturnOne() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 1, 1 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 2;
		List<String> arguments = setFeaturePath();
		CucumberRuntimeExecutor runtimeExecutor =
				getRuntimeExecutor(exitCodes, shouldThrowExceptions, false, false, arguments, numberOfThreads, false);
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 1);
	}

	@Test(expected = CucumberException.class)
	public void shouldThrowCucumberExceptionWhenOneUnderlyingRuntimeDoes() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, true };
		int numberOfThreads = 2;
		List<String> arguments = setFeaturePath();
		CucumberRuntimeExecutor runtimeExecutor =
				getRuntimeExecutor(exitCodes, shouldThrowExceptions, false, false, arguments, numberOfThreads, false);
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnANumberOfRerunFilesEqualToTheNumberOfThreadsIfTheDynamicDistributionOptionIsNotEnabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(false, true, arguments, numberOfThreads, false);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(rerunFiles.size()).isEqualTo(numberOfThreads);
	}

	@Test
	public void shouldReturnANumberOfRerunFilesEqualToTheNumberOfThreadsIfTheDynamicDistributionOptionIsEnabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(false, true, arguments, numberOfThreads, true);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(rerunFiles.size()).isNotEqualTo(numberOfThreads);
		assertThat(rerunFiles.size()).isEqualTo(arguments.size());
	}

	@Test
	public void shouldReturnANumberOfHtmlReportsEqualToTheNumberOfRerunFilesIfDynamicDistributionIsDisabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(false, true, arguments, numberOfThreads, false);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getHtmlReports().size()).isEqualTo(rerunFiles.size());
		assertThat(runtimeExecutor.getHtmlReports().size()).isEqualTo(numberOfThreads);
	}

	@Test
	public void shouldReturnANumberOfJsonReportsEqualToTheNumberOfRerunFilesIfDynamicDistributionIsDisabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(true, true, arguments, numberOfThreads, false);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getJsonReports().size()).isEqualTo(rerunFiles.size());
		assertThat(runtimeExecutor.getJsonReports().size()).isEqualTo(numberOfThreads);
	}

	@Test
	public void shouldReturnANumberOfHtmlReportsEqualToTheNumberOfRerunFilesIfDynamicDistributionIsEnabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(true, true, arguments, numberOfThreads, true);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getHtmlReports().size()).isEqualTo(rerunFiles.size());
		assertThat(runtimeExecutor.getHtmlReports().size()).isNotEqualTo(numberOfThreads);
	}

	@Test
	public void shouldReturnANumberOfJsonReportsEqualToTheNumberOfRerunFilesIfDynamicDistributionIsEnabled()
			throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		int numberOfThreads = 1;
		List<String> arguments = setFeaturePath();
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(true, true, arguments, numberOfThreads, true);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getJsonReports().size()).isEqualTo(rerunFiles.size());
		assertThat(runtimeExecutor.getJsonReports().size()).isNotEqualTo(numberOfThreads);
	}

	private List<String> setFeaturePath() {
		List<String> arguments = new ArrayList<>();
        arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory/FirstFeature.feature");
        arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory/SecondFeature.feature");
		return arguments;
	}

	private List<Path> getRerunFiles(RuntimeConfiguration runtimeConfiguration) throws IOException {
		FeatureParser featureParser = new FeatureParser(runtimeConfiguration, Thread.currentThread().getContextClassLoader());
		FeatureSplitter featureSplitter = new FeatureSplitter(runtimeConfiguration, featureParser.parseFeatures());
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		return rerunFiles;
	}

	private RuntimeConfiguration getRuntimeConfiguration(boolean jsonReportRequired, boolean htmlReportRequired,
			List<String> featureParsingArguments, int numberOfThreads, boolean dynamicFeatureDistribution) {
		return new RuntimeConfiguration(numberOfThreads, null, featureParsingArguments, null, null, htmlReportRequired, null,
				jsonReportRequired, null, false, dynamicFeatureDistribution);
	}

	private CucumberRuntimeExecutor getRuntimeExecutor(byte[] perInvocationExitCodes, boolean[] perInvocationShouldThrowException,
			boolean jsonReportRequired, boolean htmlReportRequired, List<String> featureParsingArguments, int numberOfThreads,
			boolean dynamicFeatureDistribution) throws InterruptedException, IOException {
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(jsonReportRequired, htmlReportRequired,
				featureParsingArguments, numberOfThreads, dynamicFeatureDistribution);
		List<Path> rerunFiles = getRerunFiles(runtimeConfiguration);
		FakeCucumberRuntimeFactory runtimeFactory =
				new FakeCucumberRuntimeFactory(perInvocationExitCodes, perInvocationShouldThrowException, runtimeConfiguration);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);
		return runtimeExecutor;
	}
}
