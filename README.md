# AndroidUINodeX

**AndroidUINodeX** is a production-grade Android Accessibility Scanning SDK designed to automate the audit of your app's user interface. It detects common accessibility issues in both traditional **XML Views** and **Jetpack Compose**.

## 🚀 Features

-   **Multi-Framework Support**: Seamlessly scans both View-based layouts and Jetpack Compose UIs.
-   **Rule-Based Engine**: Extensible engine that applies standard and custom accessibility rules.
-   **Structured Reports**: Generates detailed JSON-based reports including issue severity, screen coordinates, and remediation steps.
-   **CI/CD Integration**: Easy to integrate into automated UI tests (Espresso, Compose Testing Library).
-   **Pre-defined Rules**: Includes rules for:
    -   Touch Target Size (Minimum 48x48dp)
    -   Missing Content Descriptions
    -   Duplicate Labels
    -   Contrast Ratios (Coming soon)

## 📁 Project Structure

The SDK is divided into three main modules:

*   **`sdk:core`**: Generic, framework-agnostic engine and models for UI representation and rule processing.
*   **`sdk:android`**: Android-specific implementations, including mappers for Views and Jetpack Compose.
*   **`sdk:rules`**: A collection of concrete accessibility rules (e.g., `TouchTargetRule`, `ContentDescriptionRule`).

## 🛠️ Get Started

For detailed integration instructions and usage examples, please refer to the **[USAGE.md](./USAGE.md)** file.

### Quick dependency setup

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":sdk:android"))
    implementation(project(":sdk:rules"))
}
```

## 📋 Example Usage

```kotlin
val scanner = AndroidAccessibilityScanner(context)
scanner.addRule(TouchTargetRule())
scanner.addRule(ContentDescriptionRule())

val result = scanner.scan("HomeActivity")
result.issues.forEach { issue ->
    println("${issue.severity}: ${issue.summary}")
}
```