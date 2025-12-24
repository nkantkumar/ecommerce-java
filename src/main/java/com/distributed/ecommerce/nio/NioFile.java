package com.distributed.ecommerce.nio;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class NioFile {
    private static final long LARGE_FILE_THRESHOLD = 10 * 1024 * 1024; // 10MB

    public void processFile(File file) throws IOException {
        if (file.length() > LARGE_FILE_THRESHOLD) {
            processWithNIO(file);  // Efficient for large files
        } else {
            processWithIO(file);   // Simpler for small files
        }
    }

    // IO: Simple, readable code for small files
    private void processWithIO(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        }
    }

    // NIO: Memory-mapped for large files (faster)
    private void processWithNIO(File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    0,
                    channel.size()
            );

            // Process buffer directly from memory
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                // Process byte
            }
        }
    }

    private void processLine(String line) {
        // Business logic
    }
}
