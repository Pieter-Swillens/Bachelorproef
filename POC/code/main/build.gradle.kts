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
    implementation(project(":jakarta-mail-adapter"))

    // TODO - delete following dependencies once 'real' impl has been provided
    implementation(project(":test-doubles"))

    implementation(libs.picocli)
    implementation(libs.bundles.jakartaMail)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}