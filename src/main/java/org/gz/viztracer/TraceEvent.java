package org.gz.viztracer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class TraceEvent {
    long processId = 0;
    long threadId = 0;
    long timestamp = 0;
    String method;
    long duration = 0;
    String threadName;

    TraceEvent(long ts, long dur, String name) {
        processId = ProcessHandle.current().pid();
        threadId = Thread.currentThread().getId();
        timestamp = ts;
        method = name;
        duration = dur;
        threadName = Thread.currentThread().getName();
    }

    long sortByTimeStamp() {
        return timestamp;
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
