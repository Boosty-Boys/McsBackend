package com.boostyboys.mcs.model.league

import com.boostyboys.mcs.model.season.Season
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueWithSeasons(
    @SerialName("_id") val id: String,
    @SerialName("name") val name: String, // e.g. "Rising Star"
    @SerialName("current_week") val currentWeek: String, // e.g. "1"
    @SerialName("current_season_id") val currentSeasonId: String,
    @SerialName("seasons") val seasons: List<Season>,
)
