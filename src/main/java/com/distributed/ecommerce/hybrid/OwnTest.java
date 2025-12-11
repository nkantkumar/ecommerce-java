package com.distributed.ecommerce.hybrid;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OwnTest {
    public static void main(String[] args) {
        String str = "programming";
        String result = str.chars()
                .distinct()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());

        String str1 = "hello";
        Map<Character, Long> count = str1.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        System.out.println(count);

        int[] arr = {5, 2, 8, 1, 9};
        int secondLargest = Arrays.stream(arr)
                .boxed()
                .sorted(Collections.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(-1);

        Map<String, Integer> map = new HashMap<>();
        map.put("a", 3);
        map.put("b", 1);

        Map<String, Integer> sorted = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        System.out.println(sorted);
    }

}
