package com.boostyboys.mcs.model.local

import com.boostyboys.mcs.model.remote.shared.Team
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalTeam(
    @SerialName("_id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("wins") val wins: Int,
    @SerialName("losses") val losses: Int,
    @SerialName("players") val players: List<LocalPlayer>,
) {
    companion object {
        fun Team.toLocal(): LocalTeam {
            return LocalTeam(
                id = id,
                name = name,
                avatar = avatar,
                wins = wins,
                losses = losses,
                players = emptyList(),
            )
        }
    }
}
