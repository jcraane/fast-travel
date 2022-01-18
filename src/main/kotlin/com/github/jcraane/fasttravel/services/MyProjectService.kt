package com.github.jcraane.fasttravel.services

import com.intellij.openapi.project.Project
import com.github.jcraane.fasttravel.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
