package com.complex;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.ThreadFactory;

// Event class
class MessageEvent {
    private String message;

    public void set(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message: " + message;
    }
}

// Event factory (pre-allocates events)
class MessageEventFactory implements com.lmax.disruptor.EventFactory<MessageEvent> {
    public MessageEvent newInstance() {
        return new MessageEvent();
    }
}

// Consumer handler
class MessageHandler implements EventHandler<MessageEvent> {
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Received: " + event + " (seq: " + sequence + ")");
        // Process here...
    }
}

