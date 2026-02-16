package com.example.accessibility.scan.core.model

/**
 * Unified representation of a UI element (View or SemanticsNode).
 * Immutable to ensure thread safety during analysis.
 */
data class UINode(
    val id: String, // Unique identifier (e.g., resource-entry-name or hash)
    val boundsInScreen: Rect,
    val className: String, // e.g., "android.widget.Button" or "Role.Button"
    
    // Accessibility Properties
    val text: String? = null,
    val contentDescription: String? = null,
    val hintText: String? = null,
    val stateDescription: String? = null,
    
    // Interactive State
    val isClickable: Boolean = false,
    val isLongClickable: Boolean = false,
    val isFocusable: Boolean = false,
    val isFocused: Boolean = false,
    val isEnabled: Boolean = true,
    val isHeading: Boolean = false,
    val isSelected: Boolean = false,
    val isCheckable: Boolean = false,
    val isChecked: Boolean = false,
    
    // Hierarchy
    val children: List<UINode> = emptyList(),
    // Parent reference is excluded from data class equals/hashcode to avoid recursion loops, 
    // and is typically handled by the traversal or passed separately if needed. 
    // For simplicity in this immutable model, we'll traverse top-down.
    
    // Metadata
    val extras: Map<String, Any> = emptyMap()
) {
    /**
     * Helper to find all descendants matching a predicate.
     */
    fun findAll(predicate: (UINode) -> Boolean): List<UINode> {
        val result = mutableListOf<UINode>()
        if (predicate(this)) result.add(this)
        children.forEach { child ->
            result.addAll(child.findAll(predicate))
        }
        return result
    }
}
