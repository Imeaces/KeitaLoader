package org.imeaces.keitaload;

import net.lenni0451.classtransform.utils.tree.IClassProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class KeitaTransformModClassProvider implements IClassProvider {
    private final KeitaLoader keitaLoader;

    public KeitaTransformModClassProvider(KeitaLoader keitaLoader) {
        this.keitaLoader = keitaLoader;
    }

    @Override
    public byte @NotNull [] getClass(String name) throws ClassNotFoundException {
        return keitaLoader.getClassBytes(name);
    }

    @Override
    public @NotNull Map<String, Supplier<byte[]>> getAllClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
