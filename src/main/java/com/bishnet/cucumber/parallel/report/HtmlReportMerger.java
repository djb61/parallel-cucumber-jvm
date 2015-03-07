package com.bishnet.cucumber.parallel.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class HtmlReportMerger {

	private static final String IMAGE_PATTERN = "*.png";
	private static final String REPORT_JS = "report.js";
	private List<Path> reportDirs;
	
	public HtmlReportMerger(List<Path> reportDirs) {
		this.reportDirs = reportDirs;
	}
	
	public void merge(Path mergedReport) throws IOException {
		Files.createDirectories(mergedReport);
		boolean firstReport = true;
		for (Path reportDir : reportDirs) {
			if (firstReport) {
				copyResourceFiles(reportDir, mergedReport);
			}
			copyImagesAndJavaScriptWithImageRename(reportDir, mergedReport, firstReport);
			firstReport = false;
		}
	}
	
	private void copyImagesAndJavaScriptWithImageRename(Path reportDir, Path targetDir, boolean overwriteReport) throws IOException {
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(reportDir, IMAGE_PATTERN)) {
			List<String> reportJs = Files.readAllLines(reportDir.resolve(REPORT_JS), StandardCharsets.UTF_8);
			for (Path embeddedImage : dirStream) {
				String uniqueName = UUID.randomUUID().toString() + ".png";
				Files.copy(embeddedImage, targetDir.resolve(uniqueName));
				ListIterator<String> listIterator = reportJs.listIterator();
				while(listIterator.hasNext()) {
					listIterator.set(listIterator.next().replace(embeddedImage.getFileName().toString(), uniqueName));
				}
			}
			OpenOption[] copyOptions;
			if (overwriteReport)
				copyOptions = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING };
			else
				copyOptions = new OpenOption[] { StandardOpenOption.APPEND };
			Files.write(targetDir.resolve(REPORT_JS), reportJs, StandardCharsets.UTF_8, copyOptions);
		}
	}
	
	private void copyResourceFiles(Path sourceDir, Path targetDir) throws IOException {
		PathMatcher reportAndImageMatcher = FileSystems.getDefault().getPathMatcher("glob:{" + REPORT_JS +"," + IMAGE_PATTERN +"}");
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(sourceDir)) {
			for (Path resourceFile : dirStream)
				if (!reportAndImageMatcher.matches(resourceFile.getFileName()))
					Files.copy(resourceFile, targetDir.resolve(resourceFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
