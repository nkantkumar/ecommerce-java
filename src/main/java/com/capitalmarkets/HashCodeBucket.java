package com.capitalmarkets;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.UUID;

public class HashCodeBucket {
    public static void main(String[] args) {
        int hash = MurmurHash3.hash32(UUID.randomUUID().toString());
        int bucket = (hash & Integer.MAX_VALUE) % 2;
        System.out.println(bucket);

    }
}
