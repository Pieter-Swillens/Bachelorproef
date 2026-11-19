plugins { application }
description = "main"

application {
    mainClass.set("io.pieterswillens.poc.main.CliEntrypointKt")
    applicationName = "poc"
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":vocabulary"))
    implementation(project(":use-cases"))
    implementation(project(":cli-adapter"))
    implementation(project(":guava-adapter"))
    implementation(project(":jakarta-mail-adapter"))
    implementation(project(":in-memory-event-publisher"))

    testImplementation(testFixtures(project(":acceptance-tests-adapter")))

    // TODO - delete following dependencies once 'real' impl of emailRepository has been provided
    implementation(project(":test-doubles"))

    implementation(libs.picocli)
    implementation(libs.bundles.jakartaMail)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    systemProperty("poc.log.dir", rootProject.layout.projectDirectory.dir("logs").asFile.absolutePath)
}

val systemTest by tasks.registering(Test::class) {
    description = "Runs the system tests against the production wiring (slower)"
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    shouldRunAfter(tasks.named("test"))
}