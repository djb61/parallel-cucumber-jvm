package com.bishnet.cucumber.parallel.runtime;

import java.util.List;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;

public class FeatureParser {
	
	private List<String> arguments;

	public FeatureParser(List<String> arguments) {
		this.arguments = arguments;
	}
	
	public List<CucumberFeature> parseFeatures() {
		RuntimeOptions runtimeOptions = new RuntimeOptions(arguments);
		ResourceLoader resourceLoader = new MultiLoader(Thread.currentThread().getContextClassLoader());
		return runtimeOptions.cucumberFeatures(resourceLoader);
	}
}
