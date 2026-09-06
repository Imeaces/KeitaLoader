import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.2.2"
}

dependencies {
    api("net.lenni0451.classtransform:core:1.15.1")
    implementation("net.lenni0451.classtransform:mixinstranslator:1.15.1")

    compileOnlyApi("net.lenni0451.classtransform:mixinsdummy:1.15.1")

    implementation("com.google.code.gson:gson:2.13.1")

    api("org.tinylog:tinylog-api:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    compileOnly("org.jetbrains:annotations:26.0.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = 8
    }
}

tasks.getByName<Jar>("jar") {
    manifest {
        attributes(
            "Premain-Class" to "org.imeaces.keitaload.KeitaAgent",
            "Agent-Class" to "org.imeaces.keitaload.KeitaAgent",
            "Launcher-Agent-Class" to "org.imeaces.keitaload.KeitaAgent",
            "Main-Class" to "org.imeaces.keitaload.KeitaLaunchJarWithMain",

            "Specification-Title" to "KeitaLoader",
            "Specification-Version" to project.version,
            "Specification-Vendor" to "org.imeaces",

            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "org.imeaces",
        )
    }
}

tasks.getByName<ShadowJar>("shadowJar") {
    mergeServiceFiles()

    relocate("com.google.gson", "org.imeaces.keitaload.shadow.com.google.gson")
}

tasks.named("assemble") {
    dependsOn("shadowJar")
}
