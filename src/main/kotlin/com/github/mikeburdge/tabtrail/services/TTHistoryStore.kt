package com.github.mikeburdge.tabtrail.services

import com.github.mikeburdge.tabtrail.data.TTHistoryEntry
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros


@Service(Service.Level.PROJECT)
@State(
    name = "TabTrailHistoryStore",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class TTHistoryStore : PersistentStateComponent<TTHistoryStore.State> {
    private val entries = mutableListOf<TTHistoryEntry>()

    private val maxEntries: Int = 10

    class State {
        var entries: MutableList<EntryState> = mutableListOf()
    }

    class EntryState() {
        var fileUrl: String = ""
        var lastAccessedMs: Long = 0L
    }

    fun recordAccess(fileUrl: String) {
        val now = System.currentTimeMillis()
        val index = entries.indexOfFirst { it.fileUrl == fileUrl }

        if (index >= 0) {
            val currentEntry: TTHistoryEntry = entries[index]

            entries.removeAt(index)

            currentEntry.lastAccessedMs = now

            entries.addFirst(currentEntry)
        } else {
            entries.addFirst(TTHistoryEntry(fileUrl, now))
        }

        while (entries.size > maxEntries) {
            entries.removeLast()
        }
    }

    fun getEntries() = entries.toList()
    override fun getState(): TTHistoryStore.State {
        val state = State()
        for (e in entries) {
            val pe = EntryState()
            pe.fileUrl = e.fileUrl
            pe.lastAccessedMs = e.lastAccessedMs
            state.entries.add(pe)
        }
        return state
    }

    override fun loadState(p0: TTHistoryStore.State) {
        entries.clear()

        for (pe in p0.entries) {
            if (pe.fileUrl.isBlank()) continue
            entries.add(
                TTHistoryEntry(pe.fileUrl, pe.lastAccessedMs)
            )
        }

        while (entries.size > maxEntries) {
            entries.removeLast()
        }
    }
}