package com.bishnet.cucumber.parallel.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListUtilsTest {

	@Test
	public void shouldReturnEmptyListIfAnEmptySourceListIsProvided() {
		int numberOfSegments = 0;
		List<Object> sourceList = new ArrayList<>();
		List<List<Object>> partitionedList = ListUtils.partition(sourceList, numberOfSegments);
		assertThat(partitionedList.size()).isEqualTo(numberOfSegments);
	}

	@Test
	public void shouldReturnEqualSizedListsWhenSourceListSizeIsDivisibleByNumberOfSegments() {
		int numberOfSegments = 2;
		int numberOfSourceElements = 4;
		List<Object> sourceList = new ArrayList<>();
		for (int i = 0; i < numberOfSourceElements; i++)
			sourceList.add(new Object());
		int sizeOfPartition = numberOfSourceElements / numberOfSegments;
		List<List<Object>> partitionedList = ListUtils.partition(sourceList, numberOfSegments);
		assertThat(partitionedList.size()).isEqualTo(numberOfSegments);
		assertThat(partitionedList.get(0).size()).isEqualTo(sizeOfPartition);
		assertThat(partitionedList.get(1).size()).isEqualTo(sizeOfPartition);
	}

	@Test
	public void shouldReturnSmallerFinalListWhenSourceListSizeIsNotDivisibleByNumberOfSegments() {
		int numberOfSegments = 3;
		int numberOfSourceElements = 5;
		List<Object> sourceList = new ArrayList<>();
		for (int i = 0; i < numberOfSourceElements; i++)
			sourceList.add(new Object());
		List<List<Object>> partitionedList = ListUtils.partition(sourceList, numberOfSegments);
		assertThat(partitionedList.size()).isEqualTo(numberOfSegments);
		assertThat(partitionedList.get(0).size()).isEqualTo(2);
		assertThat(partitionedList.get(1).size()).isEqualTo(2);
		assertThat(partitionedList.get(2).size()).isEqualTo(1);
	}

	@Test
	public void returnedListsShouldBeInterleavedFromSourceList() {
		List<Object> sourceList = new ArrayList<>();
		Object firstObject = new Object();
		sourceList.add(firstObject);
		Object secondObject = new Object();
		sourceList.add(secondObject);
		Object thirdObject = new Object();
		sourceList.add(thirdObject);
		Object fourthObject = new Object();
		sourceList.add(fourthObject);
		List<List<Object>> partitionedList = ListUtils.partition(sourceList, 2);
		assertThat(partitionedList.get(0)).contains(firstObject, thirdObject);
		assertThat(partitionedList.get(1)).contains(secondObject, fourthObject);
	}

	@Test
	public void numberOfReturnedListsShouldBeEqualToSizeOfSourceListIfSmallerThanRequestedNumberOfSegments() {
		int numberOfSegments = 5;
		int numberOfSourceElements = 3;
		List<Object> sourceList = new ArrayList<>();
		for (int i = 0; i < numberOfSourceElements; i++)
			sourceList.add(new Object());
		List<List<Object>> partitionedList = ListUtils.partition(sourceList, numberOfSegments);
		assertThat(partitionedList.size()).isEqualTo(numberOfSourceElements);
	}
}
