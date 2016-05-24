package com.bishnet.cucumber.parallel.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.bishnet.cucumber.parallel.cli.ArgumentsParser;
import com.bishnet.cucumber.parallel.report.HtmlReportMerger;
import com.bishnet.cucumber.parallel.report.JsonReportMerger;
import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;
import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionReporter;

import cucumber.runtime.CucumberException;
import cucumber.runtime.model.CucumberFeature;

public class ParallelRuntime {

	private RuntimeConfiguration runtimeConfiguration;
	private ClassLoader cucumberClassLoader;
	private CucumberBackendFactory cucumberBackendFactory;

	public ParallelRuntime(List<String> arguments) {
		this(arguments, Thread.currentThread().getContextClassLoader());
	}

	public ParallelRuntime(List<String> arguments, ClassLoader cucumberClassLoader) {
		this(arguments, cucumberClassLoader, null);
	}

	public ParallelRuntime(List<String> arguments, CucumberBackendFactory cucumberBackendFactory) {
		this(arguments, Thread.currentThread().getContextClassLoader(), cucumberBackendFactory);
	}

	public ParallelRuntime(List<String> arguments, ClassLoader cucumberClassLoader, CucumberBackendFactory cucumberBackendFactory) {
		this.cucumberClassLoader = cucumberClassLoader;
		this.cucumberBackendFactory = cucumberBackendFactory;
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		runtimeConfiguration = argumentsParser.parse();
	}

	public byte run() {
		List<CucumberFeature> features = parseFeatures();
		if (features.isEmpty())
			return 0;
		try {
			List<Path> rerunFiles = splitFeaturesIntoRerunFiles(features);
			return runFeatures(rerunFiles);
		} catch (InterruptedException | IOException e) {
			throw new CucumberException(e);
		}
	}

	private List<CucumberFeature> parseFeatures() {
		FeatureParser featureParser = new FeatureParser(runtimeConfiguration, cucumberClassLoader);
		return featureParser.parseFeatures();
	}

	private List<Path> splitFeaturesIntoRerunFiles(List<CucumberFeature> features) throws IOException {
		FeatureSplitter featureSplitter = new FeatureSplitter(runtimeConfiguration, features);
		return featureSplitter.splitFeaturesIntoRerunFiles();
	}

	private byte runFeatures(List<Path> rerunFiles) throws InterruptedException, IOException {

		CucumberRuntimeFactory runtimeFactory = null;
		ThreadExecutionRecorder threadExecutionRecorder = null;

		if (runtimeConfiguration.threadTimelineReportRequired)
			threadExecutionRecorder = new ThreadExecutionRecorder();

		runtimeFactory = new CucumberRuntimeFactory(runtimeConfiguration, cucumberBackendFactory, cucumberClassLoader, threadExecutionRecorder);

		CucumberRuntimeExecutor executor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles, runtimeConfiguration);

		byte result = executor.run();

		if (runtimeConfiguration.jsonReportRequired) {
			JsonReportMerger merger = new JsonReportMerger(executor.getJsonReports());
			merger.merge(runtimeConfiguration.jsonReportPath);
		}
		if (runtimeConfiguration.htmlReportRequired) {
			HtmlReportMerger merger = new HtmlReportMerger(executor.getHtmlReports());
			merger.merge(runtimeConfiguration.htmlReportPath);
		}
		if (runtimeConfiguration.threadTimelineReportRequired) {
			ThreadExecutionReporter threadExecutionReporter = new ThreadExecutionReporter();
			threadExecutionReporter.writeReport(threadExecutionRecorder.getRecordedData(), runtimeConfiguration.threadTimelineReportPath);
		}
		
		
		return result;
	}
}
