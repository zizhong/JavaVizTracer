package org.gz.viztracer;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;


public class Agent implements ClassFileTransformer {

    /**
     * classes to always not to instrument
     */
    static final String[] DEFAULT_EXCLUDES = new String[]{"com/sun/", "sun/", "java/", "javax/", "org/slf4j"};

    /**
     * only this classes should instrument or leave empty to instrument all classes that not excluded
     */
    static final String[] INCLUDES = new String[]{
            // "org/bouncycastle/crypto/encodings/", "org/bouncycastle/jce/provider/JCERSACipher"
    };

    public static void premain(final String agentArgument, final Instrumentation instrumentation) {
        instrumentation.addTransformer(new Agent());
    }

    /**
     * instrument class
     */
    public byte[] transform(final ClassLoader loader, final String className, final Class clazz,
                            final java.security.ProtectionDomain domain, final byte[] bytes) {

        for (int i = 0; i < DEFAULT_EXCLUDES.length; i++) {
            if (className.startsWith(DEFAULT_EXCLUDES[i])) {
                return bytes;
            }
        }

        for (String include : INCLUDES) {
            if (className.startsWith(include)) {
                return doClass(className, clazz, bytes);
            }
        }


        return doClass(className, clazz, bytes);
    }

    /**
     * instrument class with javasisst
     */
    private byte[] doClass(final String name, final Class clazz, byte[] b) {
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
            System.err.println("Could not instrument  " + name + ",  exception : " + e.getMessage());
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
    private void doMethod(final CtBehavior method) throws NotFoundException, CannotCompileException {
        method.insertBefore("long _gz_viz_tracer_ts = System.currentTimeMillis();");
        method.insertAfter("Method _gz_viz_tracer_method = new Object(){}.getClass().getEnclosingMethod();\n" +
                "        StringBuilder _gz_viz_tracer_sb = new StringBuilder(128);\n" +
                "        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getDeclaringClass().getName());\n" +
                "        _gz_viz_tracer_sb.append(':');\n" +
                "        _gz_viz_tracer_sb.append(_gz_viz_tracer_method.getName());\n" +
                "        long dur = System.currentTimeMillis() - _gz_viz_tracer_ts;\n" +
                "        VizTracer.getInstance().addEvent(new TraceEvent(_gz_viz_tracer_ts, dur, _gz_viz_tracer_sb.toString()));");
    }
}