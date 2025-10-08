package org.orng.shelezyaka.core.domain.repositories

import org.orng.shelezyaka.core.domain.models.Resource
import org.orng.shelezyaka.core.domain.models.ResourceId

interface ResourceRepository {
    suspend fun getAll(): List<Resource>
    suspend fun findById(id: ResourceId): Resource?
    suspend fun findByIds(ids: Set<ResourceId>): List<Resource>
    suspend fun getPrices(resourceId: ResourceId, day: Int): Pair<Double, Double>
}