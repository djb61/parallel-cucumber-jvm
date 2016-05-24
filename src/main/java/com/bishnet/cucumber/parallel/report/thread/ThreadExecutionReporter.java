package com.bishnet.cucumber.parallel.report.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;

public class ThreadExecutionReporter {

	private static final String INDEX_HTML = "index.html";
	private static final String TEMPLATE_HTML = "template.html";
	private static final String RESOURCES_DIRECTORY = "timeline/";
	private static final String OUTPUT_DIRECTORY = "timeline";
	private static final String DATA_PLACEHOLDER = "${DATA}";

	private static final String[] RESOURCES = new String[] { "chosen-sprite.png", "chosen.jquery.min.js",
			"chosen.min.css", "jquery-2.2.3.min.js", "jquery.timepicker.css", "jquery.timepicker.js", "moment.min.js",
			TEMPLATE_HTML, "vis.min.css", "vis.min.js" };

	private static final Logger LOG = Logger.getLogger(ThreadExecutionReporter.class.getName());

	public void writeReport(List<ThreadTimelineData> reportData, Path outputDirectory) throws IOException {

		File destinationDirectory = FileUtils.getFile(outputDirectory.toFile(), OUTPUT_DIRECTORY);
		
		copyStaticReportFiles(destinationDirectory);

		File templateFile = FileUtils.getFile(destinationDirectory, TEMPLATE_HTML);
		String json = convertReportDataToJson(reportData);
		
		buildReportFromTemplate(destinationDirectory, json, templateFile);
		removeTemplate(templateFile);
		
	}

	private String convertReportDataToJson(List<ThreadTimelineData> reportData) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(reportData);
		return json;
	}

	private void buildReportFromTemplate(File destinationDirectory, String json, File templateFile) throws IOException {
		File outputFile = FileUtils.getFile(destinationDirectory, INDEX_HTML);

		String templateContent = FileUtils.readFileToString(templateFile);

		templateContent = templateContent.replace(DATA_PLACEHOLDER, json);
		LOG.info("Write thread timeline report to: " + outputFile.getAbsolutePath());

		FileUtils.write(outputFile, templateContent);
	}
	
	private void removeTemplate(File templateFile) {
		FileUtils.deleteQuietly(templateFile);
	}

	private void copyStaticReportFiles(File destinationDirectory) throws IOException {

		if (!destinationDirectory.exists())
			destinationDirectory.mkdirs();

		for (String resourceName : RESOURCES) {
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream(RESOURCES_DIRECTORY + resourceName);
			FileUtils.copyInputStreamToFile(inputStream, FileUtils.getFile(destinationDirectory, resourceName));
		}

	}

}
