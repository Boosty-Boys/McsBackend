package com.boostyboys.mcs.data.match

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.local.LocalGame.Companion.toLocal
import com.boostyboys.mcs.model.local.LocalMatch
import com.boostyboys.mcs.model.local.LocalTeam.Companion.toLocal
import com.boostyboys.mcs.model.remote.match.MatchesWithGamesAndTeamsResponseItem
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class MatchRepository(private val client: HttpClient) {

    suspend fun getAllMatches(): Either<List<LocalMatch>, ErrorMessage> {
        val seasonsResponse: HttpResponse = client.get {
            this.url("matches?populate=games&populate=teams")
        }.body()

        return if (seasonsResponse.status.isSuccess()) {
            val body = seasonsResponse.body<List<MatchesWithGamesAndTeamsResponseItem>>()

            val matches = body.map {
                LocalMatch(
                    week = it.week,
                    teamOne = it.teams.getOrNull(0)?.toLocal(),
                    teamTwo = it.teams.getOrNull(1)?.toLocal(),
                    winningTeamId = it.winningTeamId,
                    dateTime = it.createdAt,
                    games = it.games.map { game ->
                        game.toLocal()
                    },
                )
            }

            Either.success(matches)
        } else {
            Either.failure(
                ErrorMessage(
                    httpStatusCode = seasonsResponse.status.value,
                    message = seasonsResponse.status.description,
                ),
            )
        }
    }
}
