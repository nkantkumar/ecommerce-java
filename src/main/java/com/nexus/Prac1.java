package com.nexus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Prac1 {
    public static void main(String[] args) {
        String str= "nexusnex";

        Map<Character, Long> countMap = str.chars()
                .filter(Character::isLetter)
                .mapToObj( c -> (char) c)
                .collect(Collectors.groupingBy(
                Function.identity(),
                HashMap::new,
                Collectors.counting()
        ));
        System.out.println(countMap);

        int[] freq = new int[256];
        for (char c : str.toCharArray()) {
            freq[c]++;
        }
        //System.out.println(Arrays.toString(freq));
        int[][] matrix = {{1, 2}, {3, 4}};
       // System.out.println(Arrays.deepToString(matrix));

        Map<Character, Long> result =
                str.chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.collectingAndThen(
                                Collectors.groupingBy(Function.identity(), Collectors.counting()),
                                m -> {
                                    System.out.println("Temp Map: " + m);
                                    return m;
                                }
                        ));

        str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Function.identity(), Collectors.counting()),
                        m -> {
                            System.out.println("Temp Map: " + m);
                            return m;
                        }
                )).entrySet().stream().forEach(System.out::println);

        result.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(System.out::println);

        result.forEach((key, value) -> System.out.println(key + " = " + value));

        int k= IntStream.range(0, 100)
                .reduce(0, (a,b) -> a-b);
        System.out.println(k);

        int p = IntStream.of(5,2,3)
                .parallel()
                .reduce(Integer.MAX_VALUE,(a,b)-> Math.min(a,b));
        System.out.println(p);

        OptionalInt o= IntStream.of(3,6,2).max();
        System.out.println(o.getAsInt());



        Map<Integer, List<Integer>> map =
                IntStream.range(1, 100)
                        .parallel()
                        .boxed()
                        .collect(Collectors.groupingBy(i -> i % 10));
        map.forEach((integer, integers) -> System.out.println("" +integer +"-"+integers));


        IntStream.range(0,50)
                .filter(x -> x > 10)
                .forEach(x -> {
                    if (x == 20) return;
                    //System.out.println(x);
                });

        IntStream.range(0,50)
                .filter(x -> x > 10)
                .takeWhile(x -> x != 20)
                .forEach(System.out::println);

        var list = Arrays.asList("2","3","7");
       // list.stream()
                //.forEach(x -> list.remove(x));

        Stream.of("A", "B", "C")
                .parallel()
                .forEachOrdered(System.out::print);
    }
}
