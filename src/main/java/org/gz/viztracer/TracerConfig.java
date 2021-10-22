package org.gz.viztracer;

import java.util.ArrayList;
import java.util.List;

class TracerConfig {
    private static TracerConfig INSTANCE;
    public boolean enableOnStart = false;
    public List<String> denyClassList = new ArrayList<>();
    public List<String> allowClassList = new ArrayList<>();
    public String outputFile = "result.json";
    public long traceEntries = 1000000;
    public double minDuration = 0;

    private TracerConfig() {
    }

    static synchronized TracerConfig getInstance() {
        if (TracerConfig.INSTANCE == null) TracerConfig.INSTANCE = new TracerConfig();
        return TracerConfig.INSTANCE;
    }

    public void init(String options) {

    }
}
