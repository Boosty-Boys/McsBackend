package com.boostyboys.mcs.data.match

import com.boostyboys.mcs.Routes
import com.boostyboys.mcs.data.season.SeasonsRepository
import com.boostyboys.mcs.di.KodeinController
import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.remote.match.MatchesWithGamesAndTeamsResponseItem
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import com.boostyboys.mcs.model.remote.seasons.SeasonsWithLeaguesAndTeamsMatchesResponseItem
import com.boostyboys.mcs.model.requireQueryParameter
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import kotlinx.coroutines.async
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance

class MatchController(override val di: DI) : KodeinController() {
    private val matchRepository: MatchRepository by instance()
    private val seasonRepository: SeasonsRepository by instance()

    override fun Routing.registerRoutes() {
        get<Routes.AllMatchesForSeasonAndLeague> {
            runCatching {
                val seasonNumber = call.requireQueryParameter("seasonNumber")
                val leagueId = call.requireQueryParameter("leagueId")
                val teamsResponse = async {
                    seasonRepository.getAllTeamsForSeasonAndLeague(
                        seasonNumber = seasonNumber,
                        leagueId = leagueId,
                    )
                }

                val matchesResponse = async { matchRepository.getAllMatches() }

                val teams = teamsResponse.await()
                val matches = matchesResponse.await()

                when {
                    matches is Either.Success && teams is Either.Success -> {
                        processMatches(matches = matches, teams = teams)
                    }

                    matches is Either.Failure -> {
                        matches
                    }

                    teams is Either.Failure -> {
                        teams
                    }

                    else -> {
                        // just pick one
                        matches
                    }
                }
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

    private fun processMatches(
        matches: Either.Success<List<MatchesWithGamesAndTeamsResponseItem>>,
        teams: Either.Success<List<SeasonsWithLeaguesAndTeamsMatchesResponseItem>>,
    ): Either<List<MatchesWithGamesAndTeamsResponseItem>, ErrorMessage> {
        // create set of all team IDs for the given season + league
        val setOfTeamIdsForSeasonAndLeague = mutableSetOf<String>()

        // team id -> wins + losses for quicker lookup of wins and losses for a team
        val teamsMap = mutableMapOf<String, Pair<Int, Int>>()
        teams.value.forEach {
            setOfTeamIdsForSeasonAndLeague.addAll(it.teamIds)
            it.teams.forEach { team ->
                if (teamsMap.containsKey(team.id).not()) {
                    teamsMap[team.id] = Pair(team.wins, team.losses)
                }
            }
        }

        // set teams wins and losses
        return Either.success(
            matches.value.filter {
                it.teamIds.containsAny(setOfTeamIdsForSeasonAndLeague)
            }.map { match ->
                match.teams.map {
                    it.wins = teamsMap[it.id]?.first ?: 0
                    it.losses = teamsMap[it.id]?.second ?: 0
                    it
                }
                match
            },
        )
    }
}

/**
 * Returns true if the receiving collection contains any of the elements in the specified collection.
 *
 * @param elements the elements to look for in the receiving collection.
 * @return true if any element in [elements] is found in the receiving collection.
 */
fun <T> Collection<T>.containsAny(elements: Collection<T>): Boolean {
    val set = if (elements is Set) elements else elements.toSet()
    return any(set::contains)
}
