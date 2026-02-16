package com.example.accessibility.scan.android.mapper

import android.view.View
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import com.example.accessibility.scan.core.model.Rect
import com.example.accessibility.scan.core.model.UINode
import java.lang.reflect.Method

object ComposeToNodeMapper {

    private const val COMPOSE_VIEW_CLASS_NAME = "androidx.compose.ui.platform.AndroidComposeView"

    fun isComposeView(view: View): Boolean {
        return view.javaClass.name == COMPOSE_VIEW_CLASS_NAME
    }

    fun map(view: View): UINode? {
        if (!isComposeView(view)) return null

        try {
            // Reflection to get SemanticsOwner
            // val semanticsOwner = (view as AndroidComposeView).semanticsOwner
            val semanticsOwnerMethod = view.javaClass.getMethod("getSemanticsOwner")
            val semanticsOwner = semanticsOwnerMethod.invoke(view) ?: return null

            // val root = semanticsOwner.unmergedRootSemanticsNode
            val rootNodeMethod = semanticsOwner.javaClass.getMethod("getUnmergedRootSemanticsNode")
            val rootSemanticsNode = rootNodeMethod.invoke(semanticsOwner) as? SemanticsNode ?: return null

            return mapSemanticsNode(rootSemanticsNode, view)

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback or log error
            return null
        }
    }

    private fun mapSemanticsNode(node: SemanticsNode, invokeView: View): UINode {
        val config = node.config
        val bounds = node.boundsInRoot
        
        // Offset by view position to get screen coordinates
        // boundsInRoot is relative to the ComposeView
        val array = IntArray(2)
        invokeView.getLocationOnScreen(array)
        val offsetX = array[0]
        val offsetY = array[1]

        val screenBounds = Rect(
            left = (bounds.left + offsetX).toInt(),
            top = (bounds.top + offsetY).toInt(),
            right = (bounds.right + offsetX).toInt(),
            bottom = (bounds.bottom + offsetY).toInt()
        )

        // Extraction helpers
        val textList = config.getOrNull(SemanticsProperties.Text)
        val text = textList?.joinToString(", ") 
        
        val contentDescList = config.getOrNull(SemanticsProperties.ContentDescription)
        val contentDescription = contentDescList?.joinToString(", ")
        
        val stateDesc = config.getOrNull(SemanticsProperties.StateDescription)
        
        val isClickable = config.contains(SemanticsActions.OnClick)
        val isLongClickable = config.contains(SemanticsActions.OnLongClick)
        val isFocusable = config.contains(SemanticsProperties.Focused) // Property is usually defined if focusable
        val isFocused = config.getOrNull(SemanticsProperties.Focused) == true
        val isEnabled = !config.contains(SemanticsProperties.Disabled)
        val isHeading = config.contains(SemanticsProperties.Heading)
        val isSelected = config.getOrNull(SemanticsProperties.Selected) == true

        // Recurse children
        val children = node.children.map { child ->
            mapSemanticsNode(child, invokeView)
        }

        return UINode(
            id = "ComposeNode_${node.id}",
            boundsInScreen = screenBounds,
            className = "SemanticsNode", // We could infer role, but generic for now
            text = text,
            contentDescription = contentDescription,
            hintText = null, // Semantics properties for hints exist but vary
            stateDescription = stateDesc,
            isClickable = isClickable,
            isLongClickable = isLongClickable,
            isFocusable = isFocusable, // Note: Simplified check
            isFocused = isFocused,
            isEnabled = isEnabled,
            isHeading = isHeading,
            isSelected = isSelected,
            children = children,
            extras = mapOf(
                "isMergingEnabled" to node.mergingEnabled
            )

        )
    }
}
