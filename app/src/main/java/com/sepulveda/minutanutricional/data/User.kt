package com.sepulveda.minutanutricional.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Añadimos un índice único sobre "email" para evitar registros duplicados por correo.se
@Entity(
    tableName = "users",
    indices = [ Index(value = ["email"], unique = true) ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "passwordHash")
    val passwordHash: String,

    @ColumnInfo(name = "salt")
    val salt: String
)
