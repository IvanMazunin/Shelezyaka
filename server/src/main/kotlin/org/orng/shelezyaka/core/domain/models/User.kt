package org.orng.shelezyaka.core.domain.models

import java.util.UUID

data class User(
    val id: UserId,
    val username: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
) {
    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }
    }

    fun authenticate(password: String, hasher: (String) -> String): Boolean {
        return passwordHash == hasher(password)
    }
}