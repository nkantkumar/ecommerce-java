package com.distributed.ecommerce.async;

public class Singleton {
    private static volatile Singleton instance;

    public static Singleton getInstance() {
        if (instance == null) {  // First check (no lock)
            synchronized (Singleton.class) {
                if (instance == null) {  // Second check (with lock)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
