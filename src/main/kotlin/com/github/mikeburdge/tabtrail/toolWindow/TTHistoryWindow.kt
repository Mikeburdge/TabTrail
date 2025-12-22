package com.github.mikeburdge.tabtrail.toolWindow

import com.github.mikeburdge.tabtrail.services.TTHistoryStore
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

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

    class RecentFilesPanel(project: Project) {

        private val store: TTHistoryStore = project.service()

        private val model = DefaultListModel<String>()
        private val list = JBList(model)

        private val refreshButton = JButton("Refresh")

        val component: JComponent = JPanel(BorderLayout()).apply {
            val topBar = JPanel(BorderLayout()).apply {
                add(refreshButton, BorderLayout.WEST)
            }

            add(topBar, BorderLayout.NORTH)
            add(JBScrollPane(list), BorderLayout.CENTER)
        }


        init {
            // hook the UI Events
            refreshButton.addActionListener {
                refresh()
            }

            refresh()
        }

        private fun refresh() {
            // rebuilt the model every time
            model.clear()

            val entries = store.getEntries()

            for (entry in entries) {
                model.addElement(formatEntry(entry.fileUrl))
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


    }
}
