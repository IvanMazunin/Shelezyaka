package org.orng.shelezyaka.core.domain.models

typealias CatastropheEffect = (game: Game, currentDay: Int) -> Game

data class Catastrophe(
    val id: CatastropheId,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val effect: CatastropheEffect = { game, _ -> game } // Default: no effect
)