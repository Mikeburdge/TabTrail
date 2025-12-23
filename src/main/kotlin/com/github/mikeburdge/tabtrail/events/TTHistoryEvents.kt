package com.github.mikeburdge.tabtrail.events

import com.intellij.util.messages.Topic

fun interface TTHistoryChangedListener {
    fun historyChanged()

}


@JvmField
val TT_HISTORY_CHANGE_TOPIC: Topic<TTHistoryChangedListener> =
    Topic.create("TT History Changed", TTHistoryChangedListener::class.java)