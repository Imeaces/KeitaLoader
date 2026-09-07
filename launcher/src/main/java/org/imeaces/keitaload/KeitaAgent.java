package org.imeaces.keitaload;

import lombok.SneakyThrows;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.jar.JarFile;

public final class KeitaAgent {
    static Instrumentation INSTRUMENTATION;

    public static void premain(String args, Instrumentation instrumentation) {
        KeitaAgent.INSTRUMENTATION = instrumentation;
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        KeitaAgent.INSTRUMENTATION = instrumentation;
    }

    @SneakyThrows
    static void addJarToClasspath(Path jarFilePath) {
        INSTRUMENTATION.appendToSystemClassLoaderSearch(new JarFile(jarFilePath.toFile()));
    }
}