description = "acceptance-tests"

dependencies {
    testImplementation(project(":api"))
    testImplementation(project(":use-cases"))
    testImplementation(project(":domain"))
    testImplementation(project(":test-doubles"))
    testImplementation(project(":vocabulary"))
}