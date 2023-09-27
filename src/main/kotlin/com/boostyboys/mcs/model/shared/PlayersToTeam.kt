package com.boostyboys.mcs.model.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayersToTeam(
    @SerialName("_id")
    val id: String,
    @SerialName("player_id")
    val playerId: String,
    @SerialName("team_id")
    val teamId: String,
)