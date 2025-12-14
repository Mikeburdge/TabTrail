package com.github.mikeburdge.tabtrail.services

import com.github.mikeburdge.tabtrail.data.HistoryEntry
import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class HistoryStore {
    private val entries = mutableListOf<HistoryEntry>()

    private val maxEntries: Int = 10

    public fun recordAccess(fileUrl: String, caretOffset: Int?) {
        val now = System.currentTimeMillis()
        val index = entries.indexOfFirst { it.fileUrl == fileUrl }

        if (index >= 0) {
            val currentEntry: HistoryEntry = entries[index]

            entries.removeAt(index)

            currentEntry.lastAccessedMs = now
            currentEntry.lastCaretOffset = caretOffset

            entries.addFirst(currentEntry)
        }
        else {
            entries.addFirst(HistoryEntry(fileUrl, now, caretOffset))
        }

        while (entries.size > maxEntries) {
           entries.removeLast();
        }
    }

    fun getEntries () = entries.toList()
}