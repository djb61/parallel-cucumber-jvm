package com.bishnet.cucumber.parallel.runtime;

import java.util.List;

import cucumber.runtime.Backend;

public interface CucumberBackendFactory {

	List<Backend> getBackends();
}
