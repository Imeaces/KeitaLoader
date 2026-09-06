package org.imeaces.keitaload;

import lombok.SneakyThrows;

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

        KeitaLoader keitaLoader = new KeitaLoader();
        keitaLoader.addJarFile(programJarFile);
        keitaLoader.scanTransformMods(true);
        keitaLoader.launchMain(jarMainClass, programArgs);
    }
}
