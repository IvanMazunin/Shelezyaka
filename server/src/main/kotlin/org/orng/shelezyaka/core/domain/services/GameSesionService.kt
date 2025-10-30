package org.orng.shelezyaka.core.domain.services

class GameSessionService {
    private val sessions = mutableMapOf<String, GameSession>()
    private val disasters = listOf(
        Disaster(
            type = DisasterType.RESOURCE_SWAP,
            target = DisasterTarget.RANDOM_PLAYER,
            description = "Случайные игроки меняются ресурсами",
            effect = { session ->
                val players = session.players.filter { it.role == PlayerRole.PLAYER }.shuffled()
                if (players.size >= 2) {
                    val resourceTypes = players[0].resources.keys.intersect(players[1].resources.keys)
                    if (resourceTypes.isNotEmpty()) {
                        val resource = resourceTypes.random()
                        val temp = players[0].resources[resource] ?: 0
                        players[0].resources[resource] = players[1].resources[resource] ?: 0
                        players[1].resources[resource] = temp
                    }
                }
            }
        ),
        Disaster(
            type = DisasterType.PRICE_INCREASE,
            target = DisasterTarget.SPECIFIC_RESOURCE,
            description = "Стоимость покупки ресурса увеличивается в 4 раза",
            effect = { session ->
                val resource = session.availableResources.random()
                session.market.buyPrices[resource] = (session.market.buyPrices[resource] ?: 10) * 4
            }
        )
    )

    fun createSession(createRequest: CreateSessionRequest): GameSession {
        val session = GameSession(
            name = createRequest.name,
            creatorId = createRequest.creatorId,
            maxPlayers = createRequest.maxPlayers,
            availableResources = createRequest.availableResources,
            initialCredit = createRequest.initialCredit,
            creditRate = createRequest.creditRate,
            totalDays = createRequest.totalDays,
            disastersEnabled = createRequest.disastersEnabled
        )

        sessions[session.id] = session
        return session
    }

    fun joinSession(sessionId: String, playerName: String, role: PlayerRole): Player? {
        val session = sessions[sessionId] ?: return null
        if (session.players.size >= session.maxPlayers && role == PlayerRole.PLAYER) {
            return null
        }

        val player = Player(name = playerName, sessionId = sessionId, role = role)
        if (role == PlayerRole.PLAYER) {
            player.credit = session.initialCredit
        }

        session.players.add(player)
        return player
    }

    fun startGame(sessionId: String): Boolean {
        val session = sessions[sessionId] ?: return false
        if (session.players.none { it.role == PlayerRole.PLAYER }) return false

        session.isActive = true
        initializeMarket(session)
        return true
    }

    private fun initializeMarket(session: GameSession) {
        val basePrices = mapOf(
            ResourceType.IRON to 10,
            ResourceType.COAL to 8,
            ResourceType.OIL to 15,
            ResourceType.GOLD to 50,
            ResourceType.URANIUM to 100,
            ResourceType.FOOD to 5,
            ResourceType.WATER to 3
        )

        session.market.buyPrices.clear()
        session.availableResources.forEach { resource ->
            session.market.buyPrices[resource] = basePrices[resource] ?: 10
        }
    }
}
