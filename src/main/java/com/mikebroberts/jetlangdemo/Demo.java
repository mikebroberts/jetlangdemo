package com.mikebroberts.jetlangdemo;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) throws Throwable {
        System.out.println("Hello World");
        Channel<String> channel = new MemoryChannel<>();

        Fiber subscriber1Thread = new ThreadFiber();
        channel.subscribe(subscriber1Thread, createCallback("Subscriber 1"));

        Fiber producer1Thread = new ThreadFiber();
        producer1Thread.scheduleAtFixedRate(createPublisher("Publisher 1", channel), 100, 1000, TimeUnit.MILLISECONDS);

        Fiber producer2Thread = new ThreadFiber();
        producer2Thread.scheduleAtFixedRate(createPublisher("Publisher 2", channel), 100, 1250, TimeUnit.MILLISECONDS);

        subscriber1Thread.start();
        producer1Thread.start();
        producer2Thread.start();

        while(true) {
            Thread.sleep(1000);
        }
    }

    private static Runnable createPublisher(final String publisherName, final Channel<String> channel) {
        return new Runnable() {
            private int count = 0;
            @Override
            public void run() {
                channel.publish(publisherName + " sending message number " + ++count);
            }
        };
    }

    private static Callback<String> createCallback(final String subscriberName) {
        return new Callback<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println(subscriberName + " received message: " + message);
            }
        };
    }
}
