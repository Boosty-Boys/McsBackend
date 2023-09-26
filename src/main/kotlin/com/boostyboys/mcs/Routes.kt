package com.boostyboys.mcs

import io.ktor.resources.Resource

/**
 * A class containing routes annotated with [Resource] to implement type-safe routing.
 */
object Routes {

    // get all teams for a season and league
    @Resource("/teams")
    object AllTeamsForSeasonAndLeague
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/teams?seasonNumber=2&leagueId=5f2c5e4f08c88e00084b44b6
    // http://localhost:8080/teams?seasonNumber=2&leagueId=5f2c5e4f08c88e00084b44b6

    // get all matches for a season and league
    @Resource("/matches")
    object AllMatchesForSeasonAndLeague
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/matches?seasonNumber=2&leagueId=5f2c5e4f08c88e00084b44b6
    // http://localhost:8080/matches?seasonNumber=2&leagueId=5f2c5e4f08c88e00084b44b6

    // get all players
    @Resource("/players")
    object AllPlayers
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/players
    // http://localhost:8080/players

    // get all seasons
    @Resource("/seasons")
    object AllSeasons
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/seasons
    // http://localhost:8080/seasons

    // get all leagues for a season
    @Resource("/leagues")
    object AllLeaguesForSeason
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/leagues?seasonNumber=3
    // http://localhost:8080/leagues?seasonNumber=3
}
