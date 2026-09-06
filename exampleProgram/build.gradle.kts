plugins {
    `java-library`
}

tasks.getByName<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to "org.imeaces.keitaload.example.ExampleMain"
        )
    }
}
