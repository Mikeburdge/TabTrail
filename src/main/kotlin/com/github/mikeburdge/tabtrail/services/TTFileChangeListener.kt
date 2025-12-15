package com.github.mikeburdge.tabtrail.services

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener


class TTFileChangeListener (private val TTHistoryStore: TTHistoryStore) : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val currentFile = event.newFile ?: return

        val url: String = currentFile.url

        TTHistoryStore.recordAccess(url, null)



    }

}