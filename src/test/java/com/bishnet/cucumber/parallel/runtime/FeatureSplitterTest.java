package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cucumber.runtime.model.CucumberFeature;

public class FeatureSplitterTest {

	@Test
	public void whenPassedARequestForOneThreadShouldReturnOneRerunFile() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(arguments, 1);
		FeatureParser featureParser = new FeatureParser(runtimeConfiguration, Thread.currentThread().getContextClassLoader());
		FeatureSplitter featureSplitter = new FeatureSplitter(runtimeConfiguration, featureParser.parseFeatures());
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(1);
	}

	@Test
	public void whenPassedARequestForTwoThreadsShouldReturnTwoRerunFiles() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(arguments, 2);
		FeatureParser featureParser = new FeatureParser(runtimeConfiguration, Thread.currentThread().getContextClassLoader());
		FeatureSplitter featureSplitter = new FeatureSplitter(runtimeConfiguration, featureParser.parseFeatures());
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(2);
	}

	@Test
	public void whenPassedARequestForMoreThreadsThanFeaturesShouldReturnRerunFilesEqualToFeatureCount() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		RuntimeConfiguration runtimeConfiguration = getRuntimeConfiguration(arguments, 10);
		FeatureParser featureParser = new FeatureParser(runtimeConfiguration, Thread.currentThread().getContextClassLoader());
		List<CucumberFeature> features = featureParser.parseFeatures();
		FeatureSplitter featureSplitter = new FeatureSplitter(runtimeConfiguration, features);
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(features.size());
	}

	private RuntimeConfiguration getRuntimeConfiguration(List<String> featureParsingArguments, int numberOfThreads) {
		return new RuntimeConfiguration(numberOfThreads, null, featureParsingArguments, null, null, false, null, false, null, false);
	}
}
