rootProject.name = "POC"

dependencyResolutionManagement {

    versionCatalogs {
        // libs.versions.toml discovered automagically
        create("testlibs") {
            from(files("gradle/testlibs.versions.toml"))
        }
    }
}

include(":domain")
project(":domain").projectDir = file("code/application/domain")

include(":vocabulary")
project(":vocabulary").projectDir = file("code/vocabulary")