package com.boostyboys.mcs.data.league

import com.boostyboys.mcs.Routes
import com.boostyboys.mcs.di.KodeinController
import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.requireQueryParameter
import com.boostyboys.mcs.model.response.ErrorMessage
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance

class LeagueController(override val di: DI) : KodeinController() {

    private val leagueRepository: LeagueRepository by instance()

    override fun Routing.registerRoutes() {
        get<Routes.AllLeaguesForSeason> {
            runCatching {
                val seasonNumber = call.requireQueryParameter("seasonNumber")
                leagueRepository.getAllLeaguesForSeason(seasonNumber = seasonNumber)
            }.fold(
                onSuccess = {
                    when (it) {
                        is Either.Success -> call.respondText(
                            text = Json.encodeToString(it.value.toTypedArray()),
                            contentType = ContentType.Application.Json,
                        )

                        is Either.Failure -> call.respondText(
                            text = Json.encodeToString(
                                it.error,
                            ),
                            contentType = ContentType.Application.Json,
                        )
                    }
                },
                onFailure = {
                    call.respondText(
                        text = Json.encodeToString(
                            ErrorMessage(
                                httpStatusCode = 500,
                                message = it.localizedMessage,
                            ),
                        ),
                        contentType = ContentType.Application.Json,
                    )
                },
            )
        }
    }
}
