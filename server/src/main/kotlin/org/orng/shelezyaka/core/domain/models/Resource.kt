package org.orng.shelezyaka.core.domain.models

typealias ResourceEffect = (player: Player, currentDay: Int) -> Player

data class Resource(
    val id: ResourceId,
    val name: String,
    val description: String,
    val helpText: String,
    val baseBuyPrice: Double,
    val baseSellPrice: Double,
    val imageUrl: String? = null,
    val effect: ResourceEffect = { player, _ -> player } // Default: no effect
) {
    companion object {
        // Стандартные эффекты ресурсов
        val DOUBLING_EFFECT: ResourceEffect = { player, currentDay ->
            if (currentDay % 2 == 0) {
                player.copy(
                    resources = player.resources.mapValues { (_, quantity) -> quantity * 2 }
                )
            } else {
                player
            }
        }

        val TECH_BOOM_EFFECT: ResourceEffect = { player, currentDay ->
            if (currentDay % 5 == 0) {
                val totalResources = player.resources.values.sum()
                val bonus = totalResources * 10
                player.copy(money = player.money + bonus)
            } else {
                player
            }
        }
    }
}