description = "jakarta-mail-adapter"

dependencies {
    implementation(project(":domain"))
    implementation(project(":vocabulary"))

    implementation(libs.bundles.jakartaMail)
}