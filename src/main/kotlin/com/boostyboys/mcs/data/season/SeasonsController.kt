package com.boostyboys.mcs.data.season

import com.boostyboys.mcs.Routes
import com.boostyboys.mcs.data.team.TeamsRepository
import com.boostyboys.mcs.di.KodeinController
import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.match.FlatMatchWithGames
import com.boostyboys.mcs.model.match.MatchStatus
import com.boostyboys.mcs.model.response.ErrorMessage
import com.boostyboys.mcs.model.season.SeasonDataRequest
import com.boostyboys.mcs.model.season.SeasonDataResponse
import com.boostyboys.mcs.model.season.TeamWithResults
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import kotlinx.coroutines.async
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance

class SeasonsController(override val di: DI) : KodeinController() {
    private val seasonsRepository: SeasonsRepository by instance()
    private val teamsRepository: TeamsRepository by instance()

    override fun Routing.registerRoutes() {
        get<Routes.SeasonWithMatchesAndTeams> {
            runCatching {
                val requestBody = call.receive<SeasonDataRequest>()

                val matchesForSeasonResponseAsync = async {
                    seasonsRepository.getAllMatchesForSeason(requestBody.seasonId)
                }

                val teamsFromIdsResponseAsync = async {
                    teamsRepository.getTeamsFromIds(requestBody.teamIds)
                }

                val matchesForSeasonResponse = matchesForSeasonResponseAsync.await()
                val teamsFromIdsResponse = teamsFromIdsResponseAsync.await()

                Triple(requestBody.seasonId, matchesForSeasonResponse, teamsFromIdsResponse)
            }.fold(
                onSuccess = { (seasonId, matchesResponse, teamsResponse) ->
                    val matches = when (matchesResponse) {
                        is Either.Success -> matchesResponse.value
                        is Either.Failure -> return@fold call.respondText(
                            text = Json.encodeToString(matchesResponse.error),
                            contentType = ContentType.Application.Json,
                        )
                    }.map { match ->
                        FlatMatchWithGames(
                            id = match.id,
                            status = match.status,
                            week = match.week,
                            bestOf = match.bestOf,
                            teamOneId = match.teamIds.getOrNull(0),
                            teamTwoId = match.teamIds.getOrNull(1),
                            winningTeamId = match.winningTeamId,
                            isForfeit = match.forfeitedByTeam != null,
                            scheduledDateTime = match.scheduledDatetime,
                            games = match.games,
                            playersToTeams = match.playersToTeams,
                            streamLink = match.streamLink,
                        )
                    }

                    val teams = when (teamsResponse) {
                        is Either.Success -> teamsResponse.value
                        is Either.Failure -> return@fold call.respondText(
                            text = Json.encodeToString(teamsResponse.error),
                            contentType = ContentType.Application.Json,
                        )
                    }.map {
                        TeamWithResults(
                            id = it.id,
                            name = it.name,
                            avatar = it.avatar,
                            matchesWon = 0,
                            matchesPlayed = 0,
                            gamesWon = 0,
                            gamesPlayed = 0,
                        )
                    }

                    fun processMatchForTeam(match: FlatMatchWithGames, teamId: String?) {
                        teams.find { team ->
                            team.id == teamId
                        }?.let { team ->
                            if (match.status == MatchStatus.CLOSED) {
                                team.matchesPlayed++
                            }

                            if (match.winningTeamId == team.id) {
                                team.matchesWon++
                            }

                            match.games.forEach { game ->
                                if (game.status == MatchStatus.CLOSED) {
                                    team.gamesPlayed++
                                }

                                if (game.winningTeamId == team.id) {
                                    team.gamesWon++
                                }
                            }
                        }
                    }

                    matches.forEach { match ->
                        processMatchForTeam(match, match.teamOneId)
                        processMatchForTeam(match, match.teamTwoId)
                    }

                    call.respond(
                        SeasonDataResponse(
                            seasonId = seasonId,
                            matches = matches,
                            teams = teams,
                        ),
                    )
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
