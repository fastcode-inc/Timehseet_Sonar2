package com.fastcode.example.addons.scheduler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.*;

public class CGenClassLoader extends ClassLoader {

    public static Map<String, String> retrieveClasses(Path rootDir, String packageName) throws IOException {
        Map<String, String> classFiles = new HashMap<String, String>();
        String packagePath = packageName == null ? "" : packageName.replace('.', '/');
        Files.walkFileTree(
            rootDir,
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String filePath = file.toString().replace('\\', '/');
                    if (
                        filePath.endsWith(".class") &&
                        (packagePath == null || packagePath.isEmpty() || filePath.contains(packagePath))
                    ) {
                        String qalifiedName = packagePath.isEmpty()
                            ? filePath.replace(rootDir.toString().replace('\\', '/') + "/", "")
                            : filePath.substring(filePath.indexOf(packagePath));
                        qalifiedName = qalifiedName.replace(".class", "").replace("/", ".");
                        classFiles.put(qalifiedName, filePath);
                    }
                    System.out.println("Processing file:" + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            }
        );
        return classFiles;
    }

    URLClassLoader classLoader;
    String path = ".";
    String packageName = "";

    public CGenClassLoader(String path) {
        this.path = path;
        System.out.println(" path " + path);
        try {
            classLoader =
                new URLClassLoader(
                    new URL[] { new File(this.path).toURI().toURL() },
                    Thread.currentThread().getContextClassLoader()
                );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> findClass(String qualifiedClassName) throws ClassNotFoundException {
        Class<?> loadedClass = classLoader.loadClass(qualifiedClassName);
        return loadedClass;
    }

    public ArrayList<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        try {
            Map<String, String> classFiles;
            if (this.path.endsWith(".jar")) {
                classFiles = this.findClassesFromJar(packageName);
            } else {
                classFiles = CGenClassLoader.retrieveClasses(Paths.get(this.path), packageName);
            }

            ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

            for (Map.Entry<String, String> entry : classFiles.entrySet()) {
                Class<?> cs = classLoader.loadClass(entry.getKey());
                Class[] interfaces = cs.getInterfaces();
                for (Class str : interfaces) {
                    System.out.println(" a " + str.getName().replace(" ", ""));
                    if (str.getName().replace(" ", "").equals("org.quartz.Job")) {
                        classes.add(cs);
                    }
                }
            }
            return classes;
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(
                packageName + " does not appear to be a valid package (Null pointer exception)"
            );
        } catch (IOException ioex) {
            throw new ClassNotFoundException(
                "IOException was thrown when trying to get all resources for " + packageName
            );
        }
    }

    private Map<String, String> findClassesFromJar(String packageName) {
        try {
            String packagePath = packageName == null ? "" : packageName.replace('.', '/');
            JarFile jarFile = new JarFile(this.path);
            Enumeration e = jarFile.entries();

            Map<String, String> classFiles = new HashMap<String, String>();
            String qalifiedName;
            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();
                String filePath = je.getName();
                System.out.println("filepath:" + filePath);
                if (
                    je.isDirectory() ||
                    (!packagePath.isEmpty() && !filePath.contains(packagePath)) ||
                    !filePath.endsWith(".class")
                ) {
                    continue;
                }
                qalifiedName =
                    filePath.indexOf("classes/") > 0 ? filePath.substring(filePath.indexOf("classes/") + 8) : filePath;
                qalifiedName = qalifiedName.replace(".class", "").replace("/", ".");
                qalifiedName = qalifiedName.replace(".class", "").replace("/", ".");
                classFiles.put(qalifiedName, filePath);
                System.out.println(qalifiedName + ":" + filePath);
            }
            jarFile.close();
            return classFiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
