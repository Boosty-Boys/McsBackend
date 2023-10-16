package com.boostyboys.mcs.model.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("discord_id")
    val discordId: String?,
    @SerialName("franchise_id")
    val franchiseId: String?,
    @SerialName("hex_color")
    val hexColor: String?,
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("tier_name")
    val tierName: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("vars")
    val vars: List<Var>,
)
