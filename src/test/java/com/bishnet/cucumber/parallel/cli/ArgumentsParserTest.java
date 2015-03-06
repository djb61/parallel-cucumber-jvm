package com.bishnet.cucumber.parallel.cli;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bishnet.cucumber.parallel.runtime.RuntimeConfiguration;

public class ArgumentsParserTest {

	@Test
	public void numberOfThreadsShouldMatchNumberOfProcessorsWhenNotSpecified() {
		List<String> emptyArgList = new ArrayList<String>();
		ArgumentsParser argumentsParser = new ArgumentsParser(emptyArgList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.numberOfThreads).isEqualTo(Runtime.getRuntime().availableProcessors());
	}
	
	@Test
	public void numberOfThreadsCanBeSetByAnArgument() {
		int numberOfThreads = 10;
		List<String> threadsArgList = new ArrayList<String>();
		threadsArgList.add("--num-threads");
		threadsArgList.add(String.valueOf(numberOfThreads));
		ArgumentsParser argumentsParser = new ArgumentsParser(threadsArgList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.numberOfThreads).isEqualTo(numberOfThreads);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgument() {
		String reportPath = "report/myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgumentUsingShortForm() {
		String reportPath = "report/myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportPath.toString()).isEqualTo(reportPath);
	}
	
	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgument() {
		String reportPath = "report/myreport";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("html:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportPath.toString()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgumentUsingShortForm() {
		String reportPath = "report/myreport";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("html:" + reportPath);
		ArgumentsParser argumentsParser = new ArgumentsParser(reportArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportPath.toString()).isEqualTo(reportPath);
	}
	
	@Test
	public void pluginArgumentWhichIsNotJsonOrHtmlShouldBePassedThroughToResultingCucumberArgsList() {
		List<String> pluginArgsList = new ArrayList<String>();
		pluginArgsList.add("--plugin");
		pluginArgsList.add("other");
		int expectedArgCount = pluginArgsList.size();
		ArgumentsParser argumentsParser = new ArgumentsParser(pluginArgsList);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void numberOfThreadsArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--num-threads");
		arguments.add("10");
		arguments.add("--plugin");
		arguments.add("other");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void pluginArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		arguments.add("--snippets");
		arguments.add("asnippet");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void featurePathArgumentsShouldBeRemovedFromCucumberArgsAndAddedToFeaturePaths() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:featurepath");
		arguments.add("/absolute/feature/path");
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
		assertThat(runtimeConfiguration.featurePaths.size()).isEqualTo(2);
	}
	
	@Test
	public void glueArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--glue");
		arguments.add("com.bishnet.glue");
		arguments.add("-g");
		arguments.add("com.bishnet.moreglue");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(4);
	}
	
	@Test
	public void nameArgumentShouldNotBePassedThroughToCucumberArgsListButShouldBePresentInFeatureParseArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--name");
		arguments.add("testname");
		arguments.add("-n");
		arguments.add("anothername");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(0);
		assertThat(runtimeConfiguration.featureParsingArguments.size()).isEqualTo(4);
	}
	
	@Test
	public void tagArgumentShouldNotBePassedThroughToCucumberArgsListButShouldBePresentInFeatureParseArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--tags");
		arguments.add("@testTag");
		arguments.add("-t");
		arguments.add("@anotherTestTag");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(0);
		assertThat(runtimeConfiguration.featureParsingArguments.size()).isEqualTo(4);
	}
	
	@Test
	public void snippetsArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
	}
	
	@Test
	public void internationalisationArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--i18n");
		arguments.add("value");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.cucumberPassthroughArguments.size()).isEqualTo(2);
	}
	
	@Test
	public void formatArgumentShouldBeRemovedFromResultingCucumberArgsList() {
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
	public void isHtmlReportRequiredShouldBeFalseIfNoHtmlArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportRequired).isFalse();
	}
	
	@Test
	public void isJsonReportRequiredShouldBeFalseIfNoJsonArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportRequired).isFalse();
	}
	
	@Test
	public void isHtmlReportRequiredShouldBeTrueIfHtmlArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("html:report");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.htmlReportRequired).isTrue();
	}
	
	@Test
	public void isJsonReportRequiredShouldBeTrueIfJsonArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		ArgumentsParser argumentsParser = new ArgumentsParser(arguments);
		RuntimeConfiguration runtimeConfiguration = argumentsParser.parse();
		assertThat(runtimeConfiguration.jsonReportRequired).isTrue();
	}
}
