package com.distributed.ecommerce.hybrid;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapTest {

    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList("c", "d"),
                Arrays.asList("e", "f")
        );

        List<String> flat = nestedList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
