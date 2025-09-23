package com.sepulveda.minutanutricional.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositorio simple en memoria con normalización de email (trim + lowercase).
 * Semilla con usuarios demo. En producción: reemplazar por API/DB y hashing de password.
 */
object UsersRepository {

    private fun norm(email: String): String = email.trim().lowercase()

    // Estado interno tipado explícitamente
    private val _users: MutableStateFlow<List<Usuario>> = MutableStateFlow(
        listOf(
            Usuario(email = norm("ana@example.com"),   password = "AnaPass123",   name = "Ana"),
            Usuario(email = norm("bruno@example.com"), password = "BrunoPass123", name = "Bruno"),
            Usuario(email = norm("carla@example.com"), password = "CarlaPass123", name = "Carla"),
            Usuario(email = norm("diego@example.com"), password = "DiegoPass123", name = "Diego"),
            Usuario(email = norm("eva@example.com"),   password = "EvaPass123",   name = "Eva"),
        )
    )

    // Exponer como StateFlow tipado
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    fun exists(email: String): Boolean =
        _users.value.any { u: Usuario -> u.email == norm(email) }

    fun register(name: String, email: String, password: String): Result<Unit> {
        val e = norm(email)
        if (exists(e)) return Result.failure(IllegalStateException("Correo ya registrado"))
        _users.update { current: List<Usuario> -> current + Usuario(email = e, password = password, name = name) }
        return Result.success(Unit)
    }

    fun login(email: String, password: String): Result<Usuario> {
        val e = norm(email)
        val user: Usuario = _users.value.firstOrNull { u: Usuario -> u.email == e }
            ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))

        if (user.password != password) {
            return Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
        return Result.success(user)
    }
}
