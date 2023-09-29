package com.boostyboys.mcs.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalSeason(
    @SerialName("_id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("league") val league: LocalLeague,
    @SerialName("teams") val teams: List<LocalTeam>,
    @SerialName("matches") val matches: List<LocalMatch>,
)
