package com.fix.ecommerce.async;

import jnr.ffi.annotations.In;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;

import static java.util.Calendar.MONDAY;
import static java.util.Calendar.TUESDAY;

public class MapTest {

    public static void main(String[] args) {
        List<List<Integer>> numbers = List.of(
                List.of(1, 2),
                List.of(3, 4)
        );
        List x1 = numbers.stream().flatMap(List::stream).toList();
        System.out.println(x1);


        var list = List.of(List.of(1,2), List.of(3,4));

        var changedList= list.stream().flatMap(List::stream).toList();
        System.out.println(changedList);

        var map = new HashMap<String, Integer>();

        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("d", 4);

        var listOfkeyMap = map.keySet().stream().toList();
        System.out.println(listOfkeyMap);

        var listOfvaluesMap = map.values().stream().toList();
        System.out.println(listOfkeyMap);
    }
}
