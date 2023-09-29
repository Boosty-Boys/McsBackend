package com.boostyboys.mcs.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalMatch(
    @SerialName("week") val week: Int,
    @SerialName("team_one") val teamOne: LocalTeam?,
    @SerialName("team_two") val teamTwo: LocalTeam?,
    @SerialName("winning_team_id") val winningTeamId: String?,
    @SerialName("date_time") val dateTime: String,
    @SerialName("games") val games: List<LocalGame>,
)
