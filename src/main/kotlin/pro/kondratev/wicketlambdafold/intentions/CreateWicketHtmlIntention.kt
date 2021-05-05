package pro.kondratev.wicketlambdafold.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle

class CreateWicketHtmlIntention : CreateWicketFileIntention() {

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return this.hasNoResourceFile(".html", element)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        this.createHtmlFile(project, element)
    }

    override fun getText(): String {
        return WicketLambdaFoldBundle.message("CreateWicketHtmlIntention.name")
    }

    override fun getFamilyName(): String {
        return WicketLambdaFoldBundle.message("intention.category.wicket.html.CreateWicketHtmlIntention")
    }
}
