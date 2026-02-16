package com.example.accessibility.scan.rules

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.result.IssueType
import com.example.accessibility.scan.core.result.Severity
import com.example.accessibility.scan.core.rule.AccessibilityRule

class DuplicateLabelRule : AccessibilityRule {
    override val id: String = "DuplicateLabel"
    override val description: String = "Avoid sharing the exact same label with children, causing double readout."

    override fun check(node: UINode, root: UINode): List<Issue> {
        val label = node.contentDescription ?: node.text ?: return emptyList()
        
        val issues = mutableListOf<Issue>()
        
        // Check immediate children for exact same label
        node.children.forEach { child ->
            val childLabel = child.contentDescription ?: child.text
            if (childLabel == label) {
                issues.add(
                    Issue(
                        ruleId = id,
                        summary = "Redundant duplicate label",
                        description = "This node shares the exact same label '$label' with its child.",
                        severity = Severity.WARNING,
                        issueType = IssueType.CONTENT_LABEL,
                        elementId = node.id,
                        elementBounds = node.boundsInScreen,
                        elementClassName = node.className,
                        remediation = "Remove the contentDescription from the container if the child already reads it."
                    )
                )
            }
        }
        
        return issues
    }
}
