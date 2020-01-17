package com.github.leondevlifelog.lintrule

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class AndroidLintIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(LogDetector.ISSUE_LOG, BaseActivityDetector.ISSUE_BASE_ACTIVITY)
    override val api: Int = CURRENT_API
}