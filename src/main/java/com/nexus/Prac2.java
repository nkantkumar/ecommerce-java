package com.nexus;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Prac2 {
    public static void main(String[] args) {
        String str= "examepama";
        Map<Character, Long> freq =
                str.chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                LinkedHashMap::new,
                                Collectors.counting()
                        ));

        char result =
                freq.entrySet()
                        .stream()
                        .filter(e -> e.getValue() == 1)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow();
        System.out.println(result);

        Map<String, Integer> map = Map.of(
                "C", 1,
                "B", 2,
                "A", 3
        );
        Map<String, Integer> sorted =
                map.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                LinkedHashMap::new
                        ));
        System.out.println(sorted);

    }

}
