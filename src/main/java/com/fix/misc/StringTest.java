package com.fix.misc;

import java.util.Arrays;
import java.util.Comparator;

public class StringTest {
    public static void main(String[] args) {
        var list = Arrays.asList("nisi", "youtuvbe","netflix");
        list.sort(Comparator.comparing(x -> x.length()));
        System.out.println(list);


    }
}
