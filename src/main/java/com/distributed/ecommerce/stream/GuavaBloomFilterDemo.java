package com.distributed.ecommerce.stream;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.nio.charset.StandardCharsets;

public class GuavaBloomFilterDemo {
    public static void main(String[] args) {
        // Create bloom filter
        BloomFilter<String> bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                1000,    // expected insertions
                0.01     // false positive probability
        );

        // Add elements
        bloomFilter.put("user123@email.com");
        bloomFilter.put("user456@email.com");

        // Check membership
        System.out.println(bloomFilter.mightContain("user123@email.com")); // true
        System.out.println(bloomFilter.mightContain("user789@email.com")); // probably false

        // Check expected FPP
        System.out.println("Expected FPP: " + bloomFilter.expectedFpp());
    }
}