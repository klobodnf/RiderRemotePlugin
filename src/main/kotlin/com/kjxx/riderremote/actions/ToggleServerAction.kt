package com.kjxx.riderremote.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.kjxx.riderremote.RemotePlugin

class ToggleServerAction : AnAction("Toggle Remote Server") {
    override fun actionPerformed(e: AnActionEvent) {
        val server = RemotePlugin.getServer()
        if (server != null) {
            Messages.showInfoMessage(
                "Rider Remote server is running on http://127.0.0.1:9878",
                "Rider Remote Status"
            )
        } else {
            Messages.showWarningDialog(
                "Rider Remote server is not running",
                "Rider Remote Status"
            )
        }
    }
}
