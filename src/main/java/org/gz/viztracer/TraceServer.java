package org.gz.viztracer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

class TraceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String query = t.getRequestURI().getRawQuery();
        System.out.println("Server receives query " + query);
        if (query.contains("enable") || query.contains("1")) {
            VizTracer.getInstance().enable();
        } else {
            VizTracer.getInstance().disable();
        }

        String response = query;
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

public class TraceServer {
    private static Logger logger = Logger.getLogger(TraceServer.class.getName());
    public int port = 11051;
    TraceServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.createContext("/", new TraceHandler());
        server.setExecutor(threadPoolExecutor);
        server.start();
        logger.info(" Server started on port " + port);
    }
}