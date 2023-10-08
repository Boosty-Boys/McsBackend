package com.boostyboys.mcs.model.season

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDataRequest(
    @SerialName("season_id") val seasonId: String,
    @SerialName("team_ids") val teamIds: List<String>,
)
