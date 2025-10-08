package org.orng.shelezyaka.core.domain.repositories

import org.orng.shelezyaka.core.domain.models.Catastrophe
import org.orng.shelezyaka.core.domain.models.CatastropheId
import org.orng.shelezyaka.core.domain.models.ResourceId

interface CatastropheRepository {
    suspend fun getAll(): List<Catastrophe>
    suspend fun findById(id: CatastropheId): Catastrophe?
    suspend fun findByIds(ids: Set<CatastropheId>): List<Catastrophe>
    suspend fun getAvailableForResources(availableResourceIds: Set<ResourceId>): List<Catastrophe>
}