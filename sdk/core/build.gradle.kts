plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    // Enable JUnit 5 (Jupiter) support
    useJUnitPlatform()
}
