package com.sepulveda.minutanutricional.data

/**
 * DTO simple usado por las pantallas (no es la entidad Room).
 */
data class Usuario(
    val id: Long = 0,
    val name: String,
    val email: String
)
