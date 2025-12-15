package com.github.mikeburdge.tabtrail.data

data class TTHistoryEntry(
    val fileUrl: String,
    var lastAccessedMs: Long,
    var lastCaretOffset: Int?
)