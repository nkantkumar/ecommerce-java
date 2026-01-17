package com.codility;

import java.util.*;
import java.util.function.*;

public class MethodReferencesMagic {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        // Traditional way
        names.forEach(name -> System.out.println(name));

        // Method reference (cleaner!)
        names.forEach(System.out::println);

        // Constructor reference
        IntUnaryOperator op1 = x -> x + 1;
        Function<Integer, Integer> op2 = x -> x + 1;

        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        Function<Integer, List<String>> f = ArrayList::new;

        // Instance method reference
        String str = "Hello";
        Predicate<String> startsWithH = str::startsWith;
        System.out.println(startsWithH.test("Hel")); // true

        // Array constructor reference
        Function<Integer, String[]> arrayCreator = String[]::new;
        String[] array = arrayCreator.apply(5); // Creates String[5]
    }
}
