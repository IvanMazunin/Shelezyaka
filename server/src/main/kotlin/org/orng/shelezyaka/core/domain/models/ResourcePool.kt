package org.orng.shelezyaka.core.domain.models

object ResourcePool {
    private val resources: Map<ResourceId, Resource> = mapOf(
        ResourceId("nano_robots") to Resource(
            id = ResourceId("nano_robots"),
            name = "Нанороботы",
            description = "Удваиваются каждый четный день",
            helpText = "Микроскопические самореплицирующиеся машины",
            baseBuyPrice = 100.0,
            baseSellPrice = 50.0,
            effect =  Resource.DOUBLING_EFFECT
        ),

        ResourceId("quantum_chips") to Resource(
            id = ResourceId("quantum_chips"),
            name = "Квантовые чипы",
            description = "Приносят бонус каждые 5 дней",
            helpText = "Передовые процессоры для квантовых вычислений",
            baseBuyPrice = 200.0,
            baseSellPrice = 150.0,
            effect = Resource.TECH_BOOM_EFFECT
        ),

        ResourceId("solar_panels") to Resource(
            id = ResourceId("solar_panels"),
            name = "Солнечные панели",
            description = "Снижают стоимость покупки других ресурсов",
            helpText = "Экологичный источник энергии",
            baseBuyPrice = 80.0,
            baseSellPrice = 60.0,
            effect = { player, _ ->
                // Эффект: снижение стоимости покупки на 10%
                player // В реальной реализации добавим модификатор
            }
        ),

        ResourceId("ai_cores") to Resource(
            id = ResourceId("ai_cores"),
            name = "ИИ-ядра",
            description = "Предсказывают катастрофы",
            helpText = "Искусственный интеллект для анализа рынка",
            baseBuyPrice = 300.0,
            baseSellPrice = 220.0,
            effect = { player, _ -> player } // Специальный эффект через события
        )
    )

    fun getAllResources(): List<Resource> = resources.values.toList()
    fun getResource(id: ResourceId): Resource? = resources[id]
    fun getResources(ids: Set<ResourceId>): List<Resource> =
        ids.mapNotNull { resources[it] }
}