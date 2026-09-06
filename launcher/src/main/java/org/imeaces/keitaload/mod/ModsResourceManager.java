package org.imeaces.keitaload.mod;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModsResourceManager {
    public static final String KEITA_TRANSFORMS_CONF_FILE = "META-INF/keita-transforms";

    private final Map<Path, List<String>> modsResourcePaths = new HashMap<>();

    public Set<Path> getModsJarFiles() {
        return this.modsResourcePaths.keySet();
    }

    public Set<String> getAllTransformClassNames(){
        return this.modsResourcePaths.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public boolean registerMod(Path modsJarFile) {
        Objects.requireNonNull(modsJarFile, "modsJarFile must not be null");
        Set<String> info;
        try {
            info = fetchTransformModInfo(modsJarFile);
        } catch (IOException e) {
            Logger.warn("unable to read transform mod info of {}", modsJarFile, e);
            return false;
        }
        if (info != null){
            modsResourcePaths.put(modsJarFile, new ArrayList<>(info));
            Logger.info("registered transform mod {} with info {}", modsJarFile, info);
            return true;
        }
        return false;
    }

    @SneakyThrows
    public void registerModsUnderDir(Path modsDir){
        try (Stream<Path> stream = Files.list(modsDir)) {
            for (Iterator<Path> it = stream.iterator(); it.hasNext(); ){
                Path modFilePath = it.next();

                if (!modFilePath.toString().endsWith(".jar")) continue;
                if (!registerMod(modFilePath)){
                    Logger.warn("invalid mod file {}", modFilePath);
                }
            }
        }
    }

    public static void registerModsInsideSystemClasspath(ModsResourceManager mrm){
        final String classpath = System.getProperty("java.class.path");
        if (classpath == null) return;
        for (final String entry : classpath.split(java.io.File.pathSeparator)) {
            Path path = Paths.get(entry);
            if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".jar")) {
                mrm.registerMod(path);
            }
        }
    }

    public static @Nullable Set<String> fetchTransformModInfo(Path modJarPath) throws IOException {
        Set<String> transformClassList = new HashSet<>();

        try (JarFile jar = new JarFile(modJarPath.toFile())) {
            JarEntry entry = jar.getJarEntry(KEITA_TRANSFORMS_CONF_FILE);

            if (entry != null) {
                try (InputStream in = jar.getInputStream(entry)) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8))) {

                        reader.lines()
                                .map(String::trim)
                                .filter(line -> !line.isEmpty())
                                .forEach(transformClassList::add);
                    }
                }
            }
        }

        return transformClassList.isEmpty() ? null : transformClassList;
    }
}
