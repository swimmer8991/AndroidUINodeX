package com.example.accessibility.scan.rules

import com.example.accessibility.scan.core.model.Rect
import com.example.accessibility.scan.core.model.UINode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TouchTargetRuleTest {

    private val density = 2.0f // 1 dp = 2 px. 48dp = 96px
    private val rule = TouchTargetRule(density)
    private val root = UINode("root", Rect(0,0,1000,1000), "Root")

    @Test
    fun `check should return issue when clickable item is too small`() {
        val smallNode = UINode(
            id = "btn",
            boundsInScreen = Rect(0, 0, 50, 50), // 25dp x 25dp
            className = "android.widget.Button",
            isClickable = true
        )
        
        val issues = rule.check(smallNode, root)
        assertEquals(1, issues.size)
    }

    @Test
    fun `check should pass when item is large enough`() {
        val largeNode = UINode(
            id = "btn",
            boundsInScreen = Rect(0, 0, 100, 100), // 50dp x 50dp
            className = "android.widget.Button",
            isClickable = true
        )
        
        val issues = rule.check(largeNode, root)
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `check should ignore non-clickable items`() {
        val smallText = UINode(
            id = "txt",
            boundsInScreen = Rect(0, 0, 20, 20),
            className = "android.widget.TextView",
            isClickable = false
        )
        
        val issues = rule.check(smallText, root)
        assertTrue(issues.isEmpty())
    }
}
