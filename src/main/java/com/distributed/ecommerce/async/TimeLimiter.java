package com.distributed.ecommerce.async;

import java.util.concurrent.*;

public class TimeLimiter {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            Thread.sleep(3000);
            return "Done";
        });

        try {
            String result = future.get(2, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true); // interrupt task
            System.out.println("Task timed out");
        } finally {
            executor.shutdown();
        }
    }


}
