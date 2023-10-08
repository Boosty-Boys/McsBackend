package com.boostyboys.mcs.model.season

import com.boostyboys.mcs.model.match.FlatMatchWithGames
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDataResponse(
    @SerialName("season_id") val seasonId: String,
    @SerialName("teams") val teams: List<TeamWithResults>,
    @SerialName("matches") val matches: List<FlatMatchWithGames>,
)
