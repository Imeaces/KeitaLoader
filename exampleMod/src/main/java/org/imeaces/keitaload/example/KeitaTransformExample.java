package org.imeaces.keitaload.example;

import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;

@CTransformer(ExampleMain.class)
public class KeitaTransformExample {
    @CInject(method = "method1", target = @CTarget("HEAD"))
    public static void inject$method1(){
        System.out.println("method1 injected!");
    }
}
