package com.boostyboys.mcs.model.shared

data class SimpleGameResult(
    val winner: GameStatus.Winner,
    val loser: GameStatus,
)

sealed interface GameStatus {
    @JvmInline
    value class Winner(val id: String) : GameStatus

    @JvmInline
    value class Loser(val id: String) : GameStatus

    object Unknown : GameStatus
}
