package com.bishnet.cucumber.parallel.cli;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bishnet.cucumber.parallel.cli.ArgumentsParser;

public class ArgumentsParserTest {

	@Test
	public void numberOfThreadsShouldMatchNumberOfProcessorsWhenNotSpecified() {
		List<String> emptyArgList = new ArrayList<String>();
		ArgumentsParser options = new ArgumentsParser(emptyArgList);
		assertThat(options.getNumberOfThreads()).isEqualTo(Runtime.getRuntime().availableProcessors());
	}
	
	@Test
	public void numberOfThreadsCanBeSetByAnArgument() {
		int numberOfThreads = 10;
		List<String> threadsArgList = new ArrayList<String>();
		threadsArgList.add("--num-threads");
		threadsArgList.add(String.valueOf(numberOfThreads));
		ArgumentsParser options = new ArgumentsParser(threadsArgList);
		assertThat(options.getNumberOfThreads()).isEqualTo(numberOfThreads);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgument() {
		String reportPath = "report/myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser options = new ArgumentsParser(reportArgsList);
		assertThat(options.getJsonReportPath().toString()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgumentUsingShortForm() {
		String reportPath = "report/myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser options = new ArgumentsParser(reportArgsList);
		assertThat(options.getJsonReportPath().toString()).isEqualTo(reportPath);
	}
	
	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgument() {
		String reportPath = "report/myreport";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("--plugin");
		reportArgsList.add("html:" + reportPath);
		ArgumentsParser options = new ArgumentsParser(reportArgsList);
		assertThat(options.getHtmlReportPath().toString()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromHtmlPluginArgumentUsingShortForm() {
		String reportPath = "report/myreport";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("html:" + reportPath);
		ArgumentsParser options = new ArgumentsParser(reportArgsList);
		assertThat(options.getHtmlReportPath().toString()).isEqualTo(reportPath);
	}
	
	@Test
	public void pluginArgumentWhichIsNotJsonOrHtmlShouldBePassedThroughToResultingCucumberArgsList() {
		List<String> pluginArgsList = new ArrayList<String>();
		pluginArgsList.add("--plugin");
		pluginArgsList.add("other");
		int expectedArgCount = pluginArgsList.size();
		ArgumentsParser options = new ArgumentsParser(pluginArgsList);
		assertThat(options.getCucumberArgs().size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void numberOfThreadsArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--num-threads");
		arguments.add("10");
		arguments.add("--plugin");
		arguments.add("other");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void pluginArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		arguments.add("--tags");
		arguments.add("@other");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void featurePathArgumentsShouldBeRemovedFromCucumberArgsAndAddedToFeaturePaths() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("classpath:featurepath");
		arguments.add("/absolute/feature/path");
		arguments.add("--tags");
		arguments.add("@other");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(2);
		assertThat(options.getFeaturePaths().size()).isEqualTo(2);
	}
	
	@Test
	public void glueArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--glue");
		arguments.add("com.bishnet.glue");
		arguments.add("-g");
		arguments.add("com.bishnet.moreglue");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(4);
	}
	
	@Test
	public void nameArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--name");
		arguments.add("testname");
		arguments.add("-n");
		arguments.add("anothername");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(4);
	}
	
	@Test
	public void snippetsArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--snippets");
		arguments.add("asnippet");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(2);
	}
	
	@Test
	public void internationalisationArgumentShouldBePassedThroughToCucumberArgsList(){
		List<String> arguments = new ArrayList<String>();
		arguments.add("--i18n");
		arguments.add("value");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(2);
	}
	
	@Test
	public void formatArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--format");
		arguments.add("json:report.json");
		arguments.add("-f");
		arguments.add("html:reportHtml");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(0);
	}
	
	@Test
	public void isHtmlReportRequiredShouldBeFalseIfNoHtmlArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.isHtmlReportRequired()).isFalse();
	}
	
	@Test
	public void isJsonReportRequiredShouldBeFalseIfNoJsonArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--strict");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.isJsonReportRequired()).isFalse();
	}
	
	@Test
	public void isHtmlReportRequiredShouldBeTrueIfHtmlArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("html:report");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.isHtmlReportRequired()).isTrue();
	}
	
	@Test
	public void isJsonReportRequiredShouldBeTrueIfJsonArgumentWasPassed() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.isJsonReportRequired()).isTrue();
	}
}
