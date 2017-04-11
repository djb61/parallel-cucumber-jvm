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
import com.bishnet.cucumber.parallel.util.RerunUtils;
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
		try {
			runtimeConfiguration = argumentsParser.parse();
		}
		catch (IOException e) {
			throw new CucumberException(e);
		}
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
		if (rerunFiles.isEmpty()) {
			System.out.println(
					String.format("None of the features or scenarios at %s matched the filters, or no scenarios found.",
							runtimeConfiguration.featurePaths));
			return 0;
		}
		byte result = runFeatures(rerunFiles);
		if (result != 0 && runtimeConfiguration.flakyAttemptsCount > 0) {
			result = rerunFlakyTests(rerunFiles, result);
		}
		return result;
	}

	private byte rerunFlakyTests(List<Path> rerunFiles, byte result) throws IOException, InterruptedException {
		int failedCount = RerunUtils.countScenariosInRerunFile(runtimeConfiguration.rerunReportReportPath);
		if (failedCount > runtimeConfiguration.flakyMaxCount) {
			System.out.println(
					String.format("%d TESTS FAILED - MORE THEN ALLOWED FOR RERUN (%d)! Aborting rerun flaky.",
							failedCount, runtimeConfiguration.flakyMaxCount));
			return result;
		}
		System.out.println(String.format(
				"RERUN FLAKY TESTS STARTED. WILL TRY FOR %d ATTEMPT(S).", runtimeConfiguration.flakyAttemptsCount));
		triedRerun = 1;
		while (result != 0 && triedRerun <= runtimeConfiguration.flakyAttemptsCount) {
			rerunFiles.clear();
			rerunFiles.add(runtimeConfiguration.rerunReportReportPath);
			result = runFeatures(rerunFiles);
			System.out.println(String.format("RERUN FLAKY TESTS ATTEMPT #%d FINISHED.", triedRerun++));
		}
		System.out.println(String.format(
				"RERUN FLAKY TESTS FINISHED. TRIED FOR %d ATTEMPT(S).", triedRerun));
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

        if (triedRerun == 0) {
            if (runtimeConfiguration.rerunReportRequired) {
                RerunReportMerger merger = new RerunReportMerger(executor.getRerunReports());
                merger.merge(runtimeConfiguration.rerunReportReportPath);
            }
            if (runtimeConfiguration.jsonReportRequired) {
                JsonReportMerger merger = new JsonReportMerger(executor.getJsonReports());
                merger.merge(runtimeConfiguration.jsonReportPath);
            }
            if (runtimeConfiguration.htmlReportRequired) {
                HtmlReportMerger merger = new HtmlReportMerger(executor.getHtmlReports());
                merger.merge(runtimeConfiguration.htmlReportPath);
            }
        } else {
            RerunReportMerger merger = new RerunReportMerger(executor.getRerunReports());
            merger.merge(runtimeConfiguration.rerunReportReportPath);
            JsonReportMerger jsonMerger = new JsonReportMerger(executor.getJsonReports());
            jsonMerger.mergeRerunFailedReports(runtimeConfiguration.jsonReportPath,
                    Paths.get(runtimeConfiguration.flakyReportPath.toString(), "flaky_" + triedRerun + ".json"));
        }
        if (runtimeConfiguration.threadTimelineReportRequired) {
			ThreadExecutionReporter threadExecutionReporter = new ThreadExecutionReporter();
			threadExecutionReporter.writeReport(threadExecutionRecorder.getRecordedData(), runtimeConfiguration.threadTimelineReportPath);
		}


		return result;
	}
}
