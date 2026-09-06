package org.imeaces.keitaload;

import java.lang.instrument.Instrumentation;

public final class KeitaAgent {
    static Instrumentation INSTRUMENTATION;

    public static void premain(String args, Instrumentation instrumentation) {
        KeitaAgent.INSTRUMENTATION = instrumentation;
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        KeitaAgent.INSTRUMENTATION = instrumentation;
    }
}