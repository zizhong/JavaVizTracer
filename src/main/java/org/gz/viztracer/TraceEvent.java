package org.gz.viztracer;

public class TraceEvent {
    private long processId = 0;
    private long threadId = 0;
    private long timestamp = 0;
    private String method;
    private long duration = 0;
    private String threadName;
    public TraceEvent(long ts, long dur, String name) {
        processId = ProcessHandle.current().pid();
        threadId = Thread.currentThread().getId();
        timestamp = ts;
        method = name;
        duration = dur;
        threadName = Thread.currentThread().getName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("[pid:");
        sb.append(processId);
        sb.append(" tid:");
        sb.append(threadId);
        sb.append(" ts:");
        sb.append(timestamp);
        sb.append(" name:");
        sb.append(method);
        sb.append(" dur:");
        sb.append(duration);
        sb.append(" tname:");
        sb.append(threadName);
        sb.append("]");
        return sb.toString();
    }
}
