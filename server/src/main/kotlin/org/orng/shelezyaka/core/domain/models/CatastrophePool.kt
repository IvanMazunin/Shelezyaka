package org.orng.shelezyaka.core.domain.models

object CatastrophePool {
    private val catastrophes: Map<CatastropheId, Catastrophe> = mapOf(
        CatastropheId("money_halving") to Catastrophe(
            id =  CatastropheId("money_halving"),
            name = "Денежная реформа",
            description = "Все деньги делятся пополам",
            effect = { game, _ ->
                // Специфическая для ресурса катастрофа
                game // В реальной реализации добавим модификатор цен
            }
        ),

        CatastropheId("credit_forgiveness") to Catastrophe(
            id =  CatastropheId("credit_forgiveness"),
            name = "Кредитная амнистия",
            description = "Все кредиты прощаются",
            effect = { game, _ ->
                // Специфическая для ресурса катастрофа
                game // В реальной реализации добавим модификатор цен
            }
        ),

        CatastropheId("nano_boom") to Catastrophe(
            id = CatastropheId("nano_boom"),
            name = "Нано-бум",
            description = "Цена нанороботов удваивается",
            effect = { game, _ ->
                // Специфическая для ресурса катастрофа
                game // В реальной реализации добавим модификатор цен
            }
        ),

        CatastropheId("tech_crash") to Catastrophe(
            id =  CatastropheId("tech_crash"),
            name = "Технологический крах",
            description = "Технологические ресурсы теряют ценность",
            effect = { game, _ ->
                // Затрагивает только tech-ресурсы
                game
            }
        )
    )

    fun getAllCatastrophes(): List<Catastrophe> = catastrophes.values.toList()
    fun getCatastrophe(id: CatastropheId): Catastrophe? = catastrophes[id]
    fun getCatastrophes(ids: Set<CatastropheId>): List<Catastrophe> =
        ids.mapNotNull { catastrophes[it] }

    // Фильтрация катастроф по доступным ресурсам
    fun getAvailableCatastrophes(availableResourceIds: Set<ResourceId>): List<Catastrophe> {
        return catastrophes.values.filter { catastrophe ->
            isCatastropheAvailable(catastrophe, availableResourceIds)
        }
    }

    private fun isCatastropheAvailable(
        catastrophe: Catastrophe,
        availableResourceIds: Set<ResourceId>
    ): Boolean {
        return when (catastrophe.id.value) {
            "nano_boom" -> availableResourceIds.contains(ResourceId("nano_robots"))
            "tech_crash" -> availableResourceIds.any { it.value.contains("tech", ignoreCase = true) }
            else -> true // Общие катастрофы всегда доступны
        }
    }
}