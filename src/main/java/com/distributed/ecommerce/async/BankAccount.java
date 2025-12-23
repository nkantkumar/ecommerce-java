package com.distributed.ecommerce.async;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount {
    private final ReentrantLock lock = new ReentrantLock();
    private double balance;

    // 1. Timeout capability
    public boolean transfer(BankAccount target, double amount) {
        try {
            // Try to acquire both locks with timeout
            if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (target.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            this.balance -= amount;
                            target.balance += amount;
                            return true;
                        } finally {
                            target.lock.unlock();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    // 2. Fairness policy
    private final ReentrantLock fairLock = new ReentrantLock(true);

    // 3. Condition variables
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

}
