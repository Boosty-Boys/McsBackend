package com.boostyboys.mcs.data.league

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.league.LeagueWithSeasons
import com.boostyboys.mcs.model.response.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class LeagueRepository(private val client: HttpClient) {

    suspend fun getAllLeaguesWithSeasons(): Either<List<LeagueWithSeasons>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val leaguesResponseAsync = async {
                    client.get {
                        this.url("leagues?populate=seasons")
                    }
                }

                val leaguesResponse = leaguesResponseAsync.await()

                when {
                    leaguesResponse.status.isSuccess() -> {
                        val leaguesBody = leaguesResponse.body<List<LeagueWithSeasons>>()
                        Either.success(leaguesBody)
                    }
                    leaguesResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = leaguesResponse.status.value,
                                message = leaguesResponse.status.description,
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
