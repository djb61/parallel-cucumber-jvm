package com.bishnet.cucumber.parallel.report.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.FileUtils.*;
import static org.assertj.core.api.Assertions.assertThat;


public class ThreadExecutionReporterTest {

	private static final String OUTPUT_DIRECTORY = "timeline";
	private static final String REPORT_HTML = "index.html";
	private static final String[] RESOURCES = new String[] { "chosen-sprite.png", "chosen.jquery.min.js",
			"chosen.min.css", "jquery-2.2.3.min.js", "jquery.timepicker.css", "jquery.timepicker.js", "moment.min.js",
			REPORT_HTML, "vis.min.css", "vis.min.js" };

	private Path reportDirectory;

	@Before
	public void setUp() throws IOException {
		reportDirectory = Files.createTempDirectory("tempTestThreadReport");
	}

	@After
	public void teardown() throws IOException {
		//deleteDirectory(reportDirectory.toFile());
	}

	@Test
	public void shouldWriteThreadExecutionReport() throws IOException {

		List<ThreadTimelineData> threadTimelineData = Arrays.asList(new ThreadTimelineData(0, 0, "scenarioId1"));

		ThreadExecutionReporter threadExecutionReporter = new ThreadExecutionReporter();
		threadExecutionReporter.writeReport(threadTimelineData, reportDirectory);

		File reportDirectoryFile = reportDirectory.toFile();

		for (String requiredResourceName: RESOURCES) {
			File requiredResourceFile = getFile(reportDirectoryFile, OUTPUT_DIRECTORY, requiredResourceName);
			assertThat(requiredResourceFile.exists()).as("File %s not exist", requiredResourceFile).isTrue();
		}

		File reportExample = new File(this.getClass().getResource("/com/bishnet/cucumber/parallel/report/thread/reportExample.html").getPath());

		assertThat(contentEqualsIgnoreEOL(reportExample, getFile(reportDirectoryFile, OUTPUT_DIRECTORY, REPORT_HTML),  "UTF-8")).isTrue();

	}

}