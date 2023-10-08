package com.boostyboys.mcs.model.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchWithGames(
    @SerialName("_id")
    val id: String,
    @SerialName("best_of")
    val bestOf: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("forfeit_datetime")
    val forfeitDatetime: String?,
    @SerialName("forfeited_by_team")
    val forfeitedByTeam: String?,
    @SerialName("game_ids")
    val gameIds: List<String>,
    @SerialName("games")
    val games: List<Game>,
    @SerialName("match_type")
    val matchType: String,
    @SerialName("players_to_teams")
    val playersToTeams: List<PlayersToTeam>,
    @SerialName("scheduled_datetime")
    val scheduledDatetime: String?,
    @SerialName("status")
    val status: MatchStatus,
    @SerialName("stream_link")
    val streamLink: String?,
    @SerialName("team_ids")
    val teamIds: List<String>,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("week")
    val week: Int,
    @SerialName("winning_team_id")
    val winningTeamId: String?,
)
