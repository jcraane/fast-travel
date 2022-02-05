package com.github.jcraane.fasttravel.actions

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class QuickJumpHandler(identifierToReferenceMap: Map<Char, PsiElement>?, previous: TypedActionHandler) : TypedActionHandler {
    private val previous: TypedActionHandler
    private val identifierToReferenceMap: Map<Char, PsiElement>

    init {
        this.identifierToReferenceMap = identifierToReferenceMap ?: mutableMapOf()
        this.previous = previous
    }

    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        val psiElement = identifierToReferenceMap[charTyped]
        if (psiElement != null) {
            val element = psiElement
            var navElement: PsiElement? = element
            navElement = TargetElementUtil.getInstance().getGotoDeclarationTarget(element, navElement)
            if (navElement != null) {
                navigateInCurrentEditor(element, element.containingFile, editor)
            }
        }
        resetPreviousHandler()
    }

    private fun resetPreviousHandler() {
        val actionManager = EditorActionManager.getInstance()
        val typedAction = actionManager.typedAction
        typedAction.setupHandler(previous)
    }

    companion object {
        private fun navigateInCurrentEditor(element: PsiElement, currentFile: PsiFile, currentEditor: Editor): Boolean {
            val offset = element.textOffset
            val leaf = currentFile.findElementAt(offset)
            // check that element is really physically inside the file
            // there are fake elements with custom navigation (e.g. opening URL in browser) that override getContainingFile for various reasons
            if (leaf != null && PsiTreeUtil.isAncestor(element, leaf, false)) {
                val project = element.project
                CommandProcessor.getInstance().executeCommand(project, {
                    IdeDocumentHistory.getInstance(project).includeCurrentCommandAsNavigation()
                    OpenFileDescriptor(project, currentFile.viewProvider.virtualFile, offset).navigateIn(currentEditor)
                    if (currentEditor.project != null) {
                        CommandProcessor.getInstance().executeCommand(currentEditor.project, {
                            FileEditorManagerEx.getInstanceEx(
                                currentEditor.project!!
                            ).updateFilePresentation(element.containingFile.virtualFile)
                        }, "", null)
                    }
                }, "", null)
                return true
            }
            return false
        }
    }
}
