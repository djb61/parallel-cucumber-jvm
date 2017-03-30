package com.bishnet.cucumber.parallel.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.bishnet.cucumber.parallel.cli.ArgumentsParser;
import com.bishnet.cucumber.parallel.report.HtmlReportMerger;
import com.bishnet.cucumber.parallel.report.JsonReportMerger;
import com.bishnet.cucumber.parallel.report.RerunReportMerger;
import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;
import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionReporter;

import cucumber.runtime.CucumberException;
import cucumber.runtime.model.CucumberFeature;

public class ParallelRuntime {

	private RuntimeConfiguration runtimeConfiguration;
	private ClassLoader cucumberClassLoader;
	private CucumberBackendFactory cucumberBackendFactory;
	private int triedRerun;

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
			return runWithRerunFailed(features);
		} catch (InterruptedException | IOException e) {
			throw new CucumberException(e);
		}
	}

	private byte runWithRerunFailed(List<CucumberFeature> features) throws IOException, InterruptedException {
		List<Path> rerunFiles = splitFeaturesIntoRerunFiles(features);
		byte result = runFeatures(rerunFiles);
		if (result != 0 && runtimeConfiguration.rerunReportRequired) {
			triedRerun = 1;
			while (result != 0 && triedRerun <= runtimeConfiguration.rerunAttemptsCount) {
				rerunFiles.clear();
				rerunFiles.add(runtimeConfiguration.rerunReportReportPath);
				result = runFeatures(rerunFiles);
				triedRerun++;
			}
		}
		return result;
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

		if (runtimeConfiguration.rerunReportRequired) {
			RerunReportMerger merger = new RerunReportMerger(executor.getRerunReports());
			merger.merge(runtimeConfiguration.rerunReportReportPath);
		}
		if (runtimeConfiguration.jsonReportRequired) {
			JsonReportMerger merger = new JsonReportMerger(executor.getJsonReports());
			if (triedRerun == 0) {
				merger.merge(runtimeConfiguration.jsonReportPath);
			}
			if (result != 0 && triedRerun != runtimeConfiguration.rerunAttemptsCount) {
				merger.mergeRerunFailedReports(runtimeConfiguration.jsonReportPath,
						Paths.get(runtimeConfiguration.flakyReportPath.toString(), "flaky_" + triedRerun + ".json"));
			}
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
