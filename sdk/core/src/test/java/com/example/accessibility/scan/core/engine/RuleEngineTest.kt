package com.example.accessibility.scan.core.engine

import com.example.accessibility.scan.core.model.Rect
import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.result.IssueType
import com.example.accessibility.scan.core.result.Severity
import com.example.accessibility.scan.core.rule.AccessibilityRule
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuleEngineTest {

    @Test
    fun `analyze should aggregate issues from all rules`() = runBlocking {
        val root = UINode(
            id = "root",
            boundsInScreen = Rect(0, 0, 100, 100),
            className = "android.widget.FrameLayout"
        )
        
        val rule1 = object : AccessibilityRule {
            override val id = "rule1"
            override val description = "test rule 1"
            override fun check(node: UINode, root: UINode): List<Issue> {
                return listOf(Issue("rule1", "summary", "desc", Severity.ERROR, IssueType.CUSTOM, node.id, node.boundsInScreen, node.className))
            }
        }
        
        val rule2 = object : AccessibilityRule {
            override val id = "rule2"
            override val description = "test rule 2"
            override fun check(node: UINode, root: UINode): List<Issue> {
                return emptyList()
            }
        }
        
        val engine = RuleEngine(listOf(rule1, rule2))
        val issues = engine.analyze(root)
        
        assertEquals(1, issues.size)
        assertEquals("rule1", issues[0].ruleId)
    }
}
