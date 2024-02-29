package org.example;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClasspathResourcesFinder {

    public static List<URL> findResources(String resourcePattern) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(resourcePattern);

        return enumerationAsStream(resources).collect(Collectors.toList());
    }

    private static Stream<URL> enumerationAsStream(Enumeration<URL> enumeration) {
        return Stream.iterate(enumeration.hasMoreElements() ? enumeration.nextElement() : null,
                e -> enumeration.hasMoreElements() ? enumeration.nextElement() : null);
    }

    // 特别处理jar中的资源
    public static List<String> findResourcesInJar(URL jarUrl, String internalPath) throws IOException {
        List<String>  rs=new ArrayList<>();
        JarFile jarFile = new JarFile(new File(jarUrl.getFile()));
        Enumeration<JarEntry> entries = jarFile.entries();
        while(entries.hasMoreElements()){
            JarEntry entry=entries.nextElement();
            if(entry.getName().contains(internalPath)&&entry.getName().endsWith(".xml")){
                rs.add(entry.getName()) ;
            }
        }
        return rs;
    }

    public static void main(String[] args) throws IOException {
        // 模糊匹配并打印类路径下所有mybatis/mappers目录下的.xml文件URL
        List<URL> us= findResources("classpath*:mybatis/mappers/*.xml");
        for (int i = 0; i < us.size(); i++) {
            URL url=us.get(i);
            if (url.getProtocol().equals("jar")) {
                try {
                    findResourcesInJar(url, "mybatis/mappers/").forEach(System.out::println);
                } catch (IOException e) {
                    System.err.println("Error reading from jar: " + e.getMessage());
                }
            } else {
                System.out.println(url);
            }
        }
    }
}