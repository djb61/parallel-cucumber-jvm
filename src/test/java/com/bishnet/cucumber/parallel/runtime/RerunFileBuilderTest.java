package com.bishnet.cucumber.parallel.runtime;

import static org.fest.assertions.Assertions.assertThat;
import gherkin.formatter.model.Result;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cucumber.runtime.model.CucumberFeature;

public class RerunFileBuilderTest {
	
	private FakeRerunFormatter fakeRerunFormatter;
	private RerunFileBuilder rerunFileBuilder;

	@Before
	public void setup() {
		fakeRerunFormatter = new FakeRerunFormatter();
		rerunFileBuilder = new RerunFileBuilder(fakeRerunFormatter, fakeRerunFormatter);
	}

	@Test
	public void formatterCalledCorrectlyForAFeatureWithOneScenario() {
		String featurePath = "com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeature.feature";
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:" + featurePath);
		FeatureParser featureParser = new FeatureParser(arguments);
		for(CucumberFeature cucumberFeature : featureParser.parseFeatures())
			rerunFileBuilder.addFeature(cucumberFeature);
		assertThat(fakeRerunFormatter.getUri()).isEqualTo(featurePath);
		assertThat(fakeRerunFormatter.getStartOfLifeCycleInvocationCount()).isEqualTo(1);
		assertThat(fakeRerunFormatter.getEndOfLifeCycleInvocationCount()).isEqualTo(1);
		assertThat(fakeRerunFormatter.getUriInvocationCount()).isEqualTo(1);
		assertThat(fakeRerunFormatter.getScenarioInvocationCount()).isEqualTo(1);
		assertThat(fakeRerunFormatter.getResult().getStatus()).isEqualTo(Result.FAILED);
	}
	
	@Test
	public void formatterCalledCorrectlyForAFeatureWithThreeScenarios() {
		String featurePath = "com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeatureThreeScenarios.feature";
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:" + featurePath);
		FeatureParser featureParser = new FeatureParser(arguments);
		for(CucumberFeature cucumberFeature : featureParser.parseFeatures())
			rerunFileBuilder.addFeature(cucumberFeature);
		assertThat(fakeRerunFormatter.getUri()).isEqualTo(featurePath);
		assertThat(fakeRerunFormatter.getStartOfLifeCycleInvocationCount()).isEqualTo(3);
		assertThat(fakeRerunFormatter.getEndOfLifeCycleInvocationCount()).isEqualTo(3);
		assertThat(fakeRerunFormatter.getScenarioInvocationCount()).isEqualTo(3);
		assertThat(fakeRerunFormatter.getUriInvocationCount()).isEqualTo(1);
	}
	
	@Test
	public void formatterCalledCorrectlyForTwoFeaturesWithTotalFiveScenarios() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:com/bishnet/cucumber/parallel/runtime/samplefeatures/directory");
		FeatureParser featureParser = new FeatureParser(arguments);
		for(CucumberFeature cucumberFeature : featureParser.parseFeatures())
			rerunFileBuilder.addFeature(cucumberFeature);
		assertThat(fakeRerunFormatter.getStartOfLifeCycleInvocationCount()).isEqualTo(5);
		assertThat(fakeRerunFormatter.getEndOfLifeCycleInvocationCount()).isEqualTo(5);
		assertThat(fakeRerunFormatter.getScenarioInvocationCount()).isEqualTo(5);
		assertThat(fakeRerunFormatter.getUriInvocationCount()).isEqualTo(2);
	}
	
	@Test
	public void formatterCalledCorrectlyForAFeatureWithAScenarioOutlineWithTwoExamples() {
		String featurePath = "com/bishnet/cucumber/parallel/runtime/samplefeatures/individual/ValidFeatureWithScenarioOutlineWithTwoExamples.feature";
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:" + featurePath);
		FeatureParser featureParser = new FeatureParser(arguments);
		for(CucumberFeature cucumberFeature : featureParser.parseFeatures())
			rerunFileBuilder.addFeature(cucumberFeature);
		assertThat(fakeRerunFormatter.getUri()).isEqualTo(featurePath);
		assertThat(fakeRerunFormatter.getStartOfLifeCycleInvocationCount()).isEqualTo(2);
		assertThat(fakeRerunFormatter.getEndOfLifeCycleInvocationCount()).isEqualTo(2);
		assertThat(fakeRerunFormatter.getScenarioInvocationCount()).isEqualTo(2);
		assertThat(fakeRerunFormatter.getUriInvocationCount()).isEqualTo(1);
	}
	
	@Test
	public void formatterCalledCorrectlyWhenFileBuilderIsClose() {
		rerunFileBuilder.close();
		assertThat(fakeRerunFormatter.getDoneInvocationCount()).isEqualTo(1);
		assertThat(fakeRerunFormatter.getCloseInvocationCount()).isEqualTo(1);
	}
}
