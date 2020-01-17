package com.github.leondevlifelog.lintrule

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UClass

class BaseActivityDetector : Detector(), SourceCodeScanner {
    private val baseActivityName = "com.example.linttest.BaseActivity"
    private val appCompatActivityName = "androidx.appcompat.app.AppCompatActivity"

    companion object {
        val ISSUE_BASE_ACTIVITY: Issue = Issue.create(
            "NotBaseActivity",
            "务必继承BaseActivity类",
            "BaseActivity含有业务封装,如果继承自其他可能导致业务错误",
            Category.CORRECTNESS,
            9,
            Severity.WARNING,
            Implementation(BaseActivityDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun applicableSuperClasses(): List<String>? {
        return listOf(appCompatActivityName)
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        super.visitClass(context, declaration)
        if (declaration.name == baseActivityName) {
            return
        }
        if (!context.evaluator.extendsClass(
                declaration,
                baseActivityName,
                false
            )
        ) {
            val lintFix = fix()
                .replace()
                .text(declaration.superClass?.name)
                .with("BaseActivity")
                .reformat(true)
                .build()
            context.report(
                ISSUE_BASE_ACTIVITY,
                context.getLocation(declaration as PsiElement),
                "务必继承BaseActivity类",
                lintFix
            )
        }
    }
}