plugins {
    `java-library`
    signing
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.java.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.commons.io)
    testImplementation(libs.guava)
    testImplementation(libs.junit)
}

group = "org.danilopianini"
publishOnCentral {
    projectDescription.set(extra["projectDescription"].toString())
    projectUrl.set("https://github.com/DanySK/Thread-Inheritable-Resource-Loader-for-Java")
    projectLongName.set("Thread Inheritable Resource Loader for Java")
    scmConnection.set("scm:git:https://github.com/DanySK/Thread-Inheritable-Resource-Loader-for-Java")
}

if (System.getenv("CI") == true.toString()) {
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                developers {
                    developer {
                        name.set("Matteo Magnani")
                    }
                    developer {
                        name.set("Danilo Pianini")
                        email.set("danilo.pianini@gmail.com")
                        url.set("http://www.danilopianini.org/")
                    }
                }
            }
        }
    }
}

tasks.withType<Checkstyle>().configureEach {
    multiJvm {
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(latestJavaSupportedByGradle))
            }
        )
    }
}
