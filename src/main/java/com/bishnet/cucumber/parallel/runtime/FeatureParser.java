package com.bishnet.cucumber.parallel.runtime;

import java.util.List;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;

public class FeatureParser {
	
	private RuntimeConfiguration runtimeConfiguration;

	public FeatureParser(RuntimeConfiguration runtimeConfiguration) {
		this.runtimeConfiguration = runtimeConfiguration;
	}
	
	public List<CucumberFeature> parseFeatures() {
		RuntimeOptions runtimeOptions = new RuntimeOptions(runtimeConfiguration.featureParsingArguments);
		ResourceLoader resourceLoader = new MultiLoader(Thread.currentThread().getContextClassLoader());
		return runtimeOptions.cucumberFeatures(resourceLoader);
	}
}
