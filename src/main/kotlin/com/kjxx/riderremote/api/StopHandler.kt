package com.kjxx.riderremote.api

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
            // Note: Stopping a running process is complex in IntelliJ
            // For now, we just return a message
            call.respond(
                StopResponse(
                    status = "stopped",
                    message = "Stop signal sent (implementation depends on run configuration type)"
                )
            )
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
