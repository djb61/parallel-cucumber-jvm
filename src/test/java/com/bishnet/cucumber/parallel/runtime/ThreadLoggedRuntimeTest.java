package com.bishnet.cucumber.parallel.runtime;

import com.bishnet.cucumber.parallel.report.thread.ThreadExecutionRecorder;
import com.bishnet.cucumber.parallel.report.thread.ThreadTimelineData;
import cucumber.runtime.Backend;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThreadLoggedRuntimeTest {

	private static final String FEATURE_PATH = "featurePath";
	private static final String SCENARIO_ID = "scenarioId";
	private static final String SCENARIO_NAME = "scenarioName";
	private static final String GIVEN = "given";

	@Mock
	private ResourceLoader resourceLoader;

	@Mock
	private ClassLoader classLoader;

	@Mock
	private RuntimeOptions runtimeOptions;

	@Mock
	private Reporter reporter;

	@Mock
	private Set<Tag> tags;

	@Mock
	private Scenario scenario;

	@Mock
	private Backend backend;

	@Mock
	private ThreadExecutionRecorder threadExecutionRecorder;

	@Mock
	private Step step;

	@Mock
	private I18n i18n;

	private ThreadLoggedRuntime threadLoggedRuntime;

	@Before
	public void setUp() throws Exception {

		when(i18n.keywords(GIVEN)).thenReturn(Arrays.asList(GIVEN));
		when(scenario.getId()).thenReturn(SCENARIO_ID);
		when(scenario.getName()).thenReturn(SCENARIO_NAME);

		List<Backend> backends = Arrays.asList(backend);
		threadLoggedRuntime = new ThreadLoggedRuntime(resourceLoader, classLoader, backends, runtimeOptions, threadExecutionRecorder);
	}

	@Test
	public void shouldRecordThreadTimelineData() {

		threadLoggedRuntime.buildBackendWorlds(reporter, tags, scenario);
		threadLoggedRuntime.runStep(FEATURE_PATH, step, reporter, i18n);
		threadLoggedRuntime.disposeBackendWorlds("");

		ArgumentCaptor<ThreadTimelineData> argumentCaptor = ArgumentCaptor.forClass(ThreadTimelineData.class);
		verify(threadExecutionRecorder).recordEvent(argumentCaptor.capture());

		ThreadTimelineData recordedData = argumentCaptor.getValue();

		assertThat(recordedData.getFeaturePath()).isEqualTo(FEATURE_PATH);
		assertThat(recordedData.getThreadId()).isEqualTo(Thread.currentThread().getId());
		assertThat(recordedData.getScenario()).isEqualTo(SCENARIO_NAME);
		assertThat(recordedData.getStartTime()).isNotNull();
		assertThat(recordedData.getEndTime()).isNotNull();

	}

}
