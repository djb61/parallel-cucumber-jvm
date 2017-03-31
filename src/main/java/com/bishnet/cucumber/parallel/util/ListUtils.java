package com.bishnet.cucumber.parallel.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {

	public static <T> List<List<T>> partition(List<T> sourceList, int numberOfSegments) {
		List<List<T>> partitionedList = new ArrayList<>();
		if (sourceList.isEmpty()) {
			partitionedList.add(Collections.<T> emptyList());
			return partitionedList;
		}

		int actualNumberOfSegments = Math.min(sourceList.size(), numberOfSegments);

		for (int i = 0; i < actualNumberOfSegments; i++)
			partitionedList.add(new ArrayList<T>());

		int sourceElementIndex = 0;
		for (T sourceElement : sourceList) {
			int destinationListIndex = sourceElementIndex % actualNumberOfSegments;
			partitionedList.get(destinationListIndex).add(sourceElement);
			sourceElementIndex++;
		}

		return partitionedList;
	}
}
