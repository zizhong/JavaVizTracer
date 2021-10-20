package org.gz.viztracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AppTest {
    private void aSleepyFunction() throws JsonProcessingException {
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

        List<TraceEvent> l = new ArrayList<>();
        l.add(new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts, dur, _gz_viz_tracer_sb.toString()));
        l.add(new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts-2, dur, _gz_viz_tracer_sb.toString()));
        l.sort(Comparator.comparingLong(TraceEvent::sortByTimeStamp));
        ObjectMapper jacksonMapper = new ObjectMapper();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonStr = ow.writeValueAsString(l);
        System.out.println(jsonStr);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void assistCodeTest() throws JsonProcessingException {
        VizTracer.getInstance().enable();
        aSleepyFunction();
        VizTracer.getInstance().disable();
    }
}
