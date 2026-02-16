package com.example.accessibility.scan.core.model

/**
 * Platform-agnostic rectangle representation.
 * origin is top-left.
 */
data class Rect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
    val centerX: Int get() = left + width / 2
    val centerY: Int get() = top + height / 2
    val isEmpty: Boolean get() = left >= right || top >= bottom

    fun contains(x: Int, y: Int): Boolean {
        return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom
    }
}
