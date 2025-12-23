package com.distributed.ecommerce.async;

import java.sql.SQLOutput;
import java.util.List;

public class MapTest {

    public static void main(String[] args) {
        List<List<Integer>> numbers = List.of(
                List.of(1, 2),
                List.of(3, 4)
        );

        List x1 = numbers.stream().flatMap(List::stream).toList();

        System.out.println(x1);
    }
}
