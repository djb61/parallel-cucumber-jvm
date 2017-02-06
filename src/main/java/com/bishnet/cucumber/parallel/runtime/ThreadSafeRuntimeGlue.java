package com.bishnet.cucumber.parallel.runtime;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.UndefinedStepsTracker;
import cucumber.runtime.xstream.LocalizedXStreams;

public class ThreadSafeRuntimeGlue extends RuntimeGlue {

	protected static Lock GLUE_LOCK = new ReentrantLock();

	public ThreadSafeRuntimeGlue(UndefinedStepsTracker tracker, LocalizedXStreams localizedXStreams) {
		super(tracker, localizedXStreams);
	}

	@Override
	public void reportStepDefinitions(StepDefinitionReporter stepDefinitionReporter) {
		reportStepDefinitionsSerial(stepDefinitionReporter);
	}

	private void reportStepDefinitionsSerial(StepDefinitionReporter stepDefinitionReporter) {
		GLUE_LOCK.lock();
	
		try {
			super.reportStepDefinitions(stepDefinitionReporter);
		} finally {
			GLUE_LOCK.unlock();
		}
	}

}
