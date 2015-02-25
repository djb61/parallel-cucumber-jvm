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
		assertThat(options.getReportPath()).isEqualTo(reportPath);
	}

	@Test
	public void finalReportPathIsParsedFromJsonPluginArgumentUsingShortForm() {
		String reportPath = "report/myreport.json";
		List<String> reportArgsList = new ArrayList<String>();
		reportArgsList.add("-p");
		reportArgsList.add("json:" + reportPath);
		ArgumentsParser options = new ArgumentsParser(reportArgsList);
		assertThat(options.getReportPath()).isEqualTo(reportPath);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pluginArgumentWhichIsNotJsonShouldThrowAnException() {
		List<String> pluginArgsList = new ArrayList<String>();
		pluginArgsList.add("--plugin");
		pluginArgsList.add("other");
		new ArgumentsParser(pluginArgsList);
	}
	
	@Test
	public void numberOfThreadsArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--num-threads");
		arguments.add("10");
		arguments.add("some");
		arguments.add("other");
		arguments.add("args");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(expectedArgCount);
	}
	
	@Test
	public void pluginArgumentShouldBeRemovedFromResultingCucumberArgsList() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("--plugin");
		arguments.add("json:report.json");
		arguments.add("some");
		arguments.add("other");
		arguments.add("args");
		int expectedArgCount = arguments.size() - 2;
		ArgumentsParser options = new ArgumentsParser(arguments);
		assertThat(options.getCucumberArgs().size()).isEqualTo(expectedArgCount);
	}
}
