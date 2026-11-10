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

include(":guava-adapter")
project(":guava-adapter").projectDir = file("code/infra/backend-adapters/guava-adapter")

include(":test-doubles")
project(":test-doubles").projectDir = file("code/test-doubles")
include(":vocabulary")
project(":vocabulary").projectDir = file("code/vocabulary")
include(":scripts")
project(":scripts").projectDir = file("code/scripts")