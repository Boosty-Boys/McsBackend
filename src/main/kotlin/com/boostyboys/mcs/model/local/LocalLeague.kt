package com.boostyboys.mcs.model.local

data class LocalLeague(
    val id: String,
    val name: String,
    val order: Int,
    val seasonIds: List<String>,
    val teamsIdsBySeason: Map<String, List<String>>,
)
