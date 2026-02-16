package com.example.accessibility.scan.rules

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.result.IssueType
import com.example.accessibility.scan.core.result.Severity
import com.example.accessibility.scan.core.rule.AccessibilityRule

class ContentDescriptionRule : AccessibilityRule {
    override val id: String = "MissingContentDescription"
    override val description: String = "Actionable items must have a label."

    override fun check(node: UINode, root: UINode): List<Issue> {
        // Only care about actionable items or image-like items that might need description
        // For simplicity, let's focus on Clickable/Focusable items.
        // If it's a TextView with text, it's fine.
        
        if (!node.isClickable && !node.isFocusable) return emptyList()
        
        // If it has text, likely fine (e.g. Button with text)
        if (!node.text.isNullOrEmpty()) return emptyList()
        
        // If it has content description, fine
        if (!node.contentDescription.isNullOrEmpty()) return emptyList()
        
        // If it has a state description, might be okay? (Checkbox)
        if (!node.stateDescription.isNullOrEmpty()) return emptyList()

        // Check children? Sometimes a button contains a TextView.
        // We need to check if any child contributes text.
        val hasTextDescendant = node.findAll { !it.text.isNullOrEmpty() || !it.contentDescription.isNullOrEmpty() }.isNotEmpty()
        if (hasTextDescendant) return emptyList()

        return listOf(
            Issue(
                ruleId = id,
                summary = "Missing accessible label",
                description = "This interactive element has no text, content description, or child with text.",
                severity = Severity.ERROR,
                issueType = IssueType.CONTENT_LABEL,
                elementId = node.id,
                elementBounds = node.boundsInScreen,
                elementClassName = node.className,
                remediation = "Add android:contentDescription or ensure a child TextView is present."
            )
        )
    }
}
