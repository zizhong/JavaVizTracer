package org.example;

import static org.junit.Assert.assertTrue;

import org.gz.viztracer.TraceEvent;
import org.gz.viztracer.VizTracer;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    private void aSleepyFunction() throws InterruptedException {
        long ts = System.currentTimeMillis();
        long pid = ProcessHandle.current().pid();
        long tid = Thread.currentThread().getId();
        Method method = new Object(){}.getClass().getEnclosingMethod();
        StringBuilder sb = new StringBuilder(128);
        sb.append(method.getDeclaringClass().getName());
        sb.append(':');
        sb.append(method.getName());
        long dur = System.currentTimeMillis() - ts;
        VizTracer.getInstance().addEvent(new TraceEvent(ts, dur, sb.toString()));
    }

    @Test
    public void assistCodeTest() throws InterruptedException {
        aSleepyFunction();
    }
}
