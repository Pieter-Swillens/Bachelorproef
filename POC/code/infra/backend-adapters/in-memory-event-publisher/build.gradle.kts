description = "in-memory-event-publisher"

dependencies {
    implementation(project(":domain"))
    implementation(project(":vocabulary"))

    testImplementation(project(":test-doubles"))
}