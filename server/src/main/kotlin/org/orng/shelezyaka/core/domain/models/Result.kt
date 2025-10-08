package org.orng.shelezyaka.core.domain.models

sealed class OperationResult<out T> {
    data class Success<out T>(val data: T) : OperationResult<T>()
    data class Failure(val error: String) : OperationResult<Nothing>()
}

// Специализированные результаты для доменной логики
sealed class GameOperationResult {
    object Success : GameOperationResult()
    data class Failure(val reason: String) : GameOperationResult()
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}