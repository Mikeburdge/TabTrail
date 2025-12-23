package com.github.mikeburdge.tabtrail.services

import com.github.mikeburdge.tabtrail.events.TT_HISTORY_CHANGE_TOPIC
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileEditor.FileEditorManagerListener


class TTFileChangeListener(private val project: Project, private val TTHistoryStore: TTHistoryStore) : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val currentFile = event.newFile ?: return

        val url: String = currentFile.url

        TTHistoryStore.recordAccess(url)

        project.messageBus.syncPublisher(TT_HISTORY_CHANGE_TOPIC).historyChanged()

    }

}