package com.bishnet.cucumber.parallel.report;

import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonReportMerger {

	private List<Path> reportFiles;

	public JsonReportMerger(List<Path> reportFiles) {
		this.reportFiles = reportFiles;
	}

	public void merge(Path mergedReport) throws IOException {
		List<Map<String, Object>> features = new ArrayList<Map<String, Object>>();
		for (Path reportFile : reportFiles) {
			features.addAll(readSingleReport(reportFile));
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(mergedReport.toFile()))) {
			Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonDoubleSerializer()).setPrettyPrinting()
					.create();
			bw.write(gson.toJson(features));
			bw.close();
		}
	}

	private List<Map<String, Object>> readSingleReport(Path reportFile) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(reportFile.toFile()))) {
			Type listType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			return new Gson().fromJson(br, listType);
		}
	}
}
