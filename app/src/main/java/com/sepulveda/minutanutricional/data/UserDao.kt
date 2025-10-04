package com.sepulveda.minutanutricional.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>
}
