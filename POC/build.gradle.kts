import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

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
        "testImplementation"(platform(rootProject.testlibs.junitBom))
        "testImplementation"(rootProject.testlibs.junitJupiter)
        "testImplementation"(rootProject.testlibs.assertjCore)
        "testRuntimeOnly"(rootProject.testlibs.junitPlatformLauncher)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        outputs.cacheIf { false }
        outputs.upToDateWhen { false }
    }
}