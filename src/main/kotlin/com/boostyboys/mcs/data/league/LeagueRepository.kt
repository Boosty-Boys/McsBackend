package com.boostyboys.mcs.data.league

import com.boostyboys.mcs.data.season.populateMatchesMap
import com.boostyboys.mcs.data.season.populateWinsAndLosses
import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import com.boostyboys.mcs.model.remote.seasons.SeasonsWithLeaguesAndTeamsMatchesResponseItem
import com.boostyboys.mcs.model.remote.shared.Match
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class LeagueRepository(private val client: HttpClient) {

    suspend fun getAllLeaguesForSeason(seasonNumber: String): Either<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val seasonsResponseAsync = async {
                    client.get {
                        this.url("seasons?populate=league&populate=teams&populate=matches")
                    }
                }

                val gamesResponseAsync = async {
                    client.get {
                        this.url("matches")
                    }
                }

                val seasonsResponse = seasonsResponseAsync.await()
                val gamesResponse = gamesResponseAsync.await()

                when {
                    seasonsResponse.status.isSuccess() && gamesResponse.status.isSuccess() -> {
                        val matchesBody = gamesResponse.body<List<Match>>()
                        val matchesMap = populateMatchesMap(matchesBody)

                        // filter for season + league
                        val seasonsBody = seasonsResponse.body<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>>().filter {
                            it.name == seasonNumber
                        }

                        populateWinsAndLosses(seasonsBody, matchesMap)

                        Either.success(
                            seasonsBody,
                        )
                    }
                    seasonsResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = seasonsResponse.status.value,
                                message = seasonsResponse.status.description,
                            ),
                        )
                    }
                    gamesResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = gamesResponse.status.value,
                                message = gamesResponse.status.description,
                            ),
                        )
                    }
                    else -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = HttpStatusCode.InternalServerError.value,
                                message = "Unknown error occurred",
                            ),
                        )
                    }
                }
            }
        }
            .fold(
                onSuccess = {
                    it
                },
                onFailure = {
                    Either.failure(
                        ErrorMessage(
                            httpStatusCode = HttpStatusCode.InternalServerError.value,
                            message = it.localizedMessage,
                        ),
                    )
                },
            )
    }
}
