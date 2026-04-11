package com.nexus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Prac3 {
    public static void main(String[] args) {
        var list = List.of(1,2,5,7);
        //System.out.println(list.reversed());
        var updated = IntStream.rangeClosed(1, list.size())
                .mapToObj(i -> list.get(list.size()-i))
                        .collect(Collectors.toList());
        System.out.println(updated);

    }
}
