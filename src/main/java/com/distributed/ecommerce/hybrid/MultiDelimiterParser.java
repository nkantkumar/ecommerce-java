package com.distributed.ecommerce.hybrid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultiDelimiterParser {



    public static String parseToIntList(String input, String... delimiters) {
        String pattern = "[" + String.join("", delimiters) + "]+";

        List<Integer> list =  Arrays.stream(input.split(pattern))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> new StringBuilder(s).reverse().toString())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Collections.reverse(list);
       String r = list.stream().map(String::valueOf).collect(Collectors.joining());
        return r;
    }

    public static void main(String[] args) {
        // Mixed delimiters in single input
        String num1 = ",1;,25|3;|4";
        String num2 = ",1  2;31|4,62";
        String num3 = ";;13,,2||3,,;4;";

        // Handle comma, semicolon, and pipe
        System.out.println(parseToIntList(num1, ",", ";", "|")); // [1, 2, 3]

        // Handle comma, semicolon, pipe, and space
        System.out.println(parseToIntList(num2, ",", ";", "|", " ")); // [1, 2, 3, 4]

        // Multiple consecutive delimiters
        System.out.println(parseToIntList(num3, ",", ";", "|")); // [1, 2, 3]
    }
}