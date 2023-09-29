package com.boostyboys.mcs.model.local

data class LocalMatch(
    val week: Int,
    val teamOne: LocalTeam,
    val teamTwo: LocalTeam,
    val winningTeamId: String,
    val dateTime: String,
    val games: List<LocalGame>,
)
