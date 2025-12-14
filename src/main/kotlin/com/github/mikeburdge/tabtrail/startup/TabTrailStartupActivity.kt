package com.github.mikeburdge.tabtrail.startup

import com.github.mikeburdge.tabtrail.services.FileChangeListener
import com.github.mikeburdge.tabtrail.services.HistoryStore
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class TabTrailStartupActivity : StartupActivity {

    private val log = Logger.getInstance(TabTrailStartupActivity::class.java)
    override fun runActivity(project: Project) {
        val historyStore = project.service<HistoryStore>()
        val fileChangeListener = FileChangeListener(historyStore)

        project.messageBus.connect(project).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER, fileChangeListener)
    }
}