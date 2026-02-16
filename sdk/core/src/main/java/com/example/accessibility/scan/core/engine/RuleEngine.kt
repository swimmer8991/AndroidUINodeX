package com.example.accessibility.scan.core.engine

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue
import com.example.accessibility.scan.core.rule.AccessibilityRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RuleEngine(
    private val rules: List<AccessibilityRule>
) {
    suspend fun analyze(root: UINode): List<Issue> = withContext(Dispatchers.Default) {
        val issues = mutableListOf<Issue>()
        
        // Flatten tree for iteration or recurse. 
        // Recursion is fine for rule checking.
        traverseAndCheck(root, root, issues)
        
        return@withContext issues
    }
    
    private fun traverseAndCheck(node: UINode, root: UINode, issues: MutableList<Issue>) {
        // Run all rules on current node
        for (rule in rules) {
            issues.addAll(rule.check(node, root))
        }
        
        // Recurse
        for (child in node.children) {
            traverseAndCheck(child, root, issues)
        }
    }
}
