package com.bishnet.cucumber.parallel.report.thread;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ThreadExecutionRecorderTest {

	@Test
	public void shouldRecordEvent()  {

		ThreadExecutionRecorder threadExecutionRecorder = new ThreadExecutionRecorder();
		ThreadTimelineData threadTimelineData = new ThreadTimelineData(0, 0, "scenarioId");
		threadExecutionRecorder.recordEvent(threadTimelineData);

		List<ThreadTimelineData> recordedData = threadExecutionRecorder.getRecordedData();
		assertThat(recordedData.size()).isEqualTo(1);
		assertThat(recordedData).contains(threadTimelineData);

	}

}