package org.gz.viztracer;

import org.gz.util.CircularBuffer;

import java.util.concurrent.CompletableFuture;

public class VizTracer {
    private static VizTracer INSTANCE;
    private final CircularBuffer<TraceEvent> cb;

    // State Management
    private boolean enabled;
    private boolean saveInProgress;
    private boolean waitForEnable;

    private VizTracer() {
        cb = new CircularBuffer<>(10000);
        enabled = false;
        saveInProgress = false;
        waitForEnable = false;
    }

    public static synchronized VizTracer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VizTracer();
        }
        return INSTANCE;
    }

    public void addEvent(TraceEvent e) {
        if (enabled) {
            cb.add(e);
        }
    }

    public void enable() {
        if (saveInProgress) {
            waitForEnable = true;
        } else {
            enabled = true;
        }
    }

    public void disable() {
        if (enabled) {
            saveInProgress = true;
            enabled = false;
            CompletableFuture.supplyAsync(() -> {
                save();
                return true;
            }).thenAccept(r -> {
                System.out.println("Save() returns " + r);
                if (waitForEnable) enabled = true;
            });
        }

    }

    public void save() {
        long threadId = Thread.currentThread().getId();
        System.out.println("save on thread " + threadId);
    }

}
