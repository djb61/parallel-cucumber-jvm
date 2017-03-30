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
	public final Path threadTimelineReportPath;
	public final Path rerunReportReportPath;
	public final boolean htmlReportRequired;
	public final boolean jsonReportRequired;
	public final boolean threadTimelineReportRequired;
	public final boolean rerunReportRequired;
	public final int rerunAttemptsCount;
	public Path flakyReportPath;

	public RuntimeConfiguration(int numberOfThreads, List<String> cucumberPassThroughArguments,
			List<String> featureParsingArguments, List<String> featurePaths, Path htmlReportPath, boolean htmlReportRequired,
			Path jsonReportPath, boolean jsonReportRequired, Path threadTimelineReportPath,
			boolean threadTimelineReportRequired, Path rerunReportReportPath, boolean rerunReportRequired, int rerunAttemptsCount
			,Path flakyReportPath) {
		this.numberOfThreads = numberOfThreads;
		this.cucumberPassthroughArguments = cucumberPassThroughArguments;
		this.featureParsingArguments = featureParsingArguments;
		this.featurePaths = featurePaths;
		this.htmlReportPath = htmlReportPath;
		this.htmlReportRequired = htmlReportRequired;
		this.jsonReportPath = jsonReportPath;
		this.jsonReportRequired = jsonReportRequired;
		this.threadTimelineReportPath = threadTimelineReportPath;
		this.threadTimelineReportRequired = threadTimelineReportRequired;
		this.rerunReportReportPath = rerunReportReportPath;
		this.rerunReportRequired = rerunReportRequired;
		this.rerunAttemptsCount = rerunAttemptsCount;
		this.flakyReportPath = flakyReportPath;
	}

}
