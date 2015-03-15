package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cucumber.runtime.CucumberException;

public class CucumberRuntimeExecutorTest {

	@Test
	public void shouldReturnZeroExitCodeWhenAllUnderlyingRuntimesReturnZero() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, getRerunFiles(),
				getRuntimeConfiguration(false, false));
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnExitCodeOfOneWhenOneUnderlyingRuntimeReturnsOne() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 1 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, getRerunFiles(),
				getRuntimeConfiguration(false, false));
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 1);
	}

	@Test
	public void shouldReturnExitCodeOfOneWhenAllUnderlyingRuntimesReturnOne() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 1, 1 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, getRerunFiles(),
				getRuntimeConfiguration(false, false));
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 1);
	}

	@Test(expected = CucumberException.class)
	public void shouldThrowCucumberExceptionWhenOneUnderlyingRuntimeDoes() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, true };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, getRerunFiles(),
				getRuntimeConfiguration(false, false));
		assertThat(runtimeExecutor.run()).isEqualTo((byte) 0);
	}

	@Test
	public void shouldReturnANumberOfHtmlReportsEqualToTheNumberOfRerunFiles() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		List<Path> rerunFiles = getRerunFiles();
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles,
				getRuntimeConfiguration(false, true));
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getHtmlReports().size()).isEqualTo(rerunFiles.size());
	}

	@Test
	public void shouldReturnANumberOfJsonReportsEqualToTheNumberOfRerunFiles() throws InterruptedException, IOException {
		byte[] exitCodes = new byte[] { 0, 0 };
		boolean[] shouldThrowExceptions = new boolean[] { false, false };
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(exitCodes, shouldThrowExceptions);
		List<Path> rerunFiles = getRerunFiles();
		CucumberRuntimeExecutor runtimeExecutor = new CucumberRuntimeExecutor(runtimeFactory, rerunFiles,
				getRuntimeConfiguration(true, false));
		runtimeExecutor.run();
		assertThat(runtimeExecutor.getJsonReports().size()).isEqualTo(rerunFiles.size());
	}

	private List<Path> getRerunFiles() {
		List<Path> rerunFiles = new ArrayList<Path>();
		rerunFiles.add(Paths.get("/some/path"));
		rerunFiles.add(Paths.get("/some/other/path"));
		return rerunFiles;
	}

	private RuntimeConfiguration getRuntimeConfiguration(boolean jsonReportRequired, boolean htmlReportRequired) {
		return new RuntimeConfiguration(0, null, null, null, null, htmlReportRequired, null, jsonReportRequired);
	}
}
