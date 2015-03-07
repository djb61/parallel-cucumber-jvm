package com.bishnet.cucumber.parallel.runtime;

import java.util.ArrayList;
import java.util.List;

import cucumber.runtime.Backend;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.JavaBackend;

public class FakeCucumberBackendFactory implements CucumberBackendFactory {

	private boolean wasInvoked;

	@Override
	public List<Backend> getBackends() {
		wasInvoked = true;
		ResourceLoader resourceLoader = new MultiLoader(Thread.currentThread().getContextClassLoader());
		Backend backend = new JavaBackend(resourceLoader);
		ArrayList<Backend> backends = new ArrayList<Backend>();
		backends.add(backend);
		return backends;
	}

	public boolean wasInvoked() {
		return wasInvoked;
	}
}
