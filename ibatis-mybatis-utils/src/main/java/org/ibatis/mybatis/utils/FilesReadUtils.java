package org.ibatis.mybatis.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FilesReadUtils {
    public static final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public static Resource[] getResources(String location) {
        try {
            return resolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    public static List<URL> get(String path) {
        Resource[] rs = getResources(path);
        List<URL> us = new ArrayList<>();
        for (Resource r : rs) {
            try {
                us.add(r.getURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return us;
    }
}
