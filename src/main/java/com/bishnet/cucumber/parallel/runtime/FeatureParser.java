package com.bishnet.cucumber.parallel.runtime;

import java.util.List;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;

public class FeatureParser {

	private RuntimeConfiguration runtimeConfiguration;
	private ClassLoader featureClassLoader;

	public FeatureParser(RuntimeConfiguration runtimeConfiguration, ClassLoader featureClassLoader) {
		this.runtimeConfiguration = runtimeConfiguration;
		this.featureClassLoader = featureClassLoader;
	}

	public List<CucumberFeature> parseFeatures() {
		RuntimeOptions runtimeOptions = new RuntimeOptions(runtimeConfiguration.featureParsingArguments);
		ResourceLoader resourceLoader = new MultiLoader(featureClassLoader);
		return runtimeOptions.cucumberFeatures(resourceLoader);
	}
}
