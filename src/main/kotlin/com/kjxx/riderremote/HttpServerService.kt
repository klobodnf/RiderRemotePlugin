package com.kjxx.riderremote

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.kjxx.riderremote.api.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class HttpServerService(
    private val scope: CoroutineScope,
    private val project: Project
) {
    private val logger = Logger.getInstance(HttpServerService::class.java)
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    fun start() {
        scope.launch {
            try {
                server = embeddedServer(CIO, port = 9878, host = "127.0.0.1") {
                    configureServer()
                }.start(wait = false)
                logger.info("HTTP server started on http://127.0.0.1:9878")
            } catch (e: Exception) {
                logger.error("Failed to start HTTP server", e)
            }
        }
    }

    fun stop() {
        try {
            server?.stop(1000, 2000)
            logger.info("HTTP server stopped")
        } catch (e: Exception) {
            logger.error("Error stopping HTTP server", e)
        }
    }

    private fun Application.configureServer() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }

        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            anyHost()
        }

        routing {
            // Status
            get("/api/status") {
                StatusHandler.handle(call, project)
            }

            // Build
            post("/api/build") {
                BuildHandler.handle(call, project)
            }

            // Diagnostics
            get("/api/diagnostics") {
                DiagnosticsHandler.handle(call, project)
            }

            // Run
            post("/api/run") {
                RunHandler.handle(call, project)
            }

            // Stop current task
            post("/api/stop") {
                StopHandler.handle(call, project)
            }
        }
    }
}
