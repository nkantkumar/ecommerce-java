package com.distributed.ecommerce.hybrid;

public record Person(String name, int age) {
    // Compact constructor
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    // You can add methods
    public boolean isAdult() {
        return age >= 18;
    }
}
