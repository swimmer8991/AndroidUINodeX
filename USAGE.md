# Accessibility Scanning SDK: User Guide

## What is this SDK for?
This SDK is designed for the automated accessibility audit of Android applications. It allows you to:
- **Find UI issues**: Identify small buttons (Touch Target), missing content descriptions, duplicate labels, and contrast issues.
- **Support for Compose and Views**: Works with both traditional Views and modern Jetpack Compose.
- **Automation in tests**: Can be integrated into UI tests (Espresso/Compose) to automatically scan every screen.
- **Report generation**: Provides a structured JSON with details of each issue (type, severity, screen coordinates, recommendations for fixing).

---

## How to use

### 1. Integration into your project
Add the dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":sdk:android"))
    implementation(project(":sdk:rules"))
}
```

### 2. Initialization and Running a Scan
To work with the SDK in an Activity, create an instance of `AndroidAccessibilityScanner`, add the required rules, and call `scan()`.

```kotlin
import com.example.accessibility.scan.android.impl.AndroidAccessibilityScanner
import com.example.accessibility.scan.rules.TouchTargetRule
import com.example.accessibility.scan.rules.ContentDescriptionRule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// 1. Create a scanner for the current Activity
val scanner = AndroidAccessibilityScanner(this)

// 2. Register rules
scanner.addRule(TouchTargetRule())
scanner.addRule(ContentDescriptionRule())

// 3. Start scanning (in a coroutine)
MainScope().launch {
    val result = scanner.scan("MainActivity")
    
    // Process results
    println("Issues found: ${result.issueCount}")
    result.issues.forEach { issue ->
        println("[${issue.severity}] ${issue.summary}: ${issue.description}")
        println("How to fix: ${issue.remediation}")
    }
}
```

### 3. Integration into UI Tests
You can check the screen's accessibility directly during test runs:

```kotlin
@Test
fun testScreenAccessibility() = runTest {
    activityScenario.onActivity { activity ->
        val scanner = AndroidAccessibilityScanner(activity)
        scanner.addRule(TouchTargetRule())
        
        val result = scanner.scan("ProfileScreen")
        
        // The test will fail if critical issues are found
        if (result.errorCount > 0) {
            throw AssertionError("Accessibility issues found: ${result.issues}")
        }
    }
}
```

---

## How to test the SDK?

You **do not necessarily** need to create an `.aar` file to test it within the same project. You can use direct dependencies on the modules.

### Option 1: Testing in the same project (Recommended)
If you want to quickly verify the scanner's performance, create a new app module (`app`) in the same project and add a dependency via `project`:

```kotlin
// Your app's build.gradle.kts
dependencies {
    implementation(project(":sdk:android"))
    implementation(project(":sdk:rules"))
}
```
This allows you to make changes to the SDK and see them immediately in the app without rebuilding libraries.

### Option 2: Using AAR (For external projects)
If you need to move the SDK to another project as a binary file:
1. Open a terminal in the project root.
2. Build the project: `./gradlew assembleRelease`
3. Find the files at:
   - `sdk/android/build/outputs/aar/android-release.aar`
   - `sdk/rules/build/outputs/aar/rules-release.aar`
4. Copy them to the `libs` folder of your target application and include them:
   ```kotlin
   implementation(files("libs/android-release.aar"))
   implementation(files("libs/rules-release.aar"))
   ```

---

## Key Components
- **UINode**: A unified model for representing UI elements (regardless of whether it's View or Compose).
- **Scanner**: The main interface for starting the analysis.
- **Issue**: An object containing information about the error, its type, and the fix recommendation.
- **RuleEngine**: The engine that processes the element tree through a set of rules.

