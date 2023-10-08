package com.boostyboys.mcs.model.season

import com.boostyboys.mcs.model.match.MatchWithGames
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonWithMatches(
    @SerialName("matches") val matches: List<MatchWithGames>,
)
