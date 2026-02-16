# Accessibility Scanning SDK: Руководство пользователя

## Для чего нужен этот SDK?
Данный SDK предназначен для автоматизированного аудита доступности (accessibility) Android-приложений. Он позволяет:
- **Находить проблемы UI**: Выявлять мелкие кнопки (Touch Target), отсутствие описаний (Content Description), дубликаты меток и проблемы с контрастом.
- **Поддержка Compose и Views**: Работает как с классическими View, так и с современным Jetpack Compose.
- **Автоматизация в тестах**: Можно интегрировать в UI-тесты (Espresso/Compose), чтобы автоматически проверять каждый экран.
- **Генерация отчетов**: Выдает структурированный JSON с деталями каждой проблемы (тип, строгость, координаты на экране, рекомендации по исправлению).

---

## Как использовать

### 1. Подключение в проект
Добавьте зависимости в ваш `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":sdk:android"))
    implementation(project(":sdk:rules"))
}
```

### 2. Инициализация и запуск сканирования
Для работы с SDK в Activity создайте экземпляр `AndroidAccessibilityScanner`, добавьте нужные правила и вызовите `scan()`.

```kotlin
import com.example.accessibility.scan.android.impl.AndroidAccessibilityScanner
import com.example.accessibility.scan.rules.TouchTargetRule
import com.example.accessibility.scan.rules.ContentDescriptionRule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// 1. Создание сканера для текущей Activity
val scanner = AndroidAccessibilityScanner(this)

// 2. Регистрация правил
scanner.addRule(TouchTargetRule())
scanner.addRule(ContentDescriptionRule())

// 3. Запуск сканирования (в корутине)
MainScope().launch {
    val result = scanner.scan("MainActivity")
    
    // Обработка результатов
    println("Найдено проблем: ${result.issueCount}")
    result.issues.forEach { issue ->
        println("[${issue.severity}] ${issue.summary}: ${issue.description}")
        println("Как исправить: ${issue.remediation}")
    }
}
```

### 3. Интеграция в UI Тесты
Вы можете проверять доступность экрана прямо во время прогона тестов:

```kotlin
@Test
fun testScreenAccessibility() = runTest {
    activityScenario.onActivity { activity ->
        val scanner = AndroidAccessibilityScanner(activity)
        scanner.addRule(TouchTargetRule())
        
        val result = scanner.scan("ProfileScreen")
        
        // Тест упадет, если найдены критические ошибки
        if (result.errorCount > 0) {
            throw AssertionError("Accessibility issues found: ${result.issues}")
        }
    }
}
```

---

## Как протестировать SDK?

Вам **необязательно** создавать `.aar` файл для тестирования внутри этого же проекта. Вы можете использовать прямую зависимость от модулей.

### Вариант 1: Тестирование в том же проекте (Рекомендуется)
Если вы хотите быстро проверить работу сканера, создайте новый модуль приложения (`app`) в этом же проекте и добавьте зависимость через `project`:

```kotlin
// build.gradle.kts вашего приложения
dependencies {
    implementation(project(":sdk:android"))
    implementation(project(":sdk:rules"))
}
```
Это позволит вам вносить изменения в SDK и сразу видеть их в приложении без пересборки библиотек.

### Вариант 2: Использование AAR (Для внешних проектов)
Если вам все же нужно перенести SDK в другой проект в виде бинарного файла:
1. Откройте терминал в корне проекта.
2. Выполните сборку: `./gradlew assembleRelease`
3. Найдите файлы по пути:
   - `sdk/android/build/outputs/aar/android-release.aar`
   - `sdk/rules/build/outputs/aar/rules-release.aar`
4. Скопируйте их в папку `libs` вашего целевого приложения и подключите:
   ```kotlin
   implementation(files("libs/android-release.aar"))
   implementation(files("libs/rules-release.aar"))
   ```

---

## Ключевые компоненты
- **UINode**: Единая модель представления элемента интерфейса (независимо от того, View это или Compose).
- **Scanner**: Основной интерфейс для запуска анализа.
- **Issue**: Объект, содержащий информацию об ошибке, её типе и способе исправления.
- **RuleEngine**: Движок, который прогоняет дерево элементов через набор правил.
