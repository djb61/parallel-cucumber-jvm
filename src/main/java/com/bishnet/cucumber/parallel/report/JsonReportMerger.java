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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReportMerger {

	private List<Path> reportFiles;
	private List<Map<String, Object>> features = new ArrayList<>();
	private Map<String, Map<String, Object>> featuresMap = new HashMap<>();
	private List<Map<String, Object>> flakyfeatures = new ArrayList<>();

	public JsonReportMerger(List<Path> reportFiles) {
		this.reportFiles = reportFiles;
	}

	public void merge(Path mergedReport) throws IOException {
		for (Path reportFile : reportFiles) {
			List<Map<String, Object>> featureList = readSingleReport(reportFile);
			features.addAll(featureList);
		}
		writeMergedReportToFile(mergedReport, features);
	}

	public void mergeRerunFailedReports(Path mergedReport, Path flakyReport) throws IOException {
		features.addAll(readSingleReport(mergedReport));
		features.forEach(mergedFeature -> featuresMap.put((String) mergedFeature.get("id"), mergedFeature));
		readSingleReport(reportFiles.get(0)).forEach(rerunFeature -> {
			flakyfeatures.add(featuresMap.get((String) rerunFeature.get("id")));
			featuresMap.put((String) rerunFeature.get("id"), rerunFeature);
		});
		features.clear();
		features.addAll(featuresMap.values());
		writeMergedReportToFile(flakyReport, flakyfeatures);
		writeMergedReportToFile(mergedReport, features);
	}

	private void writeMergedReportToFile(Path mergedReport, List<Map<String, Object>> features) throws IOException {
		Path reportDirectory = mergedReport.getParent();
		if (reportDirectory != null)
			Files.createDirectories(reportDirectory);
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
