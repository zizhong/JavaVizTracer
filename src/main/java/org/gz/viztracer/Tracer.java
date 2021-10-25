package org.gz.viztracer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gz.util.CircularBuffer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Tracer {
    private static final Logger log = Logger.getLogger(Tracer.class.getName());
    private static Tracer INSTANCE;
    private final TracerConfig tracerConfig;
    private CircularBuffer<TraceEvent> cb;
    // State Management
    private boolean enabled;
    private boolean saveInProgress;
    private boolean waitForEnable;

    private Tracer() {
        tracerConfig = new TracerConfig();
        cb = new CircularBuffer<>(tracerConfig.maxTraceEvents);
        enabled = tracerConfig.enableOnStart;
        saveInProgress = false;
        waitForEnable = false;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disable();
        }));
    }

    public static Tracer getInstance() {
        if (Tracer.INSTANCE == null) {
            Tracer.INSTANCE = new Tracer();
        }
        return Tracer.INSTANCE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addEvent(TraceEvent e) {
        if (enabled && e.duration >= tracerConfig.minDurationInMs) {
            cb.add(e);
        }
    }

    public void enable() {
        if (saveInProgress) {
            waitForEnable = true;
        } else {
            enabled = true;
            System.out.println("Trace enabled");
        }
    }

    public void disable() {
        if (enabled) {
            saveInProgress = true;
            enabled = false;
            System.out.println("Trace disabled");
            CompletableFuture.supplyAsync(() -> {
                try {
                    return save();
                } catch (IOException e) {
                    Tracer.log.info("save exception {}" + e.toString());
                }
                return false;
            }).thenAccept(r -> {
                if (waitForEnable) {
                    enabled = true;
                    System.out.println("Trace enabled");
                }
            }).exceptionally(e -> {
                Tracer.log.info("supplyAsync exception {}" + e.toString());
                e.printStackTrace();
                return null;
            }).join();
            saveInProgress = false;
        }

    }

    public boolean save() throws IOException {
        ObjectMapper jacksonMapper = new ObjectMapper();
        List<TraceEvent> l = cb.drain();
        jacksonMapper.writeValue(new File(tracerConfig.outputFile), new TraceJSON(l));
        System.out.println("Wrote " + l.size() + " trace events into " + tracerConfig.outputFile);
        return true;
    }

    public TracerConfig getTracerConfig() {
        return tracerConfig;
    }
}
