package com.bishnet.cucumber.parallel.report;

import static org.assertj.core.api.Assertions.assertThat;
import gherkin.deps.com.google.gson.JsonPrimitive;

import org.junit.Test;

public class JsonDoubleSerializerTest {

	@Test
	public void doubleWithFractionalComponentShouldBeSerializedAsDouble() {
		double sourceValue = 1.24;
		JsonDoubleSerializer jsonDoubleSerializer = new JsonDoubleSerializer();
		JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonDoubleSerializer.serialize(sourceValue, null, null);
		assertThat(jsonPrimitive.getAsNumber().getClass()).isEqualTo(Double.class);
	}
	
	@Test
	public void doubleWithoutFractionalComponentShouldBeSerializedAsLong() {
		double sourceValue = 2;
		JsonDoubleSerializer jsonDoubleSerializer = new JsonDoubleSerializer();
		JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonDoubleSerializer.serialize(sourceValue, null, null);
		assertThat(jsonPrimitive.getAsNumber().getClass()).isEqualTo(Long.class);
	}
}
