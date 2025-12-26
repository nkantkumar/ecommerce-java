package com.distributed.ecommerce.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ReverseWordsStream2 {
    public static String reverseWords(String sentence) {
        List<String> words = Arrays.stream(sentence.trim().split("\\s+"))
                .collect(Collectors.toList());

        Collections.reverse(words);

        ExecutorService executor =
                Executors.newFixedThreadPool(20);

        //CompletableFuture
//                .supplyAsync(this::fetch, executor);

        return words.stream()
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        System.out.println(reverseWords("i love you"));
    }
}
