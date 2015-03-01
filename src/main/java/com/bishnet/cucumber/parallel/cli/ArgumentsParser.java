package com.bishnet.cucumber.parallel.cli;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsParser {

	private int numThreads = Runtime.getRuntime().availableProcessors();
	
	private List<String> cucumberArgs = new ArrayList<String>();
	private List<String> featurePaths = new ArrayList<String>();
	
	private String htmlReportPath;
	private String jsonReportPath;

	public ArgumentsParser(List<String> args) {
		parse(args);
	}
	
	public List<String> getCucumberArgs() {
		return cucumberArgs;
	}
	
	public List<String> getFeaturePaths() {
		return featurePaths;
	}
	
	public int getNumberOfThreads() {
		return numThreads;
	}
	
	public String getJsonReportPath() {
		return jsonReportPath;
	}
	
	public String getHtmlReportPath() {
		return htmlReportPath;
	}
	
	public boolean isJsonReportRequired() {
		return jsonReportPath != null;
	}
	
	public boolean isHtmlReportRequired() {
		return htmlReportPath != null;
	}
	
	private void parse(List<String> args) {
		while (!args.isEmpty()) {
			String arg = args.remove(0).trim();
			
			if (arg.equals("--num-threads")) {
				numThreads = Integer.parseInt(args.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p")) {
				parsePluginArgument(args.remove(0));
			} else if (arg.equals("--format") || arg.equals("-f")) {
				System.err.println("WARNING: Cucumber-JVM's --format option is deprecated. Please use --plugin instead.");
				parsePluginArgument(args.remove(0));
			} else if (arg.equals("--glue") || arg.equals("-g")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(args.remove(0));
			} else if (arg.equals("--tags") || arg.equals("-t")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(args.remove(0));
			} else if (arg.equals("--i18n")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(args.remove(0));
			} else if (arg.equals("--snippets")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(args.remove(0));
			} else if (arg.equals("--name") || arg.equals("-n")) {
				cucumberArgs.add(arg);
				cucumberArgs.add(args.remove(0));
			} else if (arg.startsWith("-")) {
				cucumberArgs.add(arg);
			}
			else {
				featurePaths.add(arg);
			}
		}
	}
	
	private void parsePluginArgument(String pluginValue) {
		String[] pluginArgsArray = pluginValue.split(":");
		if (pluginArgsArray[0].equals("json"))
			jsonReportPath = pluginArgsArray[1];
		else if (pluginArgsArray[0].equals("html"))
			htmlReportPath = pluginArgsArray[1];
		else {
			cucumberArgs.add("--plugin");
			cucumberArgs.add(pluginValue);
		}
	}
}
