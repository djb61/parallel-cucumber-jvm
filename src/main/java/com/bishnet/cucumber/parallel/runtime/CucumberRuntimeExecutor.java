package com.bishnet.cucumber.parallel.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bishnet.cucumber.parallel.util.RemoveDirectoryRunnable;

import cucumber.runtime.CucumberException;

public class CucumberRuntimeExecutor {

	private CucumberRuntimeFactory runtimeFactory;
	private List<Path> rerunFiles;
	private RuntimeConfiguration runtimeConfiguration;
	private List<Path> htmlReports = new ArrayList<Path>();
	private List<Path> jsonReports = new ArrayList<Path>();

	public CucumberRuntimeExecutor(CucumberRuntimeFactory runtimeFactory, List<Path> rerunFiles,
			RuntimeConfiguration runtimeConfiguration) {
		this.runtimeFactory = runtimeFactory;
		this.rerunFiles = rerunFiles;
		this.runtimeConfiguration = runtimeConfiguration;
	}

	public List<Path> getHtmlReports() {
		return htmlReports;
	}

	public List<Path> getJsonReports() {
		return jsonReports;
	}

	public byte run() throws InterruptedException, IOException {
		byte result = 0;
		ExecutorService executor = Executors.newFixedThreadPool(rerunFiles.size());
		List<CucumberRuntimeCallable> runtimes = new ArrayList<CucumberRuntimeCallable>();
		for (Path rerunFile : rerunFiles) {
			CucumberRuntimeCallable runtimeCallable = new CucumberRuntimeCallable(buildCallableRuntimeArgs(rerunFile),
					runtimeFactory);
			runtimes.add(runtimeCallable);
		}
		List<Future<Byte>> futures = executor.invokeAll(runtimes);
		executor.shutdown();
		for (Future<Byte> future : futures)
			try {
				byte callableResult = future.get();
				result |= callableResult;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof CucumberException)
					throw (CucumberException) e.getCause();
				else
					throw new CucumberException(e.getCause());
			}
		return result;
	}

	private List<String> buildCallableRuntimeArgs(Path rerunFile) throws IOException {
		List<String> callableRuntimeArgs = new ArrayList<String>();
		if (runtimeConfiguration.jsonReportRequired) {
			Path jsonReport = Files.createTempFile("parallelCukes", ".json");
			jsonReport.toFile().deleteOnExit();
			jsonReports.add(jsonReport);
			callableRuntimeArgs.add("--plugin");
			callableRuntimeArgs.add("json:" + jsonReport);
		}
		if (runtimeConfiguration.htmlReportRequired) {
			Path htmlReport = Files.createTempDirectory("parallelCukes");
			Runtime.getRuntime().addShutdownHook(new Thread(new RemoveDirectoryRunnable(htmlReport)));
			htmlReports.add(htmlReport);
			callableRuntimeArgs.add("--plugin");
			callableRuntimeArgs.add("html:" + htmlReport);
		}
		callableRuntimeArgs.add("@" + rerunFile);
		return callableRuntimeArgs;
	}
}
