package com.fix.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlidingWindowSum {

    public static void main(String[] args) {
        var list = List.of(1,2,3,4,5,6);
        var ws = 5;
        var avg= IntStream.range(0, list.size()-ws+1)
                .mapToObj(i -> list.subList(i, i+ws))
                .map(k -> k.stream().mapToInt(Integer::intValue).average().orElse(0))
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println(avg);
    }
}
