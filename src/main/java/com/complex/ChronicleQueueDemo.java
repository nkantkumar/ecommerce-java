package com.complex;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ChronicleQueueDemo {

    private static final String QUEUE_PATH = "Users/nishi/Documents/GitHub/chronicle-queue-demo";

    public static void main(String[] args) throws InterruptedException {
        // Clean up old queue files (for demo purposes)
        ChronicleQueue queue = SingleChronicleQueueBuilder.binary(QUEUE_PATH).build();

        // Start a reader thread
        Thread readerThread = new Thread(() -> {
            try (ChronicleQueue q = SingleChronicleQueueBuilder.binary(QUEUE_PATH).build()) {
                ExcerptTailer tailer = q.createTailer();

                System.out.println("Reader started, waiting for messages...");
                while (true) {
                    try (DocumentContext dc = tailer.readingDocument()) {
                        if (dc.isPresent()) {
                            String message = dc.wire().read("msg").text();
                            long timestamp = dc.wire().read("ts").int64();
                            System.out.printf("Received: %s (ts: %d)%n", message, timestamp);
                        }
                    }

                    // Small sleep to avoid busy looping (in prod, use polling or indexing)
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();

        // Writer in main thread
        try (ExcerptAppender appender = queue.createAppender()) {
            for (int i = 1; i <= 10; i++) {
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire()
                            .write("msg").text("Hello Chronicle Queue! Message #" + i)
                            .write("ts").int64(System.currentTimeMillis());
                }
                System.out.println("Sent message #" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("Writer finished. Reader will continue to read if restarted.");
    }
}
