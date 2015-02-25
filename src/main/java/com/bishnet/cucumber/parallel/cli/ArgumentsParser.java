package com.bishnet.cucumber.parallel.cli;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsParser {

	private int numThreads = Runtime.getRuntime().availableProcessors();
	
	private List<String> cucumberArgs = new ArrayList<String>();
	
	private String reportPath;

	public ArgumentsParser(List<String> args) {
		parse(args);
	}
	
	public List<String> getCucumberArgs() {
		return cucumberArgs;
	}
	
	public int getNumberOfThreads() {
		return numThreads;
	}
	
	public String getReportPath() {
		return reportPath;
	}
	
	private void parse(List<String> args) {
		while (!args.isEmpty()) {
			String arg = args.remove(0).trim();
			
			if (arg.equals("--num-threads")) {
				numThreads = Integer.parseInt(args.remove(0));
			} else if (arg.equals("--plugin") || arg.equals("-p")) {
				String[] pluginArgs = args.remove(0).split(":");
				if (!pluginArgs[0].equals("json"))
					throw new IllegalArgumentException("The only supported plugin is 'json'");
				reportPath = pluginArgs[1];
			} else {
				cucumberArgs.add(arg);
			}
		}
	}
}
