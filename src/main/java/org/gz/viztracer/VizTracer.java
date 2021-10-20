package org.gz.viztracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.gz.util.CircularBuffer;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
                boolean save;
                try {
                    save = save();
                } catch (JsonProcessingException e) {
                    save = false;
                    e.printStackTrace();
                }
                return save;
            }).thenAccept(r -> {
                if (waitForEnable) enabled = true;
            });
        }

    }

    public boolean save() throws JsonProcessingException {
        ObjectMapper jacksonMapper = new ObjectMapper();
        long threadId = Thread.currentThread().getId();
        AtomicLong idx = cb.index();
        List<TraceEvent> l = cb.drain();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonStr = ow.writeValueAsString(l);
        return true;
    }

}
