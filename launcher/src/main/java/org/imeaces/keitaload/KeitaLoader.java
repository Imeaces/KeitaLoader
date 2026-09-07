package org.imeaces.keitaload;

import lombok.SneakyThrows;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.classtransform.utils.ASMUtils;
import org.imeaces.keitaload.mod.ModsResourceManager;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class KeitaLoader {
    private final TransformerManager transformerManager;
    private final @Nullable KeitaModsClassLoader modsLoader;
    private final ModsResourceManager resourceManager;

    public KeitaLoader(boolean standaloneModsClassloader) {
        this.transformerManager = new TransformerManager(new KeitaTransformModClassProvider(this));
        if (standaloneModsClassloader) {
            this.modsLoader = new KeitaModsClassLoader(ClassLoader.getSystemClassLoader());
        } else {
            this.modsLoader = null;
        }
        this.resourceManager = new ModsResourceManager();

        transformerManager.addTransformerPreprocessor(new MixinsTranslator());
    }

    public void addJarFile(Path jarFile) {
        if (modsLoader != null) {
            modsLoader.addJarFile(jarFile);
        } else {
            KeitaAgent.addJarToClasspath(jarFile);
        }
    }

    public InputStream getResourceAsStream(String resourcePath) {
        if (modsLoader != null) {
            return modsLoader.getResourceAsStream(resourcePath);
        } else {
            return ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        }
    }

    public byte[] getClassBytes(String name) throws ClassNotFoundException {
        try (InputStream in = getResourceAsStream(ASMUtils.slash(name) + ".class")) {
            if (in == null) throw new ClassNotFoundException(name);

            ByteArrayOutputStream classBytes = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) classBytes.write(buf, 0, len);
            return classBytes.toByteArray();
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @SneakyThrows
    public void scanTransformMods(boolean searchSystemClasspath){
        if (searchSystemClasspath){
            ModsResourceManager.registerModsInsideSystemClasspath(resourceManager);
        }
        Path modsDir = Paths.get("mods");
        if (Files.notExists(modsDir)) {
            Files.createDirectories(modsDir);
        }
        resourceManager.registerModsUnderDir(modsDir);
    }

    @SneakyThrows
    public void launchMain(String entrypoint, String[] args){
        Logger.info("running {} v{}", KeitaConstants.SPEC_TITLE, KeitaConstants.SPEC_VERSION);

        Objects.requireNonNull(KeitaAgent.INSTRUMENTATION, "agent instrumentation missing, did you forget to append KeitaAgent?");

        Logger.info("adding {} mod(s)", resourceManager.getModsJarFiles().size());
        if (modsLoader != null) {
            resourceManager.getModsJarFiles().forEach(modsLoader::addJarFile);
        } else {
            resourceManager.getModsJarFiles().forEach(KeitaAgent::addJarToClasspath);
        }
        resourceManager.getAllTransformClassNames().forEach(transformerManager::addTransformer);

        if (modsLoader != null) {
            Thread.currentThread().setContextClassLoader(modsLoader);
        }
        transformerManager.hookInstrumentation(KeitaAgent.INSTRUMENTATION);

        Logger.info("running entrypoint {}", entrypoint);
        Method entrypointMain = Thread.currentThread().getContextClassLoader()
                .loadClass(entrypoint).getDeclaredMethod("main", String[].class);

        // Java 25 允许 package-private 的 main 方法作为程序入口，需要扩展访问
        entrypointMain.setAccessible(true);
        entrypointMain.invoke(null, (Object) args);
    }
}
