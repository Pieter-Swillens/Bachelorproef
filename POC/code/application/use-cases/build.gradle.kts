description = "use-cases"

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":vocabulary"))

    testImplementation(project(":test-doubles"))
}