package com.boostyboys.mcs.model.local

data class LocalSeason(
    val id: String,
    val name: String,
    val order: Int,
    val leagueIds: List<String>,
    val weeks: Int,
)
