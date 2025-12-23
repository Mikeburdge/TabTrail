package com.github.mikeburdge.tabtrail.toolWindow

import com.github.mikeburdge.tabtrail.data.TTHistoryEntry
import com.github.mikeburdge.tabtrail.events.TTHistoryChangedListener
import com.github.mikeburdge.tabtrail.events.TT_HISTORY_CHANGE_TOPIC
import com.github.mikeburdge.tabtrail.services.TTHistoryStore
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class TTHistoryWindow : ToolWindowFactory, DumbAware {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val recentFilesPanel = RecentFilesPanel(project)
        val content = ContentFactory.getInstance().createContent(recentFilesPanel.component, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class RecentFilesPanel(val project: Project) {

        private val store: TTHistoryStore = project.service()

        private val model = DefaultListModel<TTHistoryEntry>()

        private val list = JBList(model).apply {
            this.cellRenderer = object : ColoredListCellRenderer<TTHistoryEntry>() {
                override fun customizeCellRenderer(
                    list: JList<out TTHistoryEntry>,
                    value: TTHistoryEntry?,
                    index: Int,
                    selected: Boolean,
                    hasFocus: Boolean
                ) {
                    if (value == null) return
                    append(formatEntry(value.fileUrl), SimpleTextAttributes.REGULAR_ATTRIBUTES)
                }
            }
        }


        val component: JComponent = JPanel(BorderLayout()).apply {
            val topBar = JPanel(BorderLayout()).apply {
            }
            add(topBar, BorderLayout.NORTH)
            add(JBScrollPane(list), BorderLayout.CENTER)
        }


        init {
            project.messageBus.connect(project).subscribe(TT_HISTORY_CHANGE_TOPIC, TTHistoryChangedListener {
                ApplicationManager.getApplication().invokeLater { refresh() }
            })



            list.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ttOpenSelected")

            list.actionMap.put("ttOpenSelected", object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) {
                    openSelectedEntry()
                }
            })

            list.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount != 2) return
                    openSelectedEntry()
                }
            })

            refresh()
        }

        private fun openSelectedEntry() {
            val selected = list.selectedValue ?: return
            openEntry(selected)
        }

        private fun refresh() {
            // rebuilt the model every time
            model.clear()

            val entries = store.getEntries()

            for (entry in entries) {
                model.addElement(entry)
            }

        }

        private fun formatEntry(fileUrl: String): String {
            val lastSlash = fileUrl.lastIndexOf("/")
            return if (lastSlash >= 0 && lastSlash < fileUrl.length - 1) {
                fileUrl.substring(lastSlash + 1)
            } else (
                    fileUrl
                    )
        }


        private fun openEntry(entry: TTHistoryEntry) {
            val vFile = VirtualFileManager.getInstance().findFileByUrl(entry.fileUrl)
            if (vFile == null) {
                val group = NotificationGroupManager.getInstance().getNotificationGroup("TabTrail")
                group.createNotification(
                    "TabTrail: Cannnot find file ",
                    "could not open ${entry.fileUrl}",
                    NotificationType.WARNING
                )
                return
            }

            OpenFileDescriptor(project, vFile).navigate(true)
        }

    }
}
