package com.kjxx.riderremote.api

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class RunRequest(
    val configuration: String = "",
    val mode: String = "run" // "run" or "debug"
)

@Serializable
data class RunResponse(
    val status: String,
    val message: String,
    val configuration: String = ""
)

object RunHandler {
    private val logger = Logger.getInstance(RunHandler::class.java)

    suspend fun handle(call: ApplicationCall, project: Project) {
        val request = try {
            call.receive<RunRequest>()
        } catch (e: Exception) {
            RunRequest()
        }

        logger.info("Run requested: config=${request.configuration}, mode=${request.mode}")

        try {
            val actionManager = ActionManager.getInstance()
            val actionId = if (request.mode == "debug") "Debug" else "Run"
            val action = actionManager.getAction(actionId)

            if (action != null) {
                ApplicationManager.getApplication().invokeLater {
                    actionManager.tryToExecute(action, null, null, null, false)
                }
                call.respond(
                    RunResponse(
                        status = "running",
                        message = "Triggered Rider action: $actionId (Shift+F9 for debug)",
                        configuration = request.configuration
                    )
                )
            } else {
                call.respond(
                    RunResponse(
                        status = "error",
                        message = "Action not found: $actionId"
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Run failed", e)
            call.respond(
                RunResponse(
                    status = "error",
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }
}
