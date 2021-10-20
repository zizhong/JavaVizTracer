package org.gz.viztracer;

public class VizTracer {
    private static VizTracer INSTANCE;
    private VizTracer() {}
    public static synchronized VizTracer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VizTracer();
        }
        return INSTANCE;
    }
    public void addEvent(TraceEvent e) {
        System.out.println(e);
    }
}
