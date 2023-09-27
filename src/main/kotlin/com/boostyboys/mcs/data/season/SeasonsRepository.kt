package com.boostyboys.mcs.data.season

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.response.ErrorMessage
import com.boostyboys.mcs.model.seasons.SeasonsWithLeaguesAndTeamsMatchesResponseItem
import com.boostyboys.mcs.model.shared.GameStatus
import com.boostyboys.mcs.model.shared.Match
import com.boostyboys.mcs.model.shared.SimpleGameResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.logging.KtorSimpleLogger

class SeasonsRepository(private val client: HttpClient) {

    suspend fun getAllTeamsForSeasonAndLeague(
        seasonNumber: String,
        leagueId: String,
    ): Either<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>, ErrorMessage> {
        return runCatching {
            // todo joer make this async
            val seasonsResponse: HttpResponse = client.get {
                this.url("seasons?populate=league&populate=teams&populate=matches")
            }

            val gamesResponse: HttpResponse = client.get {
                this.url("matches")
            }

            when {
                seasonsResponse.status.isSuccess() && gamesResponse.status.isSuccess() -> {
                    val matchesBody = gamesResponse.body<List<Match>>()
                    val matchesMap = populateMatchesMap(matchesBody)

                    // filter for season + league
                    val seasonsBody = seasonsResponse.body<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>>().filter {
                        it.name == seasonNumber
                    }.filter {
                        it.league.id == leagueId
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

    suspend fun getAllSeasons(): Either<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>, ErrorMessage> {
        return runCatching {
            // todo joer make this async
            val seasonsResponse: HttpResponse = client.get {
                this.url("seasons?populate=league&populate=teams&populate=matches")
            }

            val gamesResponse: HttpResponse = client.get {
                this.url("matches")
            }

            when {
                seasonsResponse.status.isSuccess() && gamesResponse.status.isSuccess() -> {
                    val matchesBody = gamesResponse.body<List<Match>>()
                    val matchesMap = populateMatchesMap(matchesBody)

                    val seasonsBody = seasonsResponse.body<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>>()

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

internal fun populateMatchesMap(matchesBody: List<Match>): Map<String, SimpleGameResult> {
    val map = mutableMapOf<String, SimpleGameResult>()
    matchesBody.forEach {
        // todo joer make enum
        if (it.status.equals("closed", ignoreCase = true) && it.winningTeamId != null) {
            map[it.id] = SimpleGameResult(
                winner = GameStatus.Winner(it.winningTeamId),
                // games do not display loser, only winner. we will infer loser later
                loser = GameStatus.Unknown,
            )
        }
    }

    return map
}

internal val LOGGER = KtorSimpleLogger("WinsAndLossesLogger")

// todo joer where does this live
internal fun populateWinsAndLosses(seasonsBody: List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>, matchesMap: Map<String, SimpleGameResult>) {
    // populate team wins + losses
    seasonsBody.forEach { season ->
        season.matches.forEach { match ->
            val matchId = match.id
            val teamIds = match.teamIds

            if (matchesMap.containsKey(matchId).not()) {
                LOGGER.error("No winner / loser for matchId $matchId")
            }

            matchesMap[matchId]?.let {
                // for every game, increment the winner
                val winner = it.winner

                // the loser is whoever wasn't the winner
                val loserId = teamIds.first { teamId ->
                    teamId != winner.id
                }
                season.teams.first { team ->
                    team.id == winner.id
                }.wins++

                // also increment the loser
                season.teams.first { team ->
                    team.id == loserId
                }.losses++
            }
        }
    }
}
