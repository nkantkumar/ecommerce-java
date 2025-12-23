package com.distributed.ecommerce.async;

import java.util.concurrent.*;

public class CompFuTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor=  Executors.newFixedThreadPool(2);
        Future<String> future = executor.submit(() -> {
            String result = "result";
            return result;
        });
        String result = future.get(); // Blocks!

// CompletableFuture - non-blocking, composable
        Object id = null;
        CompletableFuture.supplyAsync(() -> fetchUser(id))
                //.thenApply(user -> user.getOrders())
                .thenCompose(orders -> enrichOrders(orders))
                .thenAccept(System.out::println)
                .exceptionally(ex -> {
                    return null;
                });
    }

    private static Void handleError(Throwable ex) {
        return null;
    }

    private static CompletionStage<Object> enrichOrders(Object orders) {
        return null;
    }
    private static CompletionStage<Object> fetchUser(Object id) {
        return null;
    }
}
