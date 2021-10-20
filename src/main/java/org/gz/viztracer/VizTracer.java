package org.gz.viztracer;

import org.gz.util.CircularBuffer;

public class VizTracer {
    private static VizTracer INSTANCE;
    private CircularBuffer<TraceEvent> cb;
    private VizTracer() {
        cb = new CircularBuffer<>(10000);
    }
    public static synchronized VizTracer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VizTracer();
        }
        return INSTANCE;
    }
    public void addEvent(TraceEvent e) {
        cb.add(e);
    }
}
