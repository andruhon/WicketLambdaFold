package com.github.andruhon.wicketlambdafold2.services

import com.github.andruhon.wicketlambdafold2.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
