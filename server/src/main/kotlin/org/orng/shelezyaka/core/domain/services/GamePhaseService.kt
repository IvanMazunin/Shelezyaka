package org.orng.shelezyaka.core.domain.services

import org.orng.shelezyaka.core.domain.repositories.GameRepository

class GamePhaseService(
    private val gameRepository: GameRepository,
    private val eventRepository: GameEventRepo,
    private val gameEngineService: GameEngineService
) {

    suspend fun startPhase(gameId: GameId, phase: GamePhase): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        val currentTime = System.currentTimeMillis()
        val updatedGame = game.initializePhaseState(phase, currentTime)

        // Сохраняем событие начала фазы
        eventRepository.saveEvent(
            PhaseStartedEvent(
                gameId = gameId,
                phase = phase,
                day = game.currentDay,
                durationMs = updatedGame.phaseState!!.durationMs,
                startTime = currentTime
            )
        )

        // Запускаем логику фазы
        when (phase) {
            GamePhase.MORNING -> {
                // Утренняя фаза - игроки покупают ресурсы
                // Ничего не делаем, ждем действий игроков
            }
            GamePhase.DAY -> {
                // Дневная фаза - автоматическая обработка катастрофы
                gameEngineService.processDayPhase(gameId)
            }
            GamePhase.EVENING -> {
                // Вечерняя фаза - игроки продают ресурсы
                // Ничего не делаем, ждем действий игроков
            }
        }

        return gameRepository.save(updatedGame)
    }

    suspend fun markPlayerReady(gameId: GameId, playerId: PlayerId): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        val updatedGame = game.markPlayerReady(playerId)

        // Сохраняем событие готовности игрока
        eventRepository.saveEvent(
            PlayerReadyEvent(
                gameId = gameId,
                playerId = playerId,
                phase = game.phase,
                readyPlayers = updatedGame.phaseState!!.playersReady,
                totalPlayers = game.players.size
            )
        )

        // Проверяем, все ли игроки готовы
        if (updatedGame.phaseState!!.allPlayersReady(game.players.size)) {
            return completePhase(gameId, PhaseCompletionReason.ALL_PLAYERS_READY)
        }

        return gameRepository.save(updatedGame)
    }

    suspend fun checkAndCompletePhases(): List<Game> {
        val activeGames = gameRepository.getActiveGames()
        val currentTime = System.currentTimeMillis()
        val completedGames = mutableListOf<Game>()

        for (game in activeGames) {
            val phaseState = game.phaseState ?: continue

            if (phaseState.canProceed(currentTime, game.players.size)) {
                val reason = if (phaseState.isTimeExpired(currentTime)) {
                    PhaseCompletionReason.TIME_EXPIRED
                } else {
                    PhaseCompletionReason.ALL_PLAYERS_READY
                }

                when (val result = completePhase(game.id, reason)) {
                    is OperationResult.Success -> completedGames.add(result.data)
                    is OperationResult.Failure -> {
                        // Логируем ошибку, но продолжаем обработку других игр
                        println("Failed to complete phase for game ${game.id}: ${result.error}")
                    }
                }
            } else {
                // Отправляем предупреждение о скором окончании времени
                sendTimeWarningIfNeeded(game, currentTime)
            }
        }

        return completedGames
    }

    private suspend fun completePhase(
        gameId: GameId,
        reason: PhaseCompletionReason
    ): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        val completedGame = game.completePhase()

        // Сохраняем событие завершения фазы
        eventRepository.saveEvent(
            PhaseCompletedEvent(
                gameId = gameId,
                phase = game.phase,
                completedBy = reason,
                nextPhase = getNextPhase(game.phase)
            )
        )

        // Определяем следующую фазу
        val nextPhase = getNextPhase(game.phase)
        return if (nextPhase != null) {
            startPhase(gameId, nextPhase)
        } else {
            // Игра завершена или переход на следующий день
            handleDayCompletion(gameId)
        }
    }

    private fun getNextPhase(currentPhase: GamePhase): GamePhase? {
        return when (currentPhase) {
            GamePhase.MORNING -> GamePhase.DAY
            GamePhase.DAY -> GamePhase.EVENING
            GamePhase.EVENING -> null // Завершаем день
        }
    }

    private suspend fun handleDayCompletion(gameId: GameId): OperationResult<Game> {
        val game = gameRepository.findById(gameId) ?:
        return OperationResult.Failure("Game not found")

        // Проверяем, завершена ли игра
        return if (game.currentDay >= game.config.totalDays) {
            gameEngineService.finishGame(gameId)
        } else {
            // Переход на следующий день
            gameEngineService.processNextDay(gameId)
        }
    }

    private suspend fun sendTimeWarningIfNeeded(game: Game, currentTime: Long) {
        val phaseState = game.phaseState ?: return
        val remainingMs = phaseState.durationMs - (currentTime - phaseState.startTime)

        // Отправляем предупреждение за 10 секунд до конца
        if (remainingMs in 1000L..10000L) {
            eventRepository.saveEvent(
                PhaseTimeWarningEvent(
                    gameId = game.id,
                    phase = game.phase,
                    remainingMs = remainingMs
                )
            )
        }
    }
}