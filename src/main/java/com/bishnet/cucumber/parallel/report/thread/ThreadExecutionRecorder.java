package com.bishnet.cucumber.parallel.report.thread;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadExecutionRecorder {

	private List<ThreadTimelineData> timelineData = Collections.synchronizedList(new LinkedList<ThreadTimelineData>());

	public void recordEvent(ThreadTimelineData data) {
		timelineData.add(data);
	}

	public List<ThreadTimelineData> getRecordedData() {
		return timelineData;
	}

}
