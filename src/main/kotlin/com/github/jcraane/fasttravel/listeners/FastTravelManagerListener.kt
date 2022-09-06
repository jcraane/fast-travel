package com.github.jcraane.fasttravel.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.jcraane.fasttravel.services.FastTravelProjectService

internal class FastTravelManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<FastTravelProjectService>()
    }
}
