package com.kjxx.riderremote

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class RemotePlugin : ProjectActivity {

    private val logger = Logger.getInstance(RemotePlugin::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        private var server: HttpServerService? = null

        fun getServer(): HttpServerService? = server
    }

    override suspend fun execute(project: Project) {
        logger.info("RiderRemotePlugin initializing for project: ${project.name}")

        if (server == null) {
            server = HttpServerService(scope, project)
            server?.start()
            logger.info("RiderRemote HTTP server started on port 9878")
        }
    }

    fun dispose() {
        logger.info("RiderRemotePlugin disposing")
        server?.stop()
        server = null
        scope.cancel()
    }
}
