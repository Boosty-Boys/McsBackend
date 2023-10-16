package com.boostyboys.mcs.data.team

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.response.ErrorMessage
import com.boostyboys.mcs.model.team.Team
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class TeamsRepository(private val client: HttpClient) {

    suspend fun getTeamsFromIds(teamIds: List<String>): Either<List<Team>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val teamsResponseAsync = async {
                    client.get {
                        var queryString = ""
                        teamIds.forEachIndexed { index, teamId ->
                            queryString += if (index == 0) {
                                "?_id=$teamId"
                            } else {
                                "&_id=$teamId"
                            }
                        }

                        this.url("teams$queryString")
                    }
                }

                val teamsResponse = teamsResponseAsync.await()

                when {
                    teamsResponse.status.isSuccess() -> {
                        val teamsBody = teamsResponse.body<List<Team>>()
                        Either.success(teamsBody)
                    }
                    teamsResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = teamsResponse.status.value,
                                message = teamsResponse.status.description,
                            ),
                        )
                    }
                    else -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = 500,
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
