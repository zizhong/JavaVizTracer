package org.gz.examples;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncExamples {
    public static void sleepFor1Ms() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1);
    }

    public static void main(String[] args) {
        System.out.println("App started");
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            AsyncExamples.fib(7);
        });
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            AsyncExamples.fib(7);
        });
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            AsyncExamples.fib(7);
        });
        CompletableFuture<Void> f4 = CompletableFuture.runAsync(() -> {
            AsyncExamples.fib(7);
        });
        CompletableFuture.allOf(f1, f2, f3, f4).join();
        System.out.println("App Ended");
    }

    private static int fib(int n) {
        try {
            AsyncExamples.sleepFor1Ms();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n < 2 ? 1 : AsyncExamples.fib(n - 1) + AsyncExamples.fib(n - 2);
    }
}
