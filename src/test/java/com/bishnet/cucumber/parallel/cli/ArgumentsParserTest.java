package com.bishnet.cucumber.parallel.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bishnet.cucumber.parallel.runtime.RuntimeConfiguration;

public class ArgumentsParserTest {

	private static final String REPORT_MYREPORT_JSON = "report" + File.pathSeparatorChar + "myreport.json";
	private static final String REPORT_MYREPORT = "report" + File.pathSeparatorChar + "myreport";
	private static final String REPORT_THREADREPORT = "report" + File.pathSeparatorChar + "threadreportdir";
	private static final String REPORT_RERUNREPORT = "report" + File.pathSeparatorChar + "rerunReport.rerun";
	private static final String REPORT_FLAKY = "report" + File.pathSeparatorChar + "someflakyreportdir";

	@Test
	public void numberOfThreadsShouldMatchNumberOfProcessorsWhenNotSpecified() throws IOException {
		List<String> emptyArgList = new ArrayList<String>();
		ArgumentsParser argumentsParser = new ArgumentsParser(emptyArgList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.numberOfThreads).isEqualTo(Runtime.getRuntime().availableProcessors());
	}

	@Test
	public void numberOfThreadsCanBeSetByAnArgument() throws IOException {
		int numberOfThreads = 10;
		List<String> threadsArgList = new ArrayList<String>();
		threadsArgList.add("--num-threads");
		threadsArgList.add(String.valueOf(numberOfThreads));
		ArgumentsParser argumentsParser = new ArgumentsParser(threadsArgList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.numberOfThreads).isEqualTo(numberOfThreads);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgument() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("json:" + REPORT_MYREPORT_JSON);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).isEqualTo(REPORT_MYREPORT_JSON);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgumentUsingShortForm() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("json:" + REPORT_MYREPORT_JSON);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).isEqualTo(REPORT_MYREPORT_JSON);
	}

	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgument() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("html:" + REPORT_MYREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportPath.toString()).isEqualTo(REPORT_MYREPORT);
	}

	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgumentUsingShortForm() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("html:" + REPORT_MYREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportPath.toString()).isEqualTo(REPORT_MYREPORT);
	}

	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgumentsWhenAbsoluteWindowsPathIsProvided() throws IOException {
		String reportPath = "c:\\some\\path\\myreport";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("html:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportPath.toString()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgumentsWhenAbsoluteWindowsPathIsProvided() throws IOException {
		String reportPath = "c:\\some\\path\\myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).isEqualTo(reportPath);
	}

	@Test
	public void pluginArgumentWhichIsNotJsonOrHtmlShouldBePassedThroughToResultingCucumberArgsList() throws IOException {
		List<String> pluginArgsList = new ArrayList<String>();
		pluginArgsList.add("--plugin");
		pluginArgsList.add("other");
		ArgumentsParser argumentsParser = new ArgumentsParser(pluginArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).contains("--plugin", "other");
	}

	@Test
	public void numberOfThreadsArgumentShouldBeRemovedFromResultingCucumberArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--num-threads");
		arguments.add("10");
		arguments.add("--plugin");
		arguments.add("other");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).doesNotContain("--num-threads", "10");
	}

	@Test
	public void pluginArgumentShouldBeRemovedFromResultingCucumberArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).doesNotContain("--plugin", "json:report.json");
	}

	@Test
	public void featurePathArgumentsShouldBeRemovedFromCucumberArgsAndAddedToFeaturePaths() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:featurepath");
		arguments.add("/absolute/feature/path");
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).doesNotContain("classpath:featurepath",
				"/absolute/feature/path");
		assertThat(runtimeConfiguration.featurePaths.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.featurePaths).contains("classpath:featurepath", "/absolute/feature/path");
	}

	@Test
	public void glueArgumentShouldBePassedThroughToCucumberArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--glue");
		arguments.add("com.bishnet.glue");
		arguments.add("-g");
		arguments.add("com.bishnet.moreglue");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(4);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).contains("--glue", "com.bishnet.glue", "-g",
				"com.bishnet.moreglue");
	}

	@Test
	public void nameArgumentShouldNotBePassedThroughToCucumberArgsListButShouldBePresentInFeatureParseArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--name");
		arguments.add("testname");
		arguments.add("-n");
		arguments.add("anothername");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(0);
		assertThat(runtimeConfiguration.featureParsingArguments.size()).isEqualTo(4);
		assertThat(runtimeConfiguration.featureParsingArguments).contains("--name", "testname", "-n", "anothername");
	}

	@Test
	public void tagArgumentShouldNotBePassedThroughToCucumberArgsListButShouldBePresentInFeatureParseArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--tags");
		arguments.add("@testTag");
		arguments.add("-t");
		arguments.add("@anotherTestTag");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(0);
		assertThat(runtimeConfiguration.featureParsingArguments.size()).isEqualTo(4);
		assertThat(runtimeConfiguration.featureParsingArguments).contains("--tags", "@testTag", "-t", "@anotherTestTag");
	}

	@Test
	public void snippetsArgumentShouldBePassedThroughToCucumberArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).contains("--snippets", "asnippet");
	}

	@Test
	public void internationalisationArgumentShouldBePassedThroughToCucumberArgsList() throws IOException  {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--i18n");
		arguments.add("value");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.cucumberPassthroughArguments).contains("--i18n", "value");
	}

	@Test
	public void formatArgumentShouldBeRemovedFromResultingCucumberArgsList() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--format");
		arguments.add("json:report.json");
		arguments.add("-f");
		arguments.add("html:reportHtml");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(0);
	}

	@Test
	public void isHtmlReportRequiredShouldBeFalseIfNoHtmlArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportRequired).isFalse();
	}

	@Test
	public void isJsonReportRequiredShouldBeFalseIfNoJsonArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportRequired).isFalse();
	}

	@Test
	public void isHtmlReportRequiredShouldBeTrueIfHtmlArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("html:report");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportRequired).isTrue();
	}

	@Test
	public void isJsonReportRequiredShouldBeTrueIfJsonArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportRequired).isTrue();
	}

	@Test
	public void isThreadReportRequiredShouldBeTrueIfJsonArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("thread-report:threadReportFolder");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.threadTimelineReportRequired).isTrue();
	}

	@Test
	public void isRerunReportRequiredShouldBeTrueIfRerunArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("rerun:" + REPORT_RERUNREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.rerunReportRequired).isTrue();
	}

	@Test
	public void isJsonReportRequiredSetTrueAndTempJsonReportPathSetIfFlakyRerunArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportRequired).isTrue();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).contains("parallelCukesTmp");
		assertThat(runtimeConfiguration.jsonReportPath.endsWith(".json"));
	}

	@Test
	public void isRerunReportRequiredSetTrueTempRerunReportPathSetIfFlakyRerunArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.rerunReportRequired).isTrue();
		assertThat(runtimeConfiguration.rerunReportReportPath.toString()).contains("parallelCukesTmp");
		assertThat(runtimeConfiguration.rerunReportReportPath.endsWith(".rerun"));
	}

	@Test
	public void isFlakyReportPathSetToJsonReportParentSetIfFlakyRerunArgumentWasPassedWithoutPath() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		Path jsonReportParent = runtimeConfiguration.jsonReportPath.getParent();
		assertThat(runtimeConfiguration.flakyReportPath).isEqualTo(jsonReportParent);
	}

	@Test
	public void flakyMaxCountSetToDefaultIfFlakyRerunArgumentsWasPassedWithoutflakyMaxCount() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.flakyMaxCount).isEqualTo(10);
	}

	@Test
	public void isRealJsonReportPathSetIfFlakyRerunAndJsonArgumentsWasPassedTogether() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		arguments.add("--plugin");
		arguments.add("json:report.json");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).endsWith("report.json");
		assertThat(runtimeConfiguration.jsonReportPath.toString()).doesNotContain("parallelCukesTmp");
		assertThat(runtimeConfiguration.jsonReportPath.endsWith(".json"));
	}


	@Test
	public void isRealRerunReportPathSetIfFlakyRerunAndJsonArgumentsWasPassedTogether()	throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		arguments.add("--plugin");
		arguments.add("rerun:" + REPORT_RERUNREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.rerunReportReportPath.toString()).endsWith(REPORT_RERUNREPORT);
		assertThat(runtimeConfiguration.rerunReportReportPath.toString()).doesNotContain("parallelCukesTmp");
	}

	@Test
	public void flakyRerunAttemptsCountParsedCorrectlyFlakyPathSetIfOnlyFlakyRerunArgumentWasPassed() throws
			IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-attemptsCount");
		arguments.add("3");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.flakyAttemptsCount).isEqualTo(3);
	}

	@Test
	public void flakyRerunReportDirParsedCorrectlyIfFlakyRerunArgumentWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-reportDir");
		arguments.add(REPORT_FLAKY);
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.flakyReportPath.toString()).isEqualTo(REPORT_FLAKY);
	}

	@Test
	public void flakyMaxCountParsedCorrectlyIfFlakyRerunArgumentsWasPassed() throws IOException {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--flaky-rerun-threshold");
		arguments.add("5");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.flakyMaxCount).isEqualTo(5);
	}
	
	@Test
	public void finalReportPathIsParsedFromRerunPluginArgument() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("rerun:" + REPORT_RERUNREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.rerunReportReportPath.toString()).isEqualTo(REPORT_RERUNREPORT);
	}

	@Test
	public void finalReportPathIsParsedFromThreadReportPluginArgument() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("thread-report:" + REPORT_THREADREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.threadTimelineReportPath.toString()).isEqualTo(REPORT_THREADREPORT);
	}

	@Test
	public void finalReportPathIsParsedFromThreadReportPluginArgumentUsingShortForm() throws IOException {
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("thread-report:" + REPORT_THREADREPORT);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.threadTimelineReportPath.toString()).isEqualTo(REPORT_THREADREPORT);
	}

}
