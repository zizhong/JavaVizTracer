package org.gz.viztracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

interface TraceClient {
    @GET("/trace")
    CompletableFuture<String> get(@Query("cmd") String apiKey);
}

public class AppTest {
    private static void aSleepyFunction() throws JsonProcessingException {
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
        org.gz.viztracer.VizTracer.getInstance().addEvent(
                new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts + 1, dur - 1, _gz_viz_tracer_sb.toString() + "1"));
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void assistCodeTest() throws JsonProcessingException {
        VizTracer.getInstance().enable();
        AppTest.aSleepyFunction();
        VizTracer.getInstance().disable();
    }

    @Test
    public void serverTest() throws ExecutionException, InterruptedException {
        TraceServer server;
        try {
            server = new TraceServer();
        } catch (IOException e) {
            System.out.println("Cannot start TraceServer() " + e.getCause());
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1:" + server.port)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TraceClient client = retrofit.create(TraceClient.class);

        assert !VizTracer.getInstance().isEnabled();
        CompletableFuture<String> response = client.get("1");
        String query = response.get();
        assert query.contains("1");
        assert VizTracer.getInstance().isEnabled();
        CompletableFuture<String> response2 = client.get("0");
        String query2 = response2.get();
        assert query2.contains("0");
        assert !VizTracer.getInstance().isEnabled();
    }
}