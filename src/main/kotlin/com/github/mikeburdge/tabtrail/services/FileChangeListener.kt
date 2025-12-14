package com.github.mikeburdge.tabtrail.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener


class FileChangeListener (private val historyStore: HistoryStore) : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val currentFile = event.newFile ?: return

        val url: String = currentFile.url

        historyStore.recordAccess(url, null)



    }

}