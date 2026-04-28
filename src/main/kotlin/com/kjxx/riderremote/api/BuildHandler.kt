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
data class BuildRequest(
    val target: String = "",
    val configuration: String = "Development",
    val rebuild: Boolean = false
)

@Serializable
data class BuildResponse(
    val status: String,
    val message: String,
    val rebuild: Boolean = false
)

object BuildHandler {
    private val logger = Logger.getInstance(BuildHandler::class.java)

    suspend fun handle(call: ApplicationCall, project: Project) {
        val request = try {
            call.receive<BuildRequest>()
        } catch (e: Exception) {
            BuildRequest()
        }

        logger.info("Build requested: target=${request.target}, config=${request.configuration}, rebuild=${request.rebuild}")

        try {
            val actionManager = ActionManager.getInstance()
            val possibleIds = listOf(
                request.target,
                if (request.rebuild) "RebuildProject" else "Build",
                "Cpp.BuildProject",
                "CMake.BuildProject",
                "CompileProject",
                "MakeProject",
                "BuildSolutionAction",
                "CompileDirty",
                "BuildMenu",
                "BuildProject"
            ).filter { it.isNotEmpty() }.distinct()

            var foundAction: com.intellij.openapi.actionSystem.AnAction? = null
            var foundId = ""
            for (id in possibleIds) {
                val act = actionManager.getAction(id)
                if (act != null) {
                    foundAction = act
                    foundId = id
                    break
                }
            }

            if (foundAction != null) {
                ApplicationManager.getApplication().invokeLater {
                    actionManager.tryToExecute(foundAction, null, null, null, false)
                }
                val response = BuildResponse(
                    status = "building",
                    message = "Triggered Rider internal build action: $foundId",
                    rebuild = request.rebuild
                )
                call.respond(response)
            } else {
                call.respond(
                    BuildResponse(
                        status = "error",
                        message = "No build action found. Tried: ${possibleIds.joinToString()}"
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Build failed", e)
            call.respond(
                BuildResponse(
                    status = "error",
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }
}
