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
include(":use-cases")
project(":use-cases").projectDir = file("code/application/use-cases")
include(":api")
project(":api").projectDir = file("code/application/api")

include(":guava-adapter")
project(":guava-adapter").projectDir = file("code/infra/backend-adapters/guava-adapter")
include(":jakarta-mail-adapter")
project(":jakarta-mail-adapter").projectDir = file("code/infra/backend-adapters/jakarta-mail-adapter")

include(":acceptance-tests-adapter")
project(":acceptance-tests-adapter").projectDir = file("code/infra/frontend-adapters/acceptance-tests")

include(":test-doubles")
project(":test-doubles").projectDir = file("code/test-doubles")
include(":vocabulary")
project(":vocabulary").projectDir = file("code/vocabulary")
include(":scripts")
project(":scripts").projectDir = file("code/scripts")