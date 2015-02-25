package com.bishnet.cucumber.parallel.runtime;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import cucumber.runtime.model.CucumberExamples;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenario;
import cucumber.runtime.model.CucumberScenarioOutline;
import cucumber.runtime.model.CucumberTagStatement;

public class RerunFileBuilder {
	
	private Formatter formatter;
	private Reporter reporter;

	public RerunFileBuilder(Formatter formatter, Reporter reporter) {
		/* Both of these should point to a single concrete formatter instance
		 * This is passed twice so we can access both of its interfaces methods
		 * All the cucumber concrete formatters are non-public so can't be used directly
		 */
		this.formatter = formatter;
		this.reporter = reporter;
	}
	
	public void addFeature(CucumberFeature feature) {
		formatter.uri(feature.getPath());
		addFeatureElements(feature);
	}
	
	public void close() {
		formatter.done();
		formatter.close();
	}
	
	private void addFeatureElements(CucumberFeature feature) {
		for (CucumberTagStatement tagStatement : feature.getFeatureElements()) {
			if (tagStatement instanceof CucumberScenarioOutline)
				addScenarioOutline((CucumberScenarioOutline) tagStatement);
			else if (tagStatement instanceof CucumberScenario)
				addScenario((CucumberScenario) tagStatement);
		}
	}
	
	private void addScenario(CucumberScenario cucumberScenario) {
		Scenario scenario = (Scenario) cucumberScenario.getGherkinModel();
		formatter.startOfScenarioLifeCycle(scenario);
		reporter.result(new Result(Result.FAILED, 0L, ""));
		formatter.endOfScenarioLifeCycle(scenario);
	}
	
	private void addScenarioOutline(CucumberScenarioOutline cucumberScenarioOutline) {
		for (CucumberExamples cucumberExamples : cucumberScenarioOutline.getCucumberExamplesList())
			for (CucumberScenario cucumberScenario : cucumberExamples.createExampleScenarios())
				addScenario(cucumberScenario);
	}
}
