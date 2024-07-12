package com.ail.home.algorithms;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class QuickSortTest {

	@Test
	void testQuickSort() {
		int[] arr = { 64, 34, 25, 12, 22, 11, 90 };
		quickSort(arr, 0, arr.length - 1);
		System.out.println("Sorted array");
		printArray(arr);
		assertThat(arr).isEqualTo(new int[] { 11, 12, 22, 25, 34, 64, 90 });
	}

	static void quickSort(int[] arr, int begin, int end) {
		if (begin < end) {
			int partitionIndex = partition(arr, begin, end);

			quickSort(arr, begin, partitionIndex - 1);
			quickSort(arr, partitionIndex + 1, end);
		}
	}

	static int partition(int[] arr, int begin, int end) {
		int pivot = arr[end];
		int i = (begin - 1);

		for (int j = begin; j < end; j++) {
			if (arr[j] <= pivot) {
				i++;

				int swapTemp = arr[i];
				arr[i] = arr[j];
				arr[j] = swapTemp;
			}
		}

		int swapTemp = arr[i + 1];
		arr[i + 1] = arr[end];
		arr[end] = swapTemp;

		return i + 1;
	}

	static void printArray(int[] arr) {
		for (int j : arr) {
			System.out.print(j + " ");
		}
		System.out.println();
	}
}
