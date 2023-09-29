package com.boostyboys.mcs.data.season

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.local.LocalLeague
import com.boostyboys.mcs.model.local.LocalMatch.Companion.toLocal
import com.boostyboys.mcs.model.local.LocalSeason
import com.boostyboys.mcs.model.local.LocalTeam
import com.boostyboys.mcs.model.local.LocalTeam.Companion.toLocal
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import com.boostyboys.mcs.model.remote.seasons.SeasonsWithLeaguesAndTeamsMatchesResponseItem
import com.boostyboys.mcs.model.remote.shared.GameStatus
import com.boostyboys.mcs.model.remote.shared.Match
import com.boostyboys.mcs.model.remote.shared.MatchStatus
import com.boostyboys.mcs.model.remote.shared.SimpleGameResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class SeasonsRepository(private val client: HttpClient) {

    suspend fun getAllTeamsForSeasonAndLeague(
        seasonNumber: String,
        leagueId: String,
    ): Either<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val seasonsResponseAsync = async {
                    client.get {
                        this.url("seasons?populate=league&populate=teams&populate=matches")
                    }
                }

                val matchesResponseAsync = async {
                    client.get {
                        this.url("matches")
                    }
                }

                val seasonsResponse = seasonsResponseAsync.await()
                val matchesResponse = matchesResponseAsync.await()

                when {
                    seasonsResponse.status.isSuccess() && matchesResponse.status.isSuccess() -> {
                        val matchesBody = matchesResponse.body<List<Match>>()
                        val matchesMap = populateMatchesMap(matchesBody)

                        // filter for season + league
                        val seasonsBody =
                            seasonsResponse.body<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>>().filter {
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

                    matchesResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = matchesResponse.status.value,
                                message = matchesResponse.status.description,
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

    suspend fun getAllSeasons(): Either<List<LocalSeason>, ErrorMessage> {
        return runCatching {
            runBlocking {
                val seasonsResponseAsync = async {
                    client.get {
                        this.url("seasons?populate=league&populate=teams&populate=matches")
                    }
                }

                val matchesResponseAsync = async {
                    client.get {
                        this.url("matches")
                    }
                }

                val seasonsResponse = seasonsResponseAsync.await()
                val matchesResponse = matchesResponseAsync.await()

                when {
                    seasonsResponse.status.isSuccess() && matchesResponse.status.isSuccess() -> {
                        val matchesBody = matchesResponse.body<List<Match>>()
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

                    matchesResponse.status.isSuccess().not() -> {
                        Either.failure(
                            ErrorMessage(
                                httpStatusCode = matchesResponse.status.value,
                                message = matchesResponse.status.description,
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
                onSuccess = { either ->
                    either.map {
                        it.map { remoteSeason ->
                            LocalSeason(
                                id = remoteSeason.id,
                                name = remoteSeason.name,
                                league = LocalLeague(
                                    id = remoteSeason.league.id,
                                    name = remoteSeason.league.name,
                                    seasonIds = remoteSeason.league.seasonIds,
                                ),
                                teams = remoteSeason.teams.map { remoteTeam ->
                                    remoteTeam.toLocal()
                                },
                                matches = remoteSeason.matches.map { remoteMatch ->
                                    remoteMatch.toLocal()
                                },
                            )
                        }
                    }
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

internal val LOGGER = KtorSimpleLogger("WinsAndLossesLogger")

internal fun populateMatchesMap(matchesBody: List<Match>): Map<String, SimpleGameResult> {
    val map = mutableMapOf<String, SimpleGameResult>()
    matchesBody.forEach {
        if (it.status == MatchStatus.CLOSED && it.winningTeamId != null) {
            map[it.id] = SimpleGameResult(
                winner = GameStatus.Winner(it.winningTeamId),
                // games do not display loser, only winner. we will infer loser later
                loser = GameStatus.Unknown,
            )
        } else if (it.status == MatchStatus.MISSING) {
            map[it.id] = SimpleGameResult(
                winner = GameStatus.Unknown,
                loser = GameStatus.Unknown,
            )
        }
    }

    return map
}

internal fun populateWinsAndLosses(
    seasonsBody: List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>,
    matchesMap: Map<String, SimpleGameResult>,
) {
    // populate team wins + losses
    seasonsBody.forEach { season ->
        season.matches.forEach { match ->
            val matchId = match.id
            val teamIds = match.teamIds

            if (matchesMap.containsKey(matchId).not()) {
                LOGGER.error("No entry for matchId $matchId")
            }

            matchesMap[matchId]?.let {
                // for every game, increment the winner
                val winner = it.winner

                if (winner is GameStatus.Winner) {
                    // only update the status if we have a winner
                    season.teams.first { team ->
                        team.id == winner.id
                    }.wins++

                    // the loser is whoever wasn't the winner
                    val loserId = teamIds.first { teamId ->
                        teamId != winner.id
                    }

                    // also increment the loser
                    season.teams.first { team ->
                        team.id == loserId
                    }.losses++
                }
            }
        }
    }
}
