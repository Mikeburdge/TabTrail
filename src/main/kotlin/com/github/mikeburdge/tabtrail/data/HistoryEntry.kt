package com.github.mikeburdge.tabtrail.data

data class HistoryEntry(
    val fileUrl: String,
    var lastAccessedMs: Long,
    var lastCaretOffset: Int?
)