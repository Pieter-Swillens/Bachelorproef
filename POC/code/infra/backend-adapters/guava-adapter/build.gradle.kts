description = "guava-adapter"

dependencies {
    implementation(project(":domain"))
    implementation(project(":vocabulary"))

    implementation(libs.guava)

    testImplementation(project(":test-doubles"))
}