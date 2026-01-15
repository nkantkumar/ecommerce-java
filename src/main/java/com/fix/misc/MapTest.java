package com.fix.misc;

import java.util.Map;
import java.util.PriorityQueue;

public class MapTest {
    public static void main(String[] args) {
        Map<String, Integer> map = Map.of(
                "A", 1,
                "B", 2,
                "C", 3
        );

        map.entrySet()
                .stream()
                .forEach(entry ->
                        System.out.println(entry.getKey() + " -> " + entry.getValue())
                );

        PriorityQueue<Integer> pq = new PriorityQueue<>(); // natural ordering (min first)

        pq.add(5);
        pq.add(1);
        pq.add(10);

        System.out.println(pq.peek()); // 1 (smallest element)
        System.out.println(pq.poll()); // 1 (removes smallest)
        System.out.println(pq.poll()); // 5
        System.out.println(pq.poll()); // 10

    }
}
