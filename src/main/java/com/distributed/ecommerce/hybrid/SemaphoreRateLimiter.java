package com.distributed.ecommerce.hybrid;

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SemaphoreRateLimiter {

    private final Semaphore semaphore;
    private final int maxPermits;
    private final ScheduledExecutorService scheduler;

    public SemaphoreRateLimiter(int permitsPerSecond) {
        this.maxPermits = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
        this.scheduler = Executors.newScheduledThreadPool(1);

        // Release permits periodically
        scheduler.scheduleAtFixedRate(() -> {
            int available = semaphore.availablePermits();
            if (available < maxPermits) {
                semaphore.release(maxPermits - available);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
