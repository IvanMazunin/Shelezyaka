package org.orng.shelezyaka.core.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameTimerService(
    private val gamePhaseService: GamePhaseService
) {
    private var isRunning = false

    fun start() {
        isRunning = true
        // Запускаем в отдельном потоке/корутине
        CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                try {
                    gamePhaseService.checkAndCompletePhases()
                    delay(1000) // Проверяем каждую секунду
                } catch (e: Exception) {
                    // Логируем ошибку, но продолжаем работу
                    println("Error in GameTimerService: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        isRunning = false
    }
}