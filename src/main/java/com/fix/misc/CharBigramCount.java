package com.fix.misc;

import java.util.HashMap;
import java.util.Map;

public class CharBigramCount {
    public static void main(String[] args) {
        String text = "banana";

        Map<String, Integer> bigramCounts = new HashMap<>();

        for (int i = 0; i < text.length() - 1; i++) {
            String bigram = text.substring(i, i + 2); // 2 consecutive chars
            bigramCounts.merge(bigram, 1, Integer::sum);
        }

        bigramCounts.forEach((b, c) ->
                System.out.println(b + " -> " + c));

        bigramCounts.entrySet().forEach(System.out::println);
    }
}

