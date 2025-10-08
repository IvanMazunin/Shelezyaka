package org.orng.shelezyaka.core.domain.models

import java.util.UUID

@JvmInline
value class PlayerId(val value: UUID)

@JvmInline
value class GameId(val value: UUID)

@JvmInline
value class LobbyId(val value: UUID)

@JvmInline
value class UserId(val value: UUID)

@JvmInline
value class ResourceId(val value: String)

@JvmInline
value class CatastropheId(val value: String)