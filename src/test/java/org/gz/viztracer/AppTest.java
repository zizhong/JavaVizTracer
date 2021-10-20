package org.gz.viztracer;

import org.junit.Test;

import java.lang.reflect.Method;

public class AppTest {
    private void aSleepyFunction() throws InterruptedException {
        long _gz_viz_tracer_ts = System.currentTimeMillis();
        Method _gz_viz_tracer_method = new Object() {
        }.getClass().getEnclosingMethod();
        StringBuilder _gz_viz_tracer_sb = new StringBuilder(128);
        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getDeclaringClass().getName());
        _gz_viz_tracer_sb.append(':');
        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getName());
        long dur = System.currentTimeMillis() - _gz_viz_tracer_ts;
        org.gz.viztracer.VizTracer.getInstance().addEvent(
                new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts, dur, _gz_viz_tracer_sb.toString()));
    }

    @Test
    public void assistCodeTest() throws InterruptedException {
        VizTracer.getInstance().enable();
        aSleepyFunction();
        VizTracer.getInstance().disable();
    }
}
