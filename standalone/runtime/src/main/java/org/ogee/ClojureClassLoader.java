package org.ogee;

import java.net.URL;

public class ClojureClassLoader extends ClassLoader {

    private final ClassLoader bundle;
    private final ClassLoader libs;

    public ClojureClassLoader(ClassLoader bundle, ClassLoader libs) {
        this.bundle = bundle;
        this.libs = libs;
    }

    @Override
    protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return bundle.loadClass(name);
        } catch (Exception e) {
            return libs.loadClass(name);
        }
    }

    @Override
    public URL findResource(String name) {
        URL entry = bundle.getResource(ClassLoaderUtils.convertPath(name));
        if (entry != null) {
            return entry;
        } else {
            return libs.getResource(ClassLoaderUtils.convertPath(name));
        }
    }
}
