package com.boostyboys.mcs.di

import io.ktor.http.ContentType
import io.ktor.server.routing.Routing
import org.kodein.di.DIAware
import org.kodein.di.instance

abstract class KodeinController : DIAware {
    val application: ContentType.Application by instance()

    abstract fun Routing.registerRoutes()
}
