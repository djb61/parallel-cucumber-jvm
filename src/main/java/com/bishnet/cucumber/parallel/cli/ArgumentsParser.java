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
		List<String> featurePaths = new ArrayList<String>();
		RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration();
		runtimeConfiguration.numThreads = Runtime.getRuntime().availableProcessors();
		while (!arguments.isEmpty()) {
			String arg = arguments.remove(0).trim();
			
			if (arg.equals("--num-threads")) {
				runtimeConfiguration.numThreads = Integer.parseInt(arguments.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p") || arg.equals("--format") || arg.equals("-f")) {
				String pluginValue = arguments.remove(0);
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
				cucumberArgs.add(arguments.remove(0));
			} else if (arg.equals("--tags") || arg.equals("-t")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(arguments.remove(0));
			} else if (arg.equals("--i18n")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(arguments.remove(0));
			} else if (arg.equals("--snippets")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(arguments.remove(0));
			} else if (arg.equals("--name") || arg.equals("-n")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(arguments.remove(0));
			} else if (arg.startsWith("-")) {
				cucumberArgs.add(arg);
			}
			else {
				featurePaths.add(arg);
			}
		}
		runtimeConfiguration.cucumberArgs = cucumberArgs;
		runtimeConfiguration.featurePaths = featurePaths;
		return runtimeConfiguration;
	}
}
