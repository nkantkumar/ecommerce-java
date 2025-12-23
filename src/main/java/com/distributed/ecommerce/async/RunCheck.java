package com.distributed.ecommerce.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunCheck {

    public static void main(String[] args) {
        Callable task1 = () -> {
            System.out.println("Processing...");
            int i=1;
            if (i==1) {
                try {
                    throw new Exception("Failed");  // Allowed
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return "x";
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);

// Submit Callable
        Future<String> future = executor.submit(task1);
    }
}
