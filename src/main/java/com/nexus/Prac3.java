package com.nexus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.Map;

public class Prac3 {
    public static void main(String[] args) {
        var list = List.of(1,2,5,7);
        //System.out.println(list.reversed());
        var updated = IntStream.rangeClosed(1, list.size())
                .mapToObj(i -> list.get(list.size()-i))
                        .collect(Collectors.toList());
        System.out.println(updated);

        Map<String, Integer> original = Map.of("a", 1, "b", 2, "c", 3);
        var x = original.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue,Map.Entry::getKey ));

        System.out.println(x);
        var y = original.entrySet().stream()
                .collect(Collectors.toList());
        System.out.println(y);

        String str = "nishi";

        LinkedHashMap result = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()));
                //.entrySet().stream()
                //.filter(e -> e.getValue() == 1)
                //.map(Map.Entry::getKey)
                //.findFirst()
                //.orElse(null);

        System.out.println(result); // w

    }
}
