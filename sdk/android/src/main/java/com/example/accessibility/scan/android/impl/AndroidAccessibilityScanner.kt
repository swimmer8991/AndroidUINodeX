package com.example.accessibility.scan.android.impl

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.accessibility.scan.android.mapper.ComposeToNodeMapper
import com.example.accessibility.scan.android.mapper.ViewToNodeMapper
import com.example.accessibility.scan.core.api.Scanner
import com.example.accessibility.scan.core.engine.RuleEngine
import com.example.accessibility.scan.core.model.UINode
import com.example.accessibility.scan.core.result.ScanResult
import com.example.accessibility.scan.core.rule.AccessibilityRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

class AndroidAccessibilityScanner(private val activity: Activity) : Scanner {
    
    private val rules = CopyOnWriteArrayList<AccessibilityRule>()
    private val ruleEngine by lazy { RuleEngine(rules) }
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun addRule(rule: AccessibilityRule) {
        rules.add(rule)
    }

    override suspend fun scan(activityName: String): ScanResult {
        val startTime = System.currentTimeMillis()
        
        // 1. Capture UI on Main Thread
        val rootNode = withContext(Dispatchers.Main) {
            val rootView = activity.window.decorView.rootView
            capture(rootView)
        }
        
        // 2. Run Analysis on Background Thread
        val issues = ruleEngine.analyze(rootNode)
        
        val endTime = System.currentTimeMillis()
        
        return ScanResult(
            timestamp = endTime,
            activityName = activityName,
            rootNode = rootNode,
            issues = issues,
            durationMs = endTime - startTime
        )
    }
    
    // Recursive capture that delegates to specific mappers
    private fun capture(view: View): UINode {
        if (ComposeToNodeMapper.isComposeView(view)) {
            val composeNode = ComposeToNodeMapper.map(view)
            if (composeNode != null) return composeNode
        }
        
        // Fallback or Standard View
        val node = ViewToNodeMapper.map(view)
        
        // We need to re-map children if we are doing manual recursion here 
        // to handle the mix of View and Compose.
        // ViewToNodeMapper.map already maps children recursively using itself.
        // We should start modifying ViewToNodeMapper to call this generic 'capture' or 
        // handle the 'Compose' switch inside ViewToNodeMapper. 
        // For strict separation, let's inject logic or overwrite ViewToNodeMapper to accept a 'childMapper' strategy?
        // Simpler: Just handle it in ViewToNodeMapper.
        // Since I've already written ViewToNodeMapper, I should probably update it to check for Compose.
        
        return node
    }
}
