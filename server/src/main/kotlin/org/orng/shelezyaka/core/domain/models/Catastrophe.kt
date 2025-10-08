package org.orng.shelezyaka.core.domain.models

typealias CatastropheEffect = (game: Game, currentDay: Int) -> Game

data class Catastrophe(
    val id: CatastropheId,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val effect: CatastropheEffect = { game, _ -> game } // Default: no effect
) {
    companion object {
        // Стандартные эффекты катастроф
        val HALF_MONEY_EFFECT: CatastropheEffect = { game, _ ->
            game.copy(
                players = game.players.mapValues { (_, player) ->
                    player.copy(money = player.money / 2)
                }
            )
        }

        val CREDIT_FORGIVENESS_EFFECT: CatastropheEffect = { game, _ ->
            game.copy(
                players = game.players.mapValues { (_, player) ->
                    player.copy(credit = 0.0)
                }
            )
        }

        val DOUBLE_SELL_PRICES_EFFECT: CatastropheEffect = { game, _ ->
            // Здесь можно добавить временный модификатор
            game
        }
    }
}