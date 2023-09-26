package com.boostyboys.mcs.model.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class League(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("season_ids")
    val seasonIds: List<String>,
    @SerialName("updated_at")
    val updatedAt: String,
)
