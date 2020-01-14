package com.github.leondevlifelog.lintrule

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression


class LogDetector : Detector(), SourceCodeScanner {
    companion object {
        val ISSUE_LOG: Issue = Issue.create(
            "NoLog",
            "避免使用Log打印日志",
            "Log无法满足业务需求,建议使用XLog来打印日志",
            Category.CORRECTNESS,
            7,
            Severity.WARNING,
            Implementation(LogDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("v", "d", "i", "w", "e", "wtf")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            context.report(
                ISSUE_LOG,
                node,
                context.getLocation(node),
                "请使用'XLog'取代'Log'",
                quickFixIssueLog(node)
            )
        }
    }

    private fun quickFixIssueLog(logCall: UCallExpression): LintFix? {
        val arguments = logCall.valueArguments
        val methodName = logCall.methodName
        val tag = arguments[0]
        var fixedSourceCode = "XLog." + methodName + "(" + tag.asSourceString()
        val numArguments = arguments.size
        fixedSourceCode += when (numArguments) {
            2 -> {
                val msgOrThrowable = arguments[1]
                ", " + msgOrThrowable.asSourceString() + ")"
            }
            3 -> {
                val msg = arguments[1]
                val throwable = arguments[2]
                ", " + throwable.asSourceString() + ", " + msg.asSourceString() + ")"
            }
            else -> {
                throw IllegalStateException("android.util.Log overloads should have 2 or 3 arguments")
            }
        }
        return fix().group().apply {
            add(
                fix()
                    .replace()
                    .shortenNames()
                    .reformat(true)
                    .with(fixedSourceCode)
                    .build()
            )
        }.build()
    }
}