package com.distributed.ecommerce.async;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<Integer> queue= new LinkedBlockingQueue<>(10);

        Runnable producer1 = ()-> {
            Random rand = new Random();
            try {
                for (int i = 0; i < 50; i++) {
                    int num = rand.nextInt(100);
                    queue.put(num);
                    System.out.println("PRODUCER: added " + num);
                    //Thread.sleep(rand.nextInt(100));
                }
                queue.put(-1);  // End signal
                System.out.println("PRODUCER: stopped.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumer1 = ()-> {
            try {
                while (true) {
                    int num = queue.take();
                    if (num == -1) {
                        System.out.println("CONSUMER: stopped.");
                        break;
                    }
                    System.out.println("CONSUMER: processed " + num);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

       ExecutorService exe=  Executors.newFixedThreadPool(2);

       exe.submit(producer1);
       exe.submit(consumer1);

       exe.shutdown();
       exe.awaitTermination(2, TimeUnit.SECONDS);
    }

}
