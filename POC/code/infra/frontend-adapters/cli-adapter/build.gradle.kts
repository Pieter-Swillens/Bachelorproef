description = "cli-adapter"

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":vocabulary"))

    implementation(libs.picocli)

    testImplementation(project(":test-doubles"))
}