package com.bishnet.cucumber.parallel.runtime;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import java.util.List;

public class FakeRerunFormatter implements Formatter, Reporter {

	private String uri;
	private int startOfLifecycleCount;
	private int endOfLifecycleCount;
	private int uriCount;
	private int doneCount;
	private int closeCount;
	private Result result;

	@Override
	public void before(Match match, Result result) {
	}

	@Override
	public void result(Result result) {
		this.result = result;
	}

	@Override
	public void after(Match match, Result result) {
	}

	@Override
	public void match(Match match) {
	}

	@Override
	public void embedding(String mimeType, byte[] data) {
	}

	@Override
	public void write(String text) {
	}

	@Override
	public void syntaxError(String state, String event,
			List<String> legalEvents, String uri, Integer line) {
	}

	@Override
	public void uri(String uri) {
		this.uri = uri;
		uriCount++;
	}

	@Override
	public void feature(Feature feature) {
	}

	@Override
	public void scenarioOutline(ScenarioOutline scenarioOutline) {
	}

	@Override
	public void examples(Examples examples) {
	}

	@Override
	public void startOfScenarioLifeCycle(Scenario scenario) {
		startOfLifecycleCount++;
	}

	@Override
	public void background(Background background) {
	}

	@Override
	public void scenario(Scenario scenario) {
	}

	@Override
	public void step(Step step) {
	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		endOfLifecycleCount++;
	}

	@Override
	public void done() {
		doneCount++;
	}

	@Override
	public void close() {
		closeCount++;
	}

	@Override
	public void eof() {
	}

	public String getUri() {
		return uri;
	}
	
	public int getStartOfLifeCycleInvocationCount() {
		return startOfLifecycleCount;
	}
	
	public int getEndOfLifeCycleInvocationCount() {
		return endOfLifecycleCount;
	}
	
	public Result getResult() {
		return result;
	}
	
	public int getUriInvocationCount() {
		return uriCount;
	}
	
	public int getDoneInvocationCount() {
		return doneCount;
	}

	public int getCloseInvocationCount() {
		return closeCount;
	}
}
