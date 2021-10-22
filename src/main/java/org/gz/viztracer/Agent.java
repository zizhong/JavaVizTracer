package org.gz.viztracer;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import static java.lang.System.err;
import static java.lang.System.out;


public class Agent implements ClassFileTransformer {

    /**
     * classes to always not to instrument
     */
    private static final String[] DEFAULT_EXCLUDES = new String[]{"com/sun/", "sun/", "java/", "javax/", "org/slf4j"};

    /**
     * only this classes should instrument or leave empty to instrument all classes that not excluded
     */
    private static final String[] INCLUDES = new String[]{
            "org/gz/examples"
            // "org/bouncycastle/crypto/encodings/", "org/bouncycastle/jce/provider/JCERSACipher"
    };

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        try {
            new TraceServer();
        } catch (IOException e) {
            out.println("Cannot start TraceServer() " + e.getCause());
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
        out.println("transform " + className);
        String classNameDir = className.replace('.', '/');
        for (int i = 0; i < Agent.DEFAULT_EXCLUDES.length; i++) {
            if (className.startsWith(Agent.DEFAULT_EXCLUDES[i]) || classNameDir.startsWith(Agent.DEFAULT_EXCLUDES[i])) {
                return bytes;
            }
        }

        for (String include : Agent.INCLUDES) {
            if (className.startsWith(include) || classNameDir.startsWith(include)) {
                return doClass(className, clazz, bytes);
            }
        }
        return bytes;

        //return doClass(className, clazz, bytes);
    }

    /**
     * instrument class with javasisst
     */
    private byte[] doClass(String name, Class clazz, byte[] b) {
        out.println("doClass " + name);

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
        out.println("doMethod " + method.getName());
        String longMethodName = method.getDeclaringClass().getName() + "." + method.getName();
        method.addLocalVariable("_gz_viz_tracer_ts", CtClass.longType);
        method.insertBefore("_gz_viz_tracer_ts = System.currentTimeMillis();");
        method.insertAfter("{\n" +
                "        long _gz_viz_tracer_dur = System.currentTimeMillis() - _gz_viz_tracer_ts;\n" +
                "        org.gz.viztracer.VizTracer.getInstance().addEvent(new org.gz.viztracer.TraceEvent(_gz_viz_tracer_ts, _gz_viz_tracer_dur, \"" + longMethodName + "\"));\n" +
                "}");
    }
}