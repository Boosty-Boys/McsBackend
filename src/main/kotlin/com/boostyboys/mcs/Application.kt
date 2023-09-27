package com.boostyboys.mcs

import com.boostyboys.mcs.data.league.LeagueController
import com.boostyboys.mcs.data.league.LeagueRepository
import com.boostyboys.mcs.data.match.MatchController
import com.boostyboys.mcs.data.match.MatchRepository
import com.boostyboys.mcs.data.player.PlayerController
import com.boostyboys.mcs.data.player.PlayerRepository
import com.boostyboys.mcs.data.season.SeasonsController
import com.boostyboys.mcs.data.season.SeasonsRepository
import com.boostyboys.mcs.di.KodeinController
import com.boostyboys.mcs.di.bindHttpClient
import com.boostyboys.mcs.di.bindSingleton
import io.ktor.client.HttpClient
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import org.kodein.di.DI
import org.kodein.di.Instance
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.type.jvmType

private const val LOCAL_PORT = 8080

/**
 * An entry point of the embedded-server
 **/
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: LOCAL_PORT
    embeddedServer(Netty, port = port) {
        kodeinApplication {
            install(DefaultHeaders) {
                header(HttpHeaders.Server, "ktor")
            }

            install(CallLogging)

            // swagger
            install(CORS) {
                anyHost()
                allowHeader(HttpHeaders.ContentType)
            }
            bindSingleton { di -> PlayerController(di) }

            bindSingleton { di -> SeasonsController(di) }

            bindSingleton { di -> LeagueController(di) }

            bindSingleton { di -> MatchController(di) }

            bind<PlayerRepository>() with factory { PlayerRepository(instance()) }

            bind<LeagueRepository>() with factory { LeagueRepository(instance()) }

            bind<SeasonsRepository>() with factory { SeasonsRepository(instance()) }

            bind<MatchRepository>() with factory { MatchRepository(instance()) }

            bindHttpClient()
        }
    }.start(wait = true)
}

fun Application.kodeinApplication(
    kodeinMapper: DI.MainBuilder.(Application) -> Unit = {},
) {
    val application = this

    // Allows using classes annotated with @Resources to represent URLs.
    // They are typed, can be constructed to generate URLs, and can be used to register routes.
    application.install(Resources)

    val kodein = DI {
        bind<Application>() with instance(application)
        kodeinMapper(this, application)
    }

    val httpClient: HttpClient by kodein.instance()
    autoCloseClient(httpClient)

    /**
     * Detects all the registered [KodeinController] and registers its routes.
     */
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yml")

        for (bind in kodein.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && KodeinController::class.java.isAssignableFrom(bindClass)) {
                val res by kodein.Instance(bind.key.type)
                (res as KodeinController).apply { registerRoutes() }
            }
        }
    }
}

fun Application.autoCloseClient(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopped) {
        httpClient.close()
    }
}
