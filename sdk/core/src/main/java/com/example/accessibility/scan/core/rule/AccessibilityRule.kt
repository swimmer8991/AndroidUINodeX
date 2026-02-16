package com.example.accessibility.scan.core.rule

import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.Issue

/**
 * Defines a rule that can be checked against the UI tree.
 */
interface AccessibilityRule {
    val id: String
    val description: String
    
    /**
     * Checks a node (and potentially its children) for violations.
     * 
     * @param node The node to check.
     * @param root The root of the tree, in case checking requires context of the whole tree.
     * @return List of issues found in this node. 
     */
    fun check(node: UINode, root: UINode): List<Issue>
}
