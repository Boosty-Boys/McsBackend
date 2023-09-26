package com.boostyboys.mcs.model.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    @SerialName("best_of")
    val bestOf: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("game_ids")
    val gameIds: List<String>,
    @SerialName("_id")
    val id: String,
    @SerialName("match_type")
    val matchType: String,
    @SerialName("players_to_teams")
    val playersToTeams: List<PlayersToTeam>,
    @SerialName("status")
    val status: String,
    @SerialName("team_ids")
    val teamIds: List<String>,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("week")
    val week: Int,
    @SerialName("winning_team_id")
    val winningTeamId: String?,
)
