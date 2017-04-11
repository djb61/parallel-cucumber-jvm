package com.bishnet.cucumber.parallel.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bishnet.cucumber.parallel.runtime.RuntimeConfiguration;

public class ArgumentsParser {

	private List<String> arguments;

	public ArgumentsParser(List<String> arguments) {
		this.arguments = arguments;
	}

	public RuntimeConfiguration parse() throws IOException {
		List<String> cucumberArgs = new ArrayList<String>();
		List<String> featureParseOnlyArgs = new ArrayList<String>();
		List<String> featurePaths = new ArrayList<String>();
		List<String> parseArguments = new ArrayList<String>(arguments);
		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		boolean jsonReportRequired = false;
		Path jsonReportPath = null;
		boolean htmlReportRequired = false;
		Path htmlReportPath = null;
		boolean threadTimelineReportRequired = false;
		Path threadTimelineReportPath = null;
		boolean rerunReportRequired = false;
		Path rerunReportReportPath = null;
		int flakyAttemptsCount = 0;
		Path flakyReportPath = null;
		int flakyMaxCount = 10;

		while (!parseArguments.isEmpty()) {
			String arg = parseArguments.remove(0).trim();

			if (arg.equals("--num-threads")) {
				numberOfThreads = Integer.parseInt(parseArguments.remove(0));
			} else if (arg.equals("--flaky-rerun-attemptsCount")) {
				flakyAttemptsCount = Integer.parseInt(parseArguments.remove(0));
			} else if (arg.equals("--flaky-rerun-reportDir")) {
				flakyReportPath = Paths.get(parseArguments.remove(0));
			} else if (arg.equals("--flaky-rerun-threshold")) {
				flakyMaxCount = Integer.parseInt(parseArguments.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p") || arg.equals("--format") || arg.equals("-f")) {
				String pluginValue = parseArguments.remove(0);
				String[] pluginArgsArray = pluginValue.split(":", 2);
				if (pluginArgsArray[0].equals("json")) {
					jsonReportRequired = true;
					jsonReportPath = Paths.get(pluginArgsArray[1]);
				} else if (pluginArgsArray[0].equals("html")) {
					htmlReportRequired = true;
					htmlReportPath = Paths.get(pluginArgsArray[1]);
				} else if (pluginArgsArray[0].equals("thread-report")) {
					threadTimelineReportRequired = true;
					threadTimelineReportPath = Paths.get(pluginArgsArray[1]);
				} else if (pluginArgsArray[0].equals("rerun")) {
					rerunReportRequired = true;
					rerunReportReportPath = Paths.get(pluginArgsArray[1]);
				} else {
					cucumberArgs.add(arg);
					cucumberArgs.add(pluginValue);
				}
			} else if (arg.equals("--glue") || arg.equals("-g")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(parseArguments.remove(0));
			} else if (arg.equals("--tags") || arg.equals("-t")) {
				featureParseOnlyArgs.add(arg);
				featureParseOnlyArgs.add(parseArguments.remove(0));
			} else if (arg.equals("--i18n")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(parseArguments.remove(0));
			} else if (arg.equals("--snippets")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(parseArguments.remove(0));
			} else if (arg.equals("--name") || arg.equals("-n")) {
				featureParseOnlyArgs.add(arg);
				featureParseOnlyArgs.add(parseArguments.remove(0));
			} else if (arg.startsWith("-")) {
				cucumberArgs.add(arg);
			} else {
				featurePaths.add(arg);
			}
		}
		List<String> fullFeatureParsingArguments = new ArrayList<String>();
		fullFeatureParsingArguments.addAll(cucumberArgs);
		fullFeatureParsingArguments.addAll(featureParseOnlyArgs);
		fullFeatureParsingArguments.addAll(featurePaths);
		if (flakyAttemptsCount > 0) {
			if (!jsonReportRequired) {
				jsonReportRequired = true;
				jsonReportPath = getTempJsonPath();
			}
			if (!rerunReportRequired) {
				rerunReportRequired = true;
				rerunReportReportPath = getTempRerunPath();
			}
			if (flakyReportPath == null) {
				flakyReportPath = jsonReportPath.getParent();
			}
		}
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(numberOfThreads,
				Collections.unmodifiableList(cucumberArgs), Collections.unmodifiableList(fullFeatureParsingArguments),
				Collections.unmodifiableList(featurePaths), htmlReportPath, htmlReportRequired, jsonReportPath,
				jsonReportRequired, threadTimelineReportPath, threadTimelineReportRequired, rerunReportReportPath,
				rerunReportRequired, flakyAttemptsCount, flakyReportPath, flakyMaxCount);
		return runtimeConfiguration;
	}

	private Path getTempRerunPath() throws IOException {
		Path rerunReportReportPath = Files.createTempFile("parallelCukesTmp", ".rerun");
		rerunReportReportPath.toFile().deleteOnExit();
		return rerunReportReportPath;
	}

	private Path getTempJsonPath() throws IOException {
		Path jsonReportPath = Files.createTempFile("parallelCukesTmp", ".json");
		jsonReportPath.toFile().deleteOnExit();
		return jsonReportPath;
	}
}
