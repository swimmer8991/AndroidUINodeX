package com.example.accessibility.scan.core.result

import com.example.accessibility.scan.core.model.UINode

data class ScanResult(
    val timestamp: Long,
    val activityName: String,
    val rootNode: UINode,
    val issues: List<Issue>,
    val metadata: Map<String, String> = emptyMap(),
    val durationMs: Long
) {
    val issueCount: Int get() = issues.size
    val errorCount: Int get() = issues.count { it.severity == Severity.ERROR || it.severity == Severity.CRITICAL }
}
