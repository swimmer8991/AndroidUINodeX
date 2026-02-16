package com.example.accessibility.scan.rules

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.result.IssueType
import com.example.accessibility.scan.core.result.Severity
import com.example.accessibility.scan.core.rule.AccessibilityRule
import kotlin.math.abs

class TouchTargetRule(private val displayMetricsDensity: Float) : AccessibilityRule {
    
    override val id: String = "TouchTargetSize"
    override val description: String = "Clickable items must be at least 48dp x 48dp."

    override fun check(node: UINode, root: UINode): List<Issue> {
        if (!node.isClickable && !node.isLongClickable) return emptyList()

        val minSizePx = (48 * displayMetricsDensity).toInt()
        
        val width = node.boundsInScreen.width
        val height = node.boundsInScreen.height

        // Simple check: strict 48dp enforcement. 
        // Real world might check for touch delegates or parents, but this is V1.
        if (width < minSizePx || height < minSizePx) {
            // Prepare for potential false positives if the parent handles the click with a larger area
            // But if *this* node is marked clickable, it should be large enough.
            
            return listOf(
                Issue(
                    ruleId = id,
                    summary = "Touch target too small",
                    description = "Element is ${pxToDp(width)}dp x ${pxToDp(height)}dp. Minimum required is 48dp x 48dp.",
                    severity = Severity.ERROR,
                    issueType = IssueType.TOUCH_TARGET,
                    elementId = node.id,
                    elementBounds = node.boundsInScreen,
                    elementClassName = node.className,
                    remediation = "Ensure the view has a minimum size of 48dp or use a TouchDelegate."
                )
            )
        }
        return emptyList()
    }
    
    private fun pxToDp(px: Int): Int {
        return (px / displayMetricsDensity).toInt()
    }
}
