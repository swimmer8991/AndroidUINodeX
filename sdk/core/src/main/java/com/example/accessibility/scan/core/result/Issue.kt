package com.example.accessibility.scan.core.result

import com.example.accessibility.scan.core.model.Rect

enum class Severity {
    CRITICAL,
    ERROR,
    WARNING,
    INFO
}

enum class IssueType {
    TOUCH_TARGET,
    CONTENT_LABEL,
    CONTRAST,
    TRAVERSAL_ORDER,
    CUSTOM
}

data class Issue(
    val ruleId: String,
    val summary: String,
    val description: String,
    val severity: Severity,
    val issueType: IssueType,
    val elementId: String,
    val elementBounds: Rect,
    val elementClassName: String,
    val remediation: String? = null,
    val url: String? = null // Link to WCAG or internal documentation
)
