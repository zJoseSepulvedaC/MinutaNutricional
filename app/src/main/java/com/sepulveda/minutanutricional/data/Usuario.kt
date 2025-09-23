package com.sepulveda.minutanutricional.data

data class Usuario(
    val email: String,
    val password: String,
    val name: String = ""
)
