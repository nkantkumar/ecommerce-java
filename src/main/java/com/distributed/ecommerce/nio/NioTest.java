package com.distributed.ecommerce.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NioTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        channel.connect(new InetSocketAddress("localhost", 8080)).get();
        Future<Integer> future = channel.write(ByteBuffer.wrap("data".getBytes()));



    }
}
