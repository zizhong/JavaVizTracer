package org.gz.viztracer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TracerConfig {
    private final static String VERBOSE = "org.gz.viztracer.verbose";
    private final static String ENABLE_ON_START = "org.gz.viztracer.enableOnStart";
    private final static String ALLOW_CLASS_LIST = "org.gz.viztracer.allowClassList";
    private final static String DENY_CLASS_LIST = "org.gz.viztracer.denyClassList";
    private final static String TRACE_OUTPUT = "org.gz.viztracer.outputFile";
    private final static String MAX_TRACE_EVENTS = "org.gz.viztracer.maxTraceEvents";
    private final static String MAX_DURATION_IN_NANO = "org.gz.viztracer.minDurationInNano";
    private static final String[] defaultAllowClassList = new String[]{
            "org/gz/examples"
    };
    private static final String[] defaultDenyClassList = new String[]{
            "com/sun/", "sun/", "java/", "javax/", "org/slf4j", "org/gz/viztracer"
    };

    public boolean verbose = false;
    public boolean enableOnStart = false;
    public List<String> allowClassList = new ArrayList<>();
    public List<String> denyClassList = new ArrayList<>();

    public String outputFile = "result.json";
    public int maxTraceEvents = 1000000;
    public long minDurationInNano = 0;

    public TracerConfig() {
        verbose = Boolean.parseBoolean(System.getProperty(VERBOSE, "false"));

        enableOnStart = Boolean.parseBoolean(System.getProperty(ENABLE_ON_START, "false"));

        String allowClassListStr = System.getProperty(ALLOW_CLASS_LIST, "");
        allowClassList.addAll(Arrays.asList(allowClassListStr.split(",")));
        if (allowClassList.size() == 0) {
            allowClassList.addAll(Arrays.asList(defaultAllowClassList));
        }

        String denyClassListStr = System.getProperty(DENY_CLASS_LIST, "");
        denyClassList.addAll(Arrays.asList(defaultDenyClassList));
        denyClassList.addAll(Arrays.asList(denyClassListStr.split(",")));

        outputFile = System.getProperty(TRACE_OUTPUT);
        if (outputFile == null) {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path filePath = Paths.get(currentPath.toString(), "trace.output.json");
            outputFile = filePath.toString();
        }

        maxTraceEvents = Integer.parseInt(System.getProperty(MAX_TRACE_EVENTS, "1000000"));

        minDurationInNano = Long.parseLong(System.getProperty(MAX_DURATION_IN_NANO, "0"));

        if (verbose) {
            System.err.println(VERBOSE + "=true");
            System.err.println(ENABLE_ON_START + "=" + enableOnStart);
            System.err.println(ALLOW_CLASS_LIST + "=" + allowClassList);
            System.err.println(DENY_CLASS_LIST + "=" + denyClassList);
            System.err.println(TRACE_OUTPUT + "=" + outputFile);
            System.err.println(MAX_TRACE_EVENTS + "=" + maxTraceEvents);
            System.err.println(MAX_DURATION_IN_NANO + "=" + minDurationInNano);
        }
    }

}
