package com.boostyboys.mcs.model.seasons

import com.boostyboys.mcs.model.shared.League
import com.boostyboys.mcs.model.shared.Team
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonsWithLeaguesAndTeamsResponseItem(
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
)
