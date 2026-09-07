package org.imeaces.keitaload;

import lombok.SneakyThrows;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarFile;

public class KeitaModsClassLoader extends URLClassLoader {
    public KeitaModsClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @SneakyThrows
    public void addJarFile(Path jarFile) {
        super.addURL(returnUrlOfJar(jarFile));
    }

    @SneakyThrows
    private static URL returnUrlOfJar(Path jarFile) {
        checkJarFile(jarFile);
        return jarFile.toUri().toURL();
    }

    @SneakyThrows
    private static void checkJarFile(Path jarPath) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            jarFile.getManifest();

            // ok no problem
        }
    }
}
