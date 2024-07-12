package com.ail.home.algorithms;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class BubbleSortTest {

	@Test
	void testBubbleSort() {
		int[] arr = { 64, 34, 25, 12, 22, 11, 90 };
		bubbleSort(arr);
		System.out.println("Sorted array");
		printArray(arr);
		assertThat(arr).isEqualTo(new int[] { 11, 12, 22, 25, 34, 64, 90 });
	}

	static void bubbleSort(int[] arr) {
		int n = arr.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (arr[j] > arr[j + 1]) {
					// swap temp and arr[i]
					int temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
			}
		}
	}

	static void printArray(int[] arr) {
		for (int j : arr) {
			System.out.print(j + " ");
		}
		System.out.println();
	}
}
