package com.bishnet.cucumber.parallel.runtime;

import java.nio.file.Path;
import java.util.List;

public class RuntimeConfiguration {

	public int numberOfThreads;
	public List<String> cucumberPassthroughArguments;
	public List<String> featureParsingArguments;
	public List<String> featurePaths;
	public Path htmlReportPath;
	public Path jsonReportPath;
	public boolean htmlReportRequired;
	public boolean jsonReportRequired;

}
