package com.codility;

import java.util.*;
import java.util.stream.Collectors;

public class FlattenNestedList {

    public static List<Integer> flattenAndDistinct(List<List<List<Integer>>> nestedList) {
        System.out.println(nestedList);
        return nestedList.stream()
                .flatMap(List::stream)           // Flatten first level
                .flatMap(List::stream)           // Flatten second level
                .distinct()                      // Remove duplicates
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<List<List<Integer>>> nestedList = List.of(
                List.of(
                        List.of(1, 2, 3),
                        List.of(4, 5, 6)
                ),
                List.of(
                        List.of(7, 8, 9),
                        List.of(1, 2, 10)
                ),
                List.of(
                        List.of(11, 12),
                        List.of(5, 6, 13)
                )
        );

        List<Integer> result = flattenAndDistinct(nestedList);
        System.out.println("Distinct elements: " + result);
        // Output: Distinct elements: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
    }
}