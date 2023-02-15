package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SortingAlgorithmTest {

    @Test
    public void testBubbleSort() {
        int[] unsortedArray = { 3, 60, 35, 2, 45, 320, 5 };
        int[] expectedArray = { 2, 3, 5, 35, 45, 60, 320 };
        SortingAlgorithm.bubbleSort(unsortedArray);
        assertArrayEquals(expectedArray, unsortedArray);
    }
}
