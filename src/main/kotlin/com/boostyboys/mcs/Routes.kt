package com.boostyboys.mcs

import io.ktor.resources.Resource

/**
 * A class containing routes annotated with [Resource] to implement type-safe routing.
 */
object Routes {
    // get all players on a team for a date
    @Resource("/players")
    object PlayersOnTeamForDate
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/players?on_teams_date=4-20-2022&on_teams_team_ids=5ec9358e8c0dd900074685c4
    // http://localhost:8080/players?on_teams_date=4-20-2022&on_teams_team_ids=5ec9358e8c0dd900074685c4

    // get all leagues for a season
    @Resource("/leagues")
    object AllLeaguesWithSeasons
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/leagues
    // http://localhost:8080/leagues

    // get all matches and teams associated with a season
    @Resource("/season_data")
    object SeasonWithMatchesAndTeams
    // https://mcs-app-backend-c57cd7cec173.herokuapp.com/season_data
    // http://localhost:8080/season_data
    // {
    //    "season_id": "5f2c5e4f08c88e00084b44b7",
    //    "team_ids": [
    //        "5f2c5518754b2f00087df29a",
    //        "5f2c55189682770008b3b020",
    //        "5f2c55179682770008b3b01c",
    //        "5f2c5518754b2f00087df29b",
    //        "5f2c5517754b2f00087df295",
    //        "5f2c5517754b2f00087df297",
    //        "5f2c5517754b2f00087df298",
    //        "5f2c55179682770008b3b01d",
    //        "5f2c5517754b2f00087df296",
    //        "5f2c55179682770008b3b01e",
    //        "5f2c55179682770008b3b01f",
    //        "5f2c5518754b2f00087df299"
    //    ]
    // }
}
