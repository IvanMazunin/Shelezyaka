package org.orng.shelezyaka.core.domain.models

enum class GamePhase {
    MORNING, DAY, EVENING
}

data class GamePhaseState(
    val phase: GamePhase,
    val startTime: Long, // Время начала фазы
    val durationMs: Long, // Длительность фазы в миллисекундах
    val playersReady: Set<PlayerId>, // Игроки, завершившие действия
    val phaseCompleted: Boolean = false
) {
    fun isTimeExpired(currentTime: Long): Boolean {
        return currentTime - startTime >= durationMs
    }

    fun allPlayersReady(totalPlayers: Int): Boolean {
        return playersReady.size >= totalPlayers
    }

    fun canProceed(currentTime: Long, totalPlayers: Int): Boolean {
        return phaseCompleted ||
                isTimeExpired(currentTime) ||
                allPlayersReady(totalPlayers)
    }

    fun markPlayerReady(playerId: PlayerId): GamePhaseState {
        val updatedReady = playersReady.toMutableSet()
        updatedReady.add(playerId)
        return copy(playersReady = updatedReady)
    }
}