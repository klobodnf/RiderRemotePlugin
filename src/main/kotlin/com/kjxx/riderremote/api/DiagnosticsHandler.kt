package com.kjxx.riderremote.api

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.WindowManager
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosticItem(
    val severity: String,
    val message: String,
    val file: String = "",
    val line: Int = 0,
    val column: Int = 0
)

@Serializable
data class DiagnosticsResponse(
    val errors: List<DiagnosticItem>,
    val warnings: List<DiagnosticItem>,
    val info: List<DiagnosticItem>,
    val projectName: String
)

object DiagnosticsHandler {
    private val logger = Logger.getInstance(DiagnosticsHandler::class.java)
    private val errorList = mutableListOf<DiagnosticItem>()
    private val warningList = mutableListOf<DiagnosticItem>()

    suspend fun handle(call: ApplicationCall, project: Project) {
        logger.info("Diagnostics requested for project: ${project.name}")

        // Note: In a real implementation, we would listen to compiler messages
        // and populate these lists. For now, return empty lists.
        val response = DiagnosticsResponse(
            errors = errorList.toList(),
            warnings = warningList.toList(),
            info = emptyList(),
            projectName = project.name
        )

        call.respond(response)
    }

    fun addError(item: DiagnosticItem) {
        errorList.add(item)
    }

    fun addWarning(item: DiagnosticItem) {
        warningList.add(item)
    }

    fun clearDiagnostics() {
        errorList.clear()
        warningList.clear()
    }
}
