package com.bishnet.cucumber.parallel.report;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;

public class RerunReportMerger {

	private static final char DELIMITER = ' ';
	private List<Path> reportFiles;

	public RerunReportMerger(List<Path> reportFiles) {
		this.reportFiles = reportFiles;
	}

	public void merge(Path mergedReport) throws IOException {

		Path reportDirectory = mergedReport.getParent();

		if (reportDirectory != null)
			Files.createDirectories(reportDirectory);

		try (CountingOutputStream mergedReportStream = new CountingOutputStream(Files.newOutputStream(mergedReport, WRITE, CREATE, TRUNCATE_EXISTING))) {
			for (Path tempReportFile : reportFiles)
				appendTempReportToStream(mergedReportStream, tempReportFile);
		}

	}

	private void appendTempReportToStream(CountingOutputStream mergedReportStream, Path tempReportFile) throws IOException {
		try (InputStream tempReportStream = Files.newInputStream(tempReportFile)) {

			if (mergedReportStream.getByteCount() > 0 && tempReportStream.available() > 0)
				mergedReportStream.write(DELIMITER);
			
			IOUtils.copy(tempReportStream, mergedReportStream);
			
		}
	}

}
