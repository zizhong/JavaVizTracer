package org.gz.viztracer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

class TraceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String query = t.getRequestURI().getRawQuery();
        System.out.println("Server receives query " + query);
        if (query.contains("enable") || query.contains("1")) {
            Tracer.getInstance().enable();
        } else {
            Tracer.getInstance().disable();
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
        CompletableFuture.runAsync(() -> {
            HttpServer server = null;
            try {
                server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1,
                    new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });
            server.createContext("/", new TraceHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            logger.info(" Server started on port " + port);
            HttpServer finalServer = server;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                finalServer.stop(1);
                threadPoolExecutor.shutdown();
            }));
        });
    }
}
