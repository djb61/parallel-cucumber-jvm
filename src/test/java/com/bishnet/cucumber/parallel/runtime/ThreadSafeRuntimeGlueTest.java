package com.bishnet.cucumber.parallel.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.locks.Lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.UndefinedStepsTracker;
import cucumber.runtime.xstream.LocalizedXStreams;

@RunWith(MockitoJUnitRunner.class)
public class ThreadSafeRuntimeGlueTest {

	@Mock
	private UndefinedStepsTracker tracker;
	
	@Mock
	private LocalizedXStreams localizedXStreams;
	
	@Mock
	private StepDefinitionReporter stepDefinitionReporter;
	
	@Mock
	private Lock glueLock;
	
	private ThreadSafeRuntimeGlue runtimeGlue = new ThreadSafeRuntimeGlue(tracker, localizedXStreams);
	
	@Test
	public void shouldReportStepDefinitionsWithLock() {
		ThreadSafeRuntimeGlue.GLUE_LOCK = glueLock;
		runtimeGlue.reportStepDefinitions(stepDefinitionReporter);
		verify(ThreadSafeRuntimeGlue.GLUE_LOCK).lock();
		verify(ThreadSafeRuntimeGlue.GLUE_LOCK).unlock();
	}

}
