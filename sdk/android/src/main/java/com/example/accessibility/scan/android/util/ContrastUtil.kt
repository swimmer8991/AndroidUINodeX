package com.example.accessibility.scan.android.util

import android.graphics.Color
import kotlin.math.pow

object ContrastUtil {

    /**
     * Calculates the contrast ratio between two colors (packed ints).
     * Handles alpha blending of foreground over background.
     * 
     * @param foreground The foreground color.
     * @param background The background color (must be opaque or treated as over white/black).
     * @return The contrast ratio (1.0 to 21.0).
     */
    fun calculateContrast(foreground: Int, background: Int): Double {
        val bgIsOpaque = Color.alpha(background) == 255
        
        // If background is not opaque, we can't reliably know the result without knowing what's behind it.
        // For standard checks, we assume white background if the passed background is transparent? 
        // Or simply throw/return -1.
        // Let's assume we blend the background over WHITE for safety if not opaque.
        val effectiveBackground = if (bgIsOpaque) background else blend(background, Color.WHITE)
        
        val fgIsOpaque = Color.alpha(foreground) == 255
        val effectiveForeground = if (fgIsOpaque) foreground else blend(foreground, effectiveBackground)

        val l1 = calculateLuminance(effectiveForeground)
        val l2 = calculateLuminance(effectiveBackground)

        return if (l1 > l2) {
            (l1 + 0.05) / (l2 + 0.05)
        } else {
            (l2 + 0.05) / (l1 + 0.05)
        }
    }

    /**
     * Blends a foreground color over a background color.
     */
    private fun blend(fg: Int, bg: Int): Int {
        val alphaFg = Color.alpha(fg) / 255.0
        val alphaBg = Color.alpha(bg) / 255.0
        
        // Result alpha
        val alphaR = alphaFg + alphaBg * (1 - alphaFg)
        
        val rFg = Color.red(fg)
        val gFg = Color.green(fg)
        val bFg = Color.blue(fg)
        
        val rBg = Color.red(bg)
        val gBg = Color.green(bg)
        val bBg = Color.blue(bg)

        val rR = (rFg * alphaFg + rBg * alphaBg * (1 - alphaFg)) / alphaR
        val gR = (gFg * alphaFg + gBg * alphaBg * (1 - alphaFg)) / alphaR
        val bR = (bFg * alphaFg + bBg * alphaBg * (1 - alphaFg)) / alphaR

        return Color.rgb(rR.toInt(), gR.toInt(), bR.toInt())
    }

    /**
     * Calculates relative luminance of a color.
     * https://www.w3.org/TR/WCAG20/#relativeluminancedef
     */
    fun calculateLuminance(color: Int): Double {
        val r = linearize(Color.red(color) / 255.0)
        val g = linearize(Color.green(color) / 255.0)
        val b = linearize(Color.blue(color) / 255.0)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun linearize(component: Double): Double {
        return if (component <= 0.03928) {
            component / 12.92
        } else {
            ((component + 0.055) / 1.055).pow(2.4)
        }
    }
}
