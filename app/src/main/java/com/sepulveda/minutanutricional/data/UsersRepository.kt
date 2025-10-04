package com.sepulveda.minutanutricional.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UsersRepository {

    // Debe inicializarse con init(context, scope)
    private lateinit var db: AppDatabase

    // DAO rápido (usa la clase UserDao, no el nombre del archivo)
    private val userDao: UserDao
        get() = db.userDao()

    private fun norm(email: String) = email.trim().lowercase()

    /**
     * Inicializar el repositorio con la instancia del DB.
     * Llamar una sola vez al arrancar la App.
     */
    fun init(context: Context, scope: CoroutineScope) {
        db = AppDatabase.getInstance(context, scope)

        // Pre-popular sólo si la tabla está vacía (se ejecuta en background)
        scope.launch(Dispatchers.IO) {
            val all = userDao.getAll()
            if (all.isEmpty()) {
                val demo = listOf(
                    "ana@example.com" to "AnaPass123",
                    "bruno@example.com" to "BrunoPass123",
                    "carla@example.com" to "CarlaPass123",
                    "diego@example.com" to "DiegoPass123",
                    "eva@example.com" to "EvaPass123"
                )
                demo.forEach { (email, pwd) ->
                    val salt = HashUtil.randomSalt()
                    val hash = HashUtil.hashPassword(pwd, salt)
                    val userEntity = User(
                        name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                        email = norm(email),
                        passwordHash = hash,
                        salt = salt
                    )
                    try {
                        userDao.insert(userEntity)
                    } catch (_: Exception) {
                        // ignorar errores de inserción en prepopulado
                    }
                }
            }
        }
    }

    suspend fun exists(email: String): Boolean = withContext(Dispatchers.IO) {
        val e = norm(email)
        userDao.findByEmail(e) != null
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val e = norm(email)
            val existing = userDao.findByEmail(e)
            if (existing != null) return@withContext Result.failure(IllegalStateException("Correo ya registrado"))

            val salt = HashUtil.randomSalt()
            val hash = HashUtil.hashPassword(password, salt)
            val userEntity = User(
                name = name,
                email = e,
                passwordHash = hash,
                salt = salt
            )
            try {
                userDao.insert(userEntity)
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    suspend fun login(email: String, password: String): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            val e = norm(email)
            val userEntity = userDao.findByEmail(e)
                ?: return@withContext Result.failure(IllegalArgumentException("Credenciales inválidas"))

            val computedHash = HashUtil.hashPassword(password, userEntity.salt)
            if (computedHash != userEntity.passwordHash) {
                return@withContext Result.failure(IllegalArgumentException("Credenciales inválidas"))
            }

            val usuario = Usuario(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email
            )
            Result.success(usuario)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            val e = norm(email)
            val userEntity = userDao.findByEmail(e) ?: return@withContext Result.failure(Exception("Usuario no encontrado"))
            val newSalt = HashUtil.randomSalt()
            val newHash = HashUtil.hashPassword(newPassword, newSalt)
            val updated = userEntity.copy(passwordHash = newHash, salt = newSalt)
            try {
                userDao.update(updated)
                Result.success(true)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    suspend fun getAllUsers(): List<Usuario> = withContext(Dispatchers.IO) {
        userDao.getAll().map { u ->
            Usuario(id = u.id, name = u.name, email = u.email)
        }
    }
}
