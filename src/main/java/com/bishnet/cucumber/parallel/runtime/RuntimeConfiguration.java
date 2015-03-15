package com.bishnet.cucumber.parallel.runtime;

import java.nio.file.Path;
import java.util.List;

public class RuntimeConfiguration {

	public final int numberOfThreads;
	public final List<String> cucumberPassthroughArguments;
	public final List<String> featureParsingArguments;
	public final List<String> featurePaths;
	public final Path htmlReportPath;
	public final Path jsonReportPath;
	public final boolean htmlReportRequired;
	public final boolean jsonReportRequired;

	public RuntimeConfiguration(int numberOfThreads, List<String> cucumberPassThroughArguments,
			List<String> featureParsingArguments, List<String> featurePaths, Path htmlReportPath, boolean htmlReportRequired,
			Path jsonReportPath, boolean jsonReportRequired) {
		this.numberOfThreads = numberOfThreads;
		this.cucumberPassthroughArguments = cucumberPassThroughArguments;
		this.featureParsingArguments = featureParsingArguments;
		this.featurePaths = featurePaths;
		this.htmlReportPath = htmlReportPath;
		this.htmlReportRequired = htmlReportRequired;
		this.jsonReportPath = jsonReportPath;
		this.jsonReportRequired = jsonReportRequired;
	}

}
