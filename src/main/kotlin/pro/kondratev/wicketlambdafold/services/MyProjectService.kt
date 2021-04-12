package pro.kondratev.wicketlambdafold.services

import com.intellij.openapi.project.Project
import pro.kondratev.wicketlambdafold.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
