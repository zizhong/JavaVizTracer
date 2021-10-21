package org.gz.viztracer;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;


public class Agent implements ClassFileTransformer {

    /**
     * classes to always not to instrument
     */
    private static final String[] DEFAULT_EXCLUDES = new String[]{"com/sun/", "sun/", "java/", "javax/", "org/slf4j"};

    /**
     * only this classes should instrument or leave empty to instrument all classes that not excluded
     */
    private static final String[] INCLUDES = new String[]{
            // "org/bouncycastle/crypto/encodings/", "org/bouncycastle/jce/provider/JCERSACipher"
    };

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        try {
            new TraceServer();
        } catch (IOException e) {
            System.out.println("Cannot start TraceServer() " + e.getCause());
            return;
        }
        instrumentation.addTransformer(new Agent());
    }

    /**
     * instrument class
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class clazz,
                            java.security.ProtectionDomain domain, byte[] bytes) {

        for (int i = 0; i < Agent.DEFAULT_EXCLUDES.length; i++)
            if (className.startsWith(Agent.DEFAULT_EXCLUDES[i])) return bytes;

        for (String include : Agent.INCLUDES)
            if (className.startsWith(include)) return doClass(className, clazz, bytes);


        return doClass(className, clazz, bytes);
    }

    /**
     * instrument class with javasisst
     */
    private byte[] doClass(String name, Class clazz, byte[] b) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;

        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));

            if (cl.isInterface() == false) {

                CtBehavior[] methods = cl.getDeclaredBehaviors();

                for (int i = 0; i < methods.length; i++) if (methods[i].isEmpty() == false) doMethod(methods[i]);

                b = cl.toBytecode();
            }
        } catch (Exception e) {
            System.err.println("Could not instrument  " + name + ",  exception : " + e.getMessage());
        } finally {

            if (cl != null) cl.detach();
        }

        return b;
    }

    /**
     * modify code and add log statements before the original method is called
     * and after the original method was called
     */
    private void doMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
        method.insertBefore("long _gz_viz_tracer_ts = System.currentTimeMillis();");
        method.insertAfter("Method _gz_viz_tracer_method = new Object(){}.getClass().getEnclosingMethod();\n" +
                "        StringBuilder _gz_viz_tracer_sb = new StringBuilder(128);\n" +
                "        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getDeclaringClass().getName());\n" +
                "        _gz_viz_tracer_sb.append(':');\n" +
                "        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getName());\n" +
                "        long dur = System.currentTimeMillis() - _gz_viz_tracer_ts;\n" +
                "        org.gz.viztracer.VizTracer.getInstance().addEvent(\n" +
                "        new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts, dur, _gz_viz_tracer_sb.toString()));");
    }
}