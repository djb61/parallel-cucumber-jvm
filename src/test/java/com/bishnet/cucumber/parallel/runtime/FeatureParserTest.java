package com.bishnet.cucumber.parallel.runtime;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bishnet.cucumber.parallel.runtime.FeatureParser;

import cucumber.runtime.CucumberException;
import cucumber.runtime.model.CucumberFeature;

public class FeatureParserTest {

	@Test
	public void singleFeatureFileWithValidScenariosShouldReturnOneFeature() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeature.feature");
		FeatureParser featureParser = new FeatureParser(arguments);
		List<CucumberFeature> features = featureParser.parseFeatures();
		assertThat(features.size()).isEqualTo(1);
	}
	
	@Test
	public void featureDirectoryWithTwoValidFeaturesShouldReturnTwoFeatures() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		FeatureParser featureParser = new FeatureParser(arguments);
		List<CucumberFeature> features = featureParser.parseFeatures();
		assertThat(features.size()).isEqualTo(2);
	}
	
	@Test(expected=CucumberException.class)
	public void singleFeatureFileWithAnInvalidScenarioShouldThrowACucumberException() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/InvalidFeature.feature");
		FeatureParser featureParser = new FeatureParser(arguments);
		featureParser.parseFeatures();
	}
}
