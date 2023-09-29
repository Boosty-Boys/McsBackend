package com.boostyboys.mcs.model.remote.shared

data class SimpleGameResult(
    val winner: GameStatus,
    val loser: GameStatus,
)

sealed interface GameStatus {
    @JvmInline
    value class Winner(val id: String) : GameStatus

    object Unknown : GameStatus
}
