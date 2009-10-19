package org.ogee;

import clojure.lang.RT;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CLJS_DIR = "cljs.dir";
    private ClojureRuntime clojureRuntime;
    private String cljsDir;
    private boolean shutdown = false;
    private BundleContext context;

    public void start(BundleContext context) throws Exception {
        this.context = context;

        cljsDir = context.getProperty(CLJS_DIR);
        cljsDir = cljsDir == null ? "cljs" : cljsDir;

        URL[] initCljs = getAllCljs();
        ClassLoader bundle = new BundleClassLoader(context.getBundle());
        ClassLoader libs = new URLClassLoader(initCljs, bundle);
        clojureRuntime = new ClojureRuntime(libs, getAllCljs());
        clojureRuntime.setThreadContextClassLoader();

        clojureRuntime.init();

        clojureRuntime.loadModule("ogee.osgi");
        RT.var("ogee.osgi", "init").invoke(context);
        
        clojureRuntime.initAllModules();

        startDirWatcher();
    }

    public void stop(BundleContext context) throws Exception {
        this.shutdown = true;
        clojureRuntime.destroy();
    }

    private URL[] getAllCljs() {
        List<URL> found = new ArrayList<URL>();
        findFiles(found, cljsDir);
        return found.toArray(new URL[]{});
    }

    private void findFiles(List<URL> found, String dirName) {
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (f.getName().endsWith(".jar")) {
                    try {
                        found.add(f.toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                findFiles(found, f.getAbsolutePath());
            }
        }
    }

    private void startDirWatcher() {
        final long started = System.currentTimeMillis();
        new Thread() {

            @Override
            public void run() {
                try {
                    while (!Activator.this.shutdown) {
                        Thread.sleep(500);
                        URL[] allCljs = getAllCljs();
                        for (URL url : allCljs) {
                            File f = new File(url.toURI());
                            if (f.lastModified() > started) {
                                Activator.this.shutdown = true;
                                restartOgee();
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }

    private void restartOgee() {
        logger.info("Restarting Ogee...");
        ServiceTracker st = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        st.open();
        ((PackageAdmin) st.getService()).refreshPackages(new Bundle[]{context.getBundle()});
    }
}
