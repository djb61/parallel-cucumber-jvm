package com.bishnet.cucumber.parallel.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;

import org.junit.Test;

import cucumber.runtime.CucumberException;

public class CucumberRuntimeCallableTest {

	@Test
	public void shouldReturnCorrectExitCodeForZeroCodeOnCompletion() throws Exception {
		byte exitCode = 0;
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(new byte[] { exitCode }, new boolean[] { false });
		CucumberRuntimeCallable callable = new CucumberRuntimeCallable(new ArrayList<String>(), runtimeFactory);
		assertThat(callable.call()).isEqualTo(exitCode);
	}
	
	@Test
	public void shouldReturnCorrectExitCodeForNonZeroCodeOnCompletion() throws Exception {
		byte exitCode = 1;
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(new byte[] { exitCode }, new boolean[] { false });
		CucumberRuntimeCallable callable = new CucumberRuntimeCallable(new ArrayList<String>(), runtimeFactory);
		assertThat(callable.call()).isEqualTo(exitCode);
	}
	
	@Test(expected=CucumberException.class)
	public void shouldThrowCucumberExceptionOnFailure() throws Exception {
		byte exitCode = 0;
		FakeCucumberRuntimeFactory runtimeFactory = new FakeCucumberRuntimeFactory(new byte[] { exitCode }, new boolean[] { true });
		CucumberRuntimeCallable callable = new CucumberRuntimeCallable(new ArrayList<String>(), runtimeFactory);
		callable.call();
	}
}
