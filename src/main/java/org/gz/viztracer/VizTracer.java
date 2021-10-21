package org.gz.viztracer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gz.util.CircularBuffer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class VizTracer {
    private static final Logger log = Logger.getLogger(VizTracer.class.getName());
    private static VizTracer INSTANCE;
    private final CircularBuffer<TraceEvent> cb;

    // State Management
    private boolean enabled;
    private boolean saveInProgress;
    private boolean waitForEnable;

    private VizTracer() {
        cb = new CircularBuffer<>(1000000);
        enabled = false;
        saveInProgress = false;
        waitForEnable = false;
    }

    static synchronized VizTracer getInstance() {
        if (VizTracer.INSTANCE == null) VizTracer.INSTANCE = new VizTracer();
        return VizTracer.INSTANCE;
    }

    boolean isEnabled() {
        return enabled;
    }

    void addEvent(TraceEvent e) {
        if (enabled) cb.add(e);
    }

    void enable() {
        if (saveInProgress) waitForEnable = true;
        else {
            enabled = true;
            System.out.println("Trace enabled");
        }
    }

    void disable() {
        if (enabled) {
            saveInProgress = true;
            enabled = false;
            System.out.println("Trace disabled");
            CompletableFuture.supplyAsync(() -> {
                try {
                    return save();
                } catch (IOException e) {
                    VizTracer.log.info("save exception {}" + e.toString());
                }
                return false;
            }).thenAccept(r -> {
                if (waitForEnable) {
                    enabled = true;
                    System.out.println("Trace enabled");
                }
            }).exceptionally(e -> {
                VizTracer.log.info("supplyAsync exception {}" + e.toString());
                e.printStackTrace();
                return null;
            }).join();
        }

    }

    private boolean save() throws IOException {
        ObjectMapper jacksonMapper = new ObjectMapper();
        List<TraceEvent> l = cb.drain();
        //ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        //String jsonStr = ow.writeValueAsString(new TraceJSON(l));
        jacksonMapper.writeValue(new File("C:\\Users\\grain\\workspaces\\JavaVizTracer\\output\\out.json"), new TraceJSON(l));
        return true;
    }

}
