package com.codility;

import java.util.stream.Collectors;

public class PureStreamCompression {

    public static String compress(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String compressed = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        c -> c,
                        java.util.LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> "" + entry.getKey() + entry.getValue())
                .collect(Collectors.joining());

        return compressed.length() < str.length() ? compressed : str;
    }

    public static void main(String[] args) {
        System.out.println(compress("aabcccccaaa"));  // Output: a2b1c5a3
        System.out.println(compress("abc"));          // Output: abc
    }
}