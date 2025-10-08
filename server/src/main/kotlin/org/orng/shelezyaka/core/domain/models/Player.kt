package org.orng.shelezyaka.core.domain.models

data class Player(
    val id: PlayerId,
    val username: String,
    val money: Double,
    val resources: Map<ResourceId, Int>, // resourceId -> quantity
    val credit: Double = 0.0,
    val hasTakenCredit: Boolean = false
) {
    fun canAfford(amount: Double): Boolean = money >= amount
    fun hasResource(resourceId: ResourceId): Boolean = (resources[resourceId] ?: 0) > 0
    fun getResourceQuantity(resourceId: ResourceId): Int = resources[resourceId] ?: 0
}