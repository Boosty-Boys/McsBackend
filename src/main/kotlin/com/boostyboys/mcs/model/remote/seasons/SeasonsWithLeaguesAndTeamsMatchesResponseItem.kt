package com.boostyboys.mcs.model.remote.seasons

import com.boostyboys.mcs.model.remote.shared.League
import com.boostyboys.mcs.model.remote.shared.Match
import com.boostyboys.mcs.model.remote.shared.Team
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonsWithLeaguesAndTeamsMatchesResponseItem(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("_id")
    val id: String,
    @SerialName("match_ids")
    val matchIds: List<String>,
    @SerialName("name")
    val name: String,
    @SerialName("player_ids")
    val playerIds: List<String>,
    @SerialName("team_ids")
    val teamIds: List<String>,
    @SerialName("teams")
    val teams: List<Team>,
    @SerialName("league")
    val league: League,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("matches")
    val matches: List<Match>,
)
