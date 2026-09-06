package org.imeaces.keitaload;

import lombok.SneakyThrows;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.utils.ASMUtils;
import org.imeaces.keitaload.mod.ModsResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class KeitaLoader {
    private final TransformerManager transformerManager;
    private final KeitaModsClassLoader modsLoader;
    private final ModsResourceManager resourceManager;

    public KeitaLoader() {
        this.transformerManager = new TransformerManager(new KeitaTransformModClassProvider(this));
        this.modsLoader = new KeitaModsClassLoader();
        this.resourceManager = new ModsResourceManager();
    }

    public void addJarFile(Path jarFile) {
        modsLoader.addJarFile(jarFile);
    }

    public byte[] getClassBytes(String name) throws ClassNotFoundException {
        try (InputStream in = modsLoader.getResourceAsStream(ASMUtils.slash(name) + ".class")) {
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

    public void scanTransformMods(boolean searchSystemClasspath){
        if (searchSystemClasspath){
            ModsResourceManager.registerModsInsideSystemClasspath(resourceManager);
        }
        resourceManager.registerModsUnderDir(Paths.get("mods"));
    }

    @SneakyThrows
    public void launchMain(String entrypoint, String[] args){
        Objects.requireNonNull(KeitaAgent.INSTRUMENTATION, "agent instrumentation missing, did you forget to append KeitaAgent?");

        resourceManager.getModsJarFiles().forEach(modsLoader::addJarFile);
        resourceManager.getAllTransformClassNames().forEach(transformerManager::addTransformer);

        transformerManager.hookInstrumentation(KeitaAgent.INSTRUMENTATION);

        Method entrypointMain = modsLoader.loadClass(entrypoint).getDeclaredMethod("main", String[].class);
        // Java 25 允许 package-private 的 main 方法作为程序入口，需要扩展访问
        entrypointMain.setAccessible(true);
        entrypointMain.invoke(null, (Object) args);
    }
}
