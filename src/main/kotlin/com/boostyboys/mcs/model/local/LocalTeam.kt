package com.boostyboys.mcs.model.local

data class LocalTeam(
    val id: String,
    val name: String,
    val logoUrl: String,
    val wins: Int,
    val losses: Int,
    val players: List<LocalPlayer>,
    val leagueId: String,
    val seasonId: String,
)
