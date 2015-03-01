package com.bishnet.cucumber.parallel.runtime;

import static org.fest.assertions.Assertions.assertThat;

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
		FeatureParser featureParser = new FeatureParser(arguments);
		FeatureSplitter featureSplitter = new FeatureSplitter(featureParser.parseFeatures(), 1);
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(1);
	}
	
	@Test
	public void whenPassedARequestForTwoThreadsShouldReturnTwoRerunFiles() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		FeatureParser featureParser = new FeatureParser(arguments);
		FeatureSplitter featureSplitter = new FeatureSplitter(featureParser.parseFeatures(), 2);
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(2);
	}
	
	@Test
	public void whenPassedARequestForMoreThreadsThanFeaturesShouldReturnRerunFilesEqualToFeatureCount() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		FeatureParser featureParser = new FeatureParser(arguments);
		List<CucumberFeature> features = featureParser.parseFeatures();
		FeatureSplitter featureSplitter = new FeatureSplitter(features, 10);
		List<Path> rerunFiles = featureSplitter.splitFeaturesIntoRerunFiles();
		assertThat(rerunFiles.size()).isEqualTo(features.size());
	}
}
