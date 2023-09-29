package com.boostyboys.mcs.model.local

import com.boostyboys.mcs.model.remote.shared.Match
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
) {
    companion object {
        fun Match.toLocal(): LocalMatch {
            return LocalMatch(
                week = week,
                teamOne = null,
                teamTwo = null,
                winningTeamId = winningTeamId,
                dateTime = createdAt,
                games = emptyList(),
            )
        }
    }
}
