package com.bishnet.cucumber.parallel.report.thread;

import gherkin.deps.com.google.gson.annotations.SerializedName;

public class ThreadTimelineData {

	@SerializedName("start")
	private long startTime;
	@SerializedName("end")
	private long endTime;
	@SerializedName("group")
	private long threadId;
	@SerializedName("content")
	private String scenario;
	@SerializedName("featurePath")
	private String featurePath;

	public ThreadTimelineData(long startTime, long threadId, String scenario) {
		this.startTime = startTime;
		this.threadId = threadId;
		this.scenario = scenario;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void setFeaturePath(String featurePath) {
		this.featurePath = featurePath;
	}

	public String getFeaturePath() {
		return featurePath;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getThreadId() {
		return threadId;
	}

	public String getScenario() {
		return scenario;
	}
}
