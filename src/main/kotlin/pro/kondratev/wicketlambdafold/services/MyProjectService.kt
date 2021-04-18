package pro.kondratev.wicketlambdafold.services

import com.intellij.openapi.project.Project
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle

class MyProjectService(project: Project) {

    init {
        println(WicketLambdaFoldBundle.message("projectService", project.name))
    }
}
