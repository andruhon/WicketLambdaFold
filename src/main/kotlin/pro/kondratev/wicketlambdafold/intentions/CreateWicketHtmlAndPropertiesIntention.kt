package pro.kondratev.wicketlambdafold.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle

class CreateWicketHtmlAndPropertiesIntention : CreateWicketFileIntention() {

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return(
            this.hasNoResourceFile(".html", element) &&
            this.hasNoResourceFile(".properties", element)
        )
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        this.createHtmlFile(project, element)
        this.createPropertiesFile(project, element)
    }

    override fun getText(): String {
        return WicketLambdaFoldBundle.message("CreateWicketHtmlAndPropertiesIntention.name")
    }

    override fun getFamilyName(): String {
        return WicketLambdaFoldBundle.message("intention.category.wicket.html.CreateWicketHtmlAndPropertiesIntention")
    }
}
