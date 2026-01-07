package com.complex;


import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.ThreadFactory;

public class DisruptorDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;

        // Ring buffer size must be power of 2
        int bufferSize = 1024;

        Disruptor<MessageEvent> disruptor =
                new Disruptor<>(new MessageEventFactory(), bufferSize, threadFactory);

        disruptor.handleEventsWith(new MessageHandler());
        disruptor.start();

        RingBuffer<MessageEvent> ringBuffer = disruptor.getRingBuffer();

        // Producer loop
        for (long i = 1; i <= 10; i++) {
            long sequence = ringBuffer.next();  // Claim slot
            try {
                MessageEvent event = ringBuffer.get(sequence);
                event.set("Hello #" + i + " @ " + System.currentTimeMillis());
            } finally {
                ringBuffer.publish(sequence);  // Make visible
            }
            Thread.sleep(500);  // Simulate work
        }

        Thread.sleep(2000);  // Wait for processing
        disruptor.shutdown();
    }
}