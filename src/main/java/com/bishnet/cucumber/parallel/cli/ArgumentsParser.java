package com.bishnet.cucumber.parallel.cli;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsParser {

	private int numThreads = Runtime.getRuntime().availableProcessors();
	
	private List<String> cucumberArgs = new ArrayList<String>();
	
	private String htmlReportPath;
	private String jsonReportPath;

	public ArgumentsParser(List<String> args) {
		parse(args);
	}
	
	public List<String> getCucumberArgs() {
		return cucumberArgs;
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
	
	private void parse(List<String> args) {
		while (!args.isEmpty()) {
			String arg = args.remove(0).trim();
			
			if (arg.equals("--num-threads")) {
				numThreads = Integer.parseInt(args.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p")) {
				String pluginArgs = args.remove(0);
				String[] pluginArgsArray = pluginArgs.split(":");
				if (pluginArgsArray[0].equals("json"))
					jsonReportPath = pluginArgsArray[1];
				else if (pluginArgsArray[0].equals("html"))
					htmlReportPath = pluginArgsArray[1];
				else {
					cucumberArgs.add(arg);
					cucumberArgs.add(pluginArgs);
				}
			} else {
				cucumberArgs.add(arg);
			}
		}
	}
}
