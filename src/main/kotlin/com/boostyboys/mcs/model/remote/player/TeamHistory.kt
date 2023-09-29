package com.boostyboys.mcs.model.remote.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamHistory(
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("date_left")
    val dateLeft: String?,
    @SerialName("_id")
    val id: String,
    @SerialName("team_id")
    val teamId: String,
)
