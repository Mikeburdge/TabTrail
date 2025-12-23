package com.github.mikeburdge.tabtrail.startup

import com.github.mikeburdge.tabtrail.services.TTFileChangeListener
import com.github.mikeburdge.tabtrail.services.TTHistoryStore
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class TTStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        val TTHistoryStore = project.service<TTHistoryStore>()
        val TTFileChangeListener = TTFileChangeListener(project, TTHistoryStore)

        project.messageBus.connect(project).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER, TTFileChangeListener)
    }
}