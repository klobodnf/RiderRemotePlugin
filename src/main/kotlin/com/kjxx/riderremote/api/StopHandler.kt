package com.kjxx.riderremote.api

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class StopResponse(
    val status: String,
    val message: String
)

object StopHandler {
    private val logger = Logger.getInstance(StopHandler::class.java)

    suspend fun handle(call: ApplicationCall, project: Project) {
        logger.info("Stop requested for project: ${project.name}")

        try {
            val actionManager = ActionManager.getInstance()
            val action = actionManager.getAction("Stop")

            if (action != null) {
                ApplicationManager.getApplication().invokeLater {
                    actionManager.tryToExecute(action, null, null, null, false)
                }
                call.respond(
                    StopResponse(
                        status = "stopped",
                        message = "Triggered Rider Stop action (Ctrl+F2)"
                    )
                )
            } else {
                call.respond(
                    StopResponse(
                        status = "error",
                        message = "Stop action not found"
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Stop failed", e)
            call.respond(
                StopResponse(
                    status = "error",
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }
}
