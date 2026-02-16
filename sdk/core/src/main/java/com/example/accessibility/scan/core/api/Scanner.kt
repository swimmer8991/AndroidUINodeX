package com.example.accessibility.scan.core.api

import com.example.accessibility.scan.core.result.ScanResult
import com.example.accessibility.scan.core.rule.AccessibilityRule

interface Scanner {
    /**
     * Registers a custom rule to the scanner.
     */
    fun addRule(rule: AccessibilityRule)
    
    /**
     * Captures the current UI state and runs analysis.
     * Must be safe to call from any thread, but implementation should handle 
     * UI thread checks for view traversal.
     * 
     * @param activityName Optional name to tag the result with.
     */
    suspend fun scan(activityName: String = "Unknown"): ScanResult
}
