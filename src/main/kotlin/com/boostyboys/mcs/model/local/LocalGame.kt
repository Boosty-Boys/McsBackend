package com.boostyboys.mcs.model.local

import com.boostyboys.mcs.model.remote.shared.Game
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalGame(
    @SerialName("_id") val gameId: String,
    @SerialName("game_number") val gameNumber: Int?,
    @SerialName("winning_team_id") val winningTeamId: String?,
) {
    companion object {
        fun Game.toLocal(): LocalGame {
            return LocalGame(
                gameId = id,
                gameNumber = gameNumber,
                winningTeamId = winningTeamId,
            )
        }
    }
}
