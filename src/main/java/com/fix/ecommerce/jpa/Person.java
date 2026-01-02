package com.fix.ecommerce.jpa;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Person {
    public abstract String name();
    public abstract int age();
    public abstract List<String> hobbies();
}

