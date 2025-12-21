package com.distributed.ecommerce.hybrid;

public class User implements Cloneable {
    private String name;
    private int age;
   // private Address address; // reference type

    @Override
    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }
}