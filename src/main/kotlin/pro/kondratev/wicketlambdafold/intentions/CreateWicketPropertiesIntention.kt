package pro.kondratev.wicketlambdafold.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle

class CreateWicketPropertiesIntention : CreateWicketFileIntention() {

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return this.hasNoResourceFile(".properties", element)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        this.createPropertiesFile(project, element)
    }

    override fun getText(): String {
        return WicketLambdaFoldBundle.message("CreateWicketPropertiesIntention.name")
    }

    override fun getFamilyName(): String {
        return WicketLambdaFoldBundle.message("intention.category.wicket.html.CreateWicketPropertiesIntention")
    }
}
