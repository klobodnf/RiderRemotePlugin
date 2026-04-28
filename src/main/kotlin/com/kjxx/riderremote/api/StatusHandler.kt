package com.kjxx.riderremote.api

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val projectName: String,
    val projectPath: String,
    val isBuilding: Boolean = false,
    val isRunning: Boolean = false,
    val serverStatus: String = "running"
)

object StatusHandler {
    private val logger = Logger.getInstance(StatusHandler::class.java)

    suspend fun handle(call: ApplicationCall, project: Project) {
        logger.info("Status requested for project: ${project.name}")

        val response = StatusResponse(
            projectName = project.name,
            projectPath = project.basePath ?: "",
            serverStatus = "running"
        )

        call.respond(response)
    }
}
