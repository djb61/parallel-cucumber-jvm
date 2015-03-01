package com.bishnet.cucumber.parallel.runtime;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.model.CucumberFeature;

public class FeatureSplitter {

	private List<CucumberFeature> features;
	private int numThreads;
	
	public FeatureSplitter(List<CucumberFeature> features, int numThreads) {
		this.features = features;
		this.numThreads = numThreads;
	}
	
	public List<Path> splitFeaturesIntoRerunFiles() throws IOException {
		List<Path> rerunPaths = new ArrayList<Path>();
		int featuresPerThread = features.size() / numThreads;
		if (features.size() % numThreads > 0)
			featuresPerThread = featuresPerThread + 1;
		List<List<CucumberFeature>> partitionedFeatures = ListUtils.partition(features, featuresPerThread);
		
		for (List<CucumberFeature> threadFeatures : partitionedFeatures) {
			rerunPaths.add(createSingleRerunFile(threadFeatures));
		}
		return rerunPaths;
	}
	
	private Path createSingleRerunFile(List<CucumberFeature> rerunFeatures) throws IOException {
		Path rerunPath = Files.createTempFile("parallelCukes", ".rerun");
		rerunPath.toFile().deleteOnExit();
		PluginFactory pluginFactory = new PluginFactory();
		Object rerunFormatter = pluginFactory.create("rerun:" + rerunPath);
		RerunFileBuilder rerunFileBuilder = new RerunFileBuilder((Formatter) rerunFormatter, (Reporter) rerunFormatter);
		for (CucumberFeature feature : rerunFeatures)
			rerunFileBuilder.addFeature(feature);
		rerunFileBuilder.close();
		return rerunPath;
	}
}
