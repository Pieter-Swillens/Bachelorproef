plugins {
    `java-test-fixtures`
}

description = "acceptance-tests"

dependencies {
    testFixturesApi(project(":api"))
    testFixturesApi(project(":domain"))
    testFixturesApi(project(":test-doubles"))
    testFixturesApi(project(":vocabulary"))

    testFixturesImplementation(platform(testlibs.junitBom))
    testFixturesImplementation(testlibs.junitJupiter)
    testFixturesImplementation(testlibs.assertjCore)

    testImplementation(project(":use-cases"))
}