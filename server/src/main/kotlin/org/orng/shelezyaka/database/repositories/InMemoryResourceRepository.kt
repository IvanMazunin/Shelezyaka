package org.orng.shelezyaka.database.repositories

import org.orng.shelezyaka.core.domain.models.Resource
import org.orng.shelezyaka.core.domain.models.ResourceId
import org.orng.shelezyaka.core.domain.models.ResourcePool
import org.orng.shelezyaka.core.domain.repositories.ResourceRepository

class InMemoryResourceRepository : ResourceRepository {

    override suspend fun getAll(): List<Resource> = ResourcePool.getAllResources()

    override suspend fun findById(id: ResourceId): Resource? =
        ResourcePool.getResource(id)

    override suspend fun findByIds(ids: Set<ResourceId>): List<Resource> =
        ResourcePool.getResources(ids)

    override suspend fun getPrices(resourceId: ResourceId, day: Int): Pair<Double, Double> {
        val resource = findById(resourceId) ?: return 0.0 to 0.0

        val dayMultiplier = 1.0 + (day * 0.02)
        val volatility = 0.9 + (Math.random() * 0.2)

        return Pair(
            resource.baseBuyPrice * dayMultiplier * volatility,
            resource.baseSellPrice * dayMultiplier * volatility
        )
    }
}