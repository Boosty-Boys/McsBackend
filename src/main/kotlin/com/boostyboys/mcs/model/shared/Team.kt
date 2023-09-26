package com.boostyboys.mcs.model.shared

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

    // populated by us manually
    @SerialName("wins")
    var wins: Int = 0,
    @SerialName("losses")
    var losses: Int = 0,
)
