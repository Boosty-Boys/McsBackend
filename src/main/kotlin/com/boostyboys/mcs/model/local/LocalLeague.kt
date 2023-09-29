package com.boostyboys.mcs.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalLeague(
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("season_id")
    val seasonIds: List<String>,
)
