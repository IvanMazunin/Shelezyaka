package org.orng.shelezyaka.core.domain.models

data class GameConfig(
    val totalDays: Int,
    val startMoney: Double,
    val maxPlayers : Int,
    val availableResourceIds: Set<ResourceId>,
    val availableCatastropheIds: Set<CatastropheId>,
    val maxCreditMultiplier: Double = 2.0,
    val interestRate: Double = 0.1,
    val createdAt: Long = System.currentTimeMillis()
) {
    init {
        require(totalDays > 0) { "Total days must be positive" }
        require(startMoney > 0) { "Start money must be positive" }
        require(maxCreditMultiplier >= 1) { "Max credit multiplier must be >= 1" }
        require(interestRate >= 0) { "Interest rate cannot be negative" }
    }
}