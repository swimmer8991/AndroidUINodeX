plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":sdk:core"))
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))
    testImplementation("junit:junit:4.12")
}
