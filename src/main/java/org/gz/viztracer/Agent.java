package org.gz.viztracer;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import static java.lang.System.err;
import static java.lang.System.out;


public class Agent implements ClassFileTransformer {

    private static boolean verbose;

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        try {
            new TraceServer();
        } catch (IOException e) {
            out.println("Cannot start TraceServer() " + e.getCause());
            return;
        }
        instrumentation.addTransformer(new Agent());
        verbose = Tracer.getInstance().getTracerConfig().verbose;
    }


    /**
     * instrument class
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class clazz,
                            java.security.ProtectionDomain domain, byte[] bytes) {
        String classNameDir = className.replace('/', '.');

        for (String include : Tracer.getInstance().getTracerConfig().allowClassList) {
            if (include.length() == 0) {
                continue;
            }
            if (className.startsWith(include) || classNameDir.startsWith(include)) {
                return doClass(className, clazz, bytes);
            }
        }
        for (String exclude : Tracer.getInstance().getTracerConfig().denyClassList) {
            if (exclude.length() == 0) {
                continue;
            }
            if (className.startsWith(exclude) || classNameDir.startsWith(exclude)) {

                return bytes;
            }
        }

        return bytes;

    }

    /**
     * instrument class with javasisst
     */
    private byte[] doClass(String name, Class clazz, byte[] b) {
        if (verbose) {
            err.println("Agent.doClass " + name);
        }
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;

        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
            if (cl.isInterface() == false) {
                CtBehavior[] methods = cl.getDeclaredBehaviors();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].isEmpty() == false) {
                        doMethod(methods[i]);
                    }
                }
                b = cl.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            err.println("Could not instrument  " + name + ",  exception : " + e.getMessage());
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }

        return b;
    }

    /**
     * modify code and add log statements before the original method is called
     * and after the original method was called
     */
    private void doMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
        String longMethodName = method.getDeclaringClass().getName() + "." + method.getName();
        if (verbose) {
            err.println("Agent.doMethod " + longMethodName);
        }
        method.addLocalVariable("_gz_viz_tracer_ts", CtClass.longType);
        method.insertBefore("_gz_viz_tracer_ts = System.nanoTime();");
        method.insertAfter("{\n" +
                "    if (org.gz.viztracer.Tracer.getInstance().isEnabled()) {\n" +
                "        long _gz_viz_tracer_dur = System.nanoTime() - _gz_viz_tracer_ts;\n" +
                "        org.gz.viztracer.Tracer.getInstance().addEvent(new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts, _gz_viz_tracer_dur, \"" + longMethodName + "\"));\n" +
                "    }\n" +
                "}");
    }
}