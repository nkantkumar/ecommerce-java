package com.complex;

import java.util.HashMap;

public class MapTest {
    public static void main(String[] args) {
        var map = new HashMap<String, Integer>();

        map.computeIfAbsent("alice", k ->1);
        System.out.println(map);
        map.computeIfPresent("alice", (x, y) -> y+1);

        System.out.println(map);
        map.computeIfAbsent("alice", k ->1);
        System.out.println(map);
    }
}
