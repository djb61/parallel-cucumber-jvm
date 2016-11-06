package com.bishnet.cucumber.parallel.runtime;

import java.util.Collection;
import java.util.Set;

import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;
import com.bishnet.cucumber.parallel.report.thread.ThreadTimelineData;

import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

public class ThreadLoggedRuntime extends Runtime {

	private ThreadTimelineData threadTimelineData;
	private ThreadExecutionRecorder threadExecutionRecorder;

	public ThreadLoggedRuntime(ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader,
			RuntimeOptions runtimeOptions, ThreadExecutionRecorder threadExecutionRecorder) {
		super(resourceLoader, classFinder, classLoader, runtimeOptions);
		this.threadExecutionRecorder = threadExecutionRecorder;
	}

	public ThreadLoggedRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
			Collection<? extends Backend> backends, RuntimeOptions runtimeOptions,
			ThreadExecutionRecorder threadExecutionRecorder) {
		super(resourceLoader, classLoader, backends, runtimeOptions);
		this.threadExecutionRecorder = threadExecutionRecorder;
	}

	@Override
	public void buildBackendWorlds(Reporter reporter, Set<Tag> tags, Scenario gherkinScenario) {
		super.buildBackendWorlds(reporter, tags, gherkinScenario);
		createTimelineData(gherkinScenario);
	}

	@Override
	public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
		super.runStep(featurePath, step, reporter, i18n);
		addFeaturePathToTimelineData(featurePath);
	}

	@Override
	public void disposeBackendWorlds(String scenarioDesignation) {
		saveTimelineData();
		super.disposeBackendWorlds(scenarioDesignation);
	}

	private void createTimelineData(Scenario gherkinScenario) {
		threadTimelineData = new ThreadTimelineData(System.currentTimeMillis(), Thread.currentThread().getId(),
				gherkinScenario.getId());
	}
	
	private void addFeaturePathToTimelineData(String featurePath) {
		if (threadTimelineData.getFeaturePath() == null)
			threadTimelineData.setFeaturePath(featurePath);
	}
	
	private void saveTimelineData() {
		threadTimelineData.setEndTime(System.currentTimeMillis());
		threadExecutionRecorder.recordEvent(threadTimelineData);
	}
	
}
