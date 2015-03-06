package com.bishnet.cucumber.parallel.cli;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bishnet.cucumber.parallel.runtime.RuntimeConfiguration;

public class ArgumentsParser {

	private List<String> arguments;
	
	public ArgumentsParser(List<String> arguments) {
		this.arguments = arguments;
	}
	
	public RuntimeConfiguration parse() {
		List<String> cucumberArgs = new ArrayList<String>();
		List<String> featureParseOnlyArgs = new ArrayList<String>();
		List<String> featurePaths = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration();
		runtimeConfiguration.numberOfThreads = Runtime.getRuntime().availableProcessors();
		List<String> parseArguments = new ArrayList<String>(arguments);
		while (!parseArguments.isEmpty()) {
			String arg = parseArguments.remove(0).trim();
			
			if (arg.equals("--num-threads")) {
				runtimeConfiguration.numberOfThreads = Integer.parseInt(parseArguments.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p") || arg.equals("--format") || arg.equals("-f")) {
				String pluginValue = parseArguments.remove(0);
				String[] pluginArgsArray = pluginValue.split(":");
				if (pluginArgsArray[0].equals("json")) {
					runtimeConfiguration.jsonReportRequired = true;
					runtimeConfiguration.jsonReportPath = Paths.get(pluginArgsArray[1]);
				} else if (pluginArgsArray[0].equals("html")) {
					runtimeConfiguration.htmlReportRequired = true;
					runtimeConfiguration.htmlReportPath = Paths.get(pluginArgsArray[1]);
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
			}
			else {
				featurePaths.add(arg);
			}
		}
		List<String> fullFeatureParsingArguments = new ArrayList<String>();
		fullFeatureParsingArguments.addAll(cucumberArgs);
		fullFeatureParsingArguments.addAll(featureParseOnlyArgs);
		fullFeatureParsingArguments.addAll(featurePaths);
		runtimeConfiguration.featureParsingArguments = fullFeatureParsingArguments;
		runtimeConfiguration.featurePaths = featurePaths;
		runtimeConfiguration.cucumberPassthroughArguments = cucumberArgs;
		return runtimeConfiguration;
	}
}
