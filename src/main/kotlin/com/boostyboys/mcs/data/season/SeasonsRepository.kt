package com.boostyboys.mcs.data.season

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.match.MatchWithGames
import com.boostyboys.mcs.model.response.ErrorMessage
import com.boostyboys.mcs.model.season.SeasonWithMatches
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class SeasonsRepository(private val client: HttpClient) {

    suspend fun getAllMatchesForSeason(seasonId: String): Either<List<MatchWithGames>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val seasonResponseAsync = async {
                    client.get {
                        this.url("seasons/$seasonId?populate=matches.games")
                    }
                }

                val seasonResponse = seasonResponseAsync.await()

                when {
                    seasonResponse.status.isSuccess() -> {
                        val seasonBody = seasonResponse.body<SeasonWithMatches>()
                        Either.success(seasonBody.matches)
                    }

                    seasonResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = seasonResponse.status.value,
                                message = seasonResponse.status.description,
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
        }.fold(
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
