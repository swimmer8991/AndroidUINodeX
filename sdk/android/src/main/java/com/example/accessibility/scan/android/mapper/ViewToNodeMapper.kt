package com.example.accessibility.scan.android.mapper

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.WindowManager
import androidx.core.view.children
import com.example.accessibility.scan.core.model.UINode
import java.util.UUID

object ViewToNodeMapper {

    fun map(view: View): UINode {
        val bounds = Rect()
        // getGlobalVisibleRect returns true if at least part of the view is visible within the root
        val isVisibleOnScreen = view.getGlobalVisibleRect(bounds)
        
        // Convert Android Rect to our platform-agnostic Rect
        val coreBounds = com.example.accessibility.scan.core.model.Rect(
            bounds.left, bounds.top, bounds.right, bounds.bottom
        )

        // Check for Compose View first
        if (ComposeToNodeMapper.isComposeView(view)) {
            val composeNode = ComposeToNodeMapper.map(view)
            if (composeNode != null) return composeNode
        }

        val childrenNodes = if (view is ViewGroup) {
            view.children.mapNotNull { child ->
                if (child.visibility == View.GONE) null else map(child)
            }.toList()
        } else {
            emptyList()
        }

        // Extract ID - preferably resource entry name, fallback to hashcode
        val id = try {
            if (view.id != View.NO_ID) {
                view.resources.getResourceEntryName(view.id)
            } else {
                "${view.javaClass.simpleName}_${view.hashCode()}"
            }
        } catch (e: Exception) {
            "${view.javaClass.simpleName}_${view.hashCode()}"
        }

        return UINode(
            id = id,
            boundsInScreen = coreBounds,
            className = view.accessibilityClassName?.toString() ?: view.javaClass.name,
            text = getViewText(view),
            contentDescription = view.contentDescription?.toString(),
            hintText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) view.tooltipText?.toString() else null, // rudimentary hint mapping
            isClickable = view.isClickable,
            isLongClickable = view.isLongClickable,
            isFocusable = view.isFocusable,
            isFocused = view.isFocused,
            isEnabled = view.isEnabled,
            isSelected = view.isSelected,
            children = childrenNodes,
            extras = mapOf(
                "alpha" to view.alpha,
                "visibility" to view.visibility,
                "viewId" to view.id
            )
        )
    }

    private fun getViewText(view: View): String? {
        // Basic reflection-less text extraction
        // In a real SDK, we'd use AccessibilityNodeInfo logic or specific view type checks (TextView)
        // Since we want to avoid linking against specific widgets in the core mapper if possible, 
        // strictly speaking checking "instanceof TextView" requires android.widget dependency.
        // For this phase, we'll assume we can cast to TextView safely or use reflection if we were minimizing deps,
        // but since this is the "android" module, we can depend on android.widget.
        
        if (view is android.widget.TextView) {
            return view.text?.toString()
        }
        return null
    }
}
