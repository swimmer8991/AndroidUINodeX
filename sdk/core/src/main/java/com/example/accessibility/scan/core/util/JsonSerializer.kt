package com.example.accessibility.scan.core.util

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.result.ScanResult

object JsonSerializer {

    fun toJson(result: ScanResult): String {
        val sb = StringBuilder()
        sb.append("{")
        appendProperty(sb, "timestamp", result.timestamp)
        sb.append(",")
        appendProperty(sb, "activityName", result.activityName)
        sb.append(",")
        appendProperty(sb, "durationMs", result.durationMs)
        sb.append(",")
        appendProperty(sb, "issueCount", result.issueCount)
        sb.append(",")
        
        sb.append("\"issues\":[")
        result.issues.forEachIndexed { index, issue ->
            if (index > 0) sb.append(",")
            sb.append(issueToJson(issue))
        }
        sb.append("],")
        
        sb.append("\"rootNode\":")
        sb.append(nodeToJson(result.rootNode))
        
        sb.append("}")
        return sb.toString()
    }

    private fun issueToJson(issue: Issue): String {
        val sb = StringBuilder()
        sb.append("{")
        appendProperty(sb, "ruleId", issue.ruleId)
        sb.append(",")
        appendProperty(sb, "summary", issue.summary)
        sb.append(",")
        appendProperty(sb, "severity", issue.severity.name)
        sb.append(",")
        appendProperty(sb, "elementId", issue.elementId)
        sb.append(",")
        appendProperty(sb, "description", issue.description)
        sb.append("}")
        return sb.toString()
    }

    private fun nodeToJson(node: UINode): String {
        val sb = StringBuilder()
        sb.append("{")
        appendProperty(sb, "id", node.id)
        sb.append(",")
        appendProperty(sb, "class", node.className)
        sb.append(",")
        // Bounds
        sb.append("\"bounds\":{\"left\":${node.boundsInScreen.left},\"top\":${node.boundsInScreen.top},\"right\":${node.boundsInScreen.right},\"bottom\":${node.boundsInScreen.bottom}}")
        
        if (node.text != null) {
            sb.append(",")
            appendProperty(sb, "text", node.text)
        }
        if (node.contentDescription != null) {
            sb.append(",")
            appendProperty(sb, "contentDescription", node.contentDescription)
        }
        
        if (node.children.isNotEmpty()) {
            sb.append(",")
            sb.append("\"children\":[")
            node.children.forEachIndexed { index, child ->
                if (index > 0) sb.append(",")
                sb.append(nodeToJson(child))
            }
            sb.append("]")
        }
        
        sb.append("}")
        return sb.toString()
    }

    private fun appendProperty(sb: StringBuilder, key: String, value: Any?) {
        sb.append("\"$key\":")
        if (value is Number || value is Boolean) {
            sb.append(value)
        } else {
            sb.append("\"${escape(value.toString())}\"")
        }
    }

    private fun escape(s: String): String {
        return s.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
    }
}
