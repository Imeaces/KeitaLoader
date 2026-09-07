package org.imeaces.keitaload;

import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.JarFile;

public class KeitaLaunchJarWithMain {
    @SneakyThrows
    public static void main(String[] args){
        if (args.length < 1) {
            System.err.println("Usage: KeitaLaunchJarWithMain <jar file path> [...ARGS]");
            System.exit(2);
        }

        Path programJarFile = Paths.get(args[0]);
        String jarMainClass;

        String[] programArgs = args.length > 1
                ? Arrays.copyOfRange(args, 1, args.length)
                : new String[0];

        try (JarFile jar = new JarFile(programJarFile.toFile())){
            jarMainClass = jar.getManifest()
                    .getMainAttributes()
                    .getValue("Main-Class");
        }

        if (KeitaConfigurations.LOAD_CLASSPATH_MODS && KeitaConfigurations.STANDALONE_MODS_CLASSLOADER) {
            Logger.warn("Warning! Using load-classpath-mods with standalone-mods-classloader together may cause unforeseen consequences!");
        }

        KeitaLoader keitaLoader = new KeitaLoader(KeitaConfigurations.STANDALONE_MODS_CLASSLOADER);
        keitaLoader.addJarFile(programJarFile);
        keitaLoader.scanTransformMods(KeitaConfigurations.LOAD_CLASSPATH_MODS);
        keitaLoader.launchMain(jarMainClass, programArgs);
    }
}
