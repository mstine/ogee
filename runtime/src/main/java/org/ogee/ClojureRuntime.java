package org.ogee;

import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.lang.RT;

public class ClojureRuntime {

    private final Logger logger = LoggerFactory.getLogger(ClojureRuntime.class);
    private final URL[] initCljs;
    private ClassLoader classLoader;

    public ClojureRuntime(ClassLoader classLoader, URL[] initCljs) throws Exception {
        this.classLoader = classLoader;
        this.initCljs = initCljs;
    }

    public void initAllModules() throws Exception {
//        loadModule("ogee");

        for (URL clj : initCljs) {
            JarFile jar = new JarFile(new File(clj.toURI()));
            String cm = jar.getManifest().getMainAttributes().getValue("Clojure-Module");
            if (cm == null) {
                continue;
            }

            try {
                loadModule(cm);
                moduleStarted(cm);
            } catch (Exception e) {
                logger.error("Error while starting module " + cm, e);
            }
        }
    }

    public void destroy() throws Exception {
        RT.var("ogee", "ogee-stop").invoke();
    }

    public void setThreadContextClassLoader() {
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public void loadModule(String name) throws Exception {
        RT.load(name.replace('.', '/'));
    }

    public void moduleStarted(String mainModule) throws Exception {
        RT.var(mainModule, "init").invoke();
    }

}
