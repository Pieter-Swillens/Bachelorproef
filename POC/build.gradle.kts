plugins {
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinPluginSerialization) apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        "implementation"(rootProject.libs.bundles.logging)
        "testImplementation"(platform(rootProject.testlibs.junitBom))
        "testImplementation"(rootProject.testlibs.junitJupiter)
        "testImplementation"(rootProject.testlibs.assertjCore)
        "testRuntimeOnly"(rootProject.testlibs.junitPlatformLauncher)
    }

    tasks.withType<Test> {
        val isSystemTest = name == "systemTest"
        useJUnitPlatform {
            if (isSystemTest) {
                includeTags("system-test")
            } else {
                excludeTags("system-test")
            }
        }
        outputs.cacheIf { false }
        outputs.upToDateWhen { false }
    }
}