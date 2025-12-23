package com.distributed.ecommerce.async;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

// OLD WAY - One thread per connection
class BlockingServer {
    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8080);

        while (true) {
            Socket client = server.accept();  // Blocks

            // Need new thread per client
            new Thread(() -> {
                try {
                    InputStream in = client.getInputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer);  // Blocks thread
                    // Process...
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        // 10,000 clients = 10,000 threads = Problem!
    }
}
