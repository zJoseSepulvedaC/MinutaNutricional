package com.sepulveda.minutanutricional.data

data class Usuario(val usuario: String, val password: String)

val usuariosDemo: Array<Usuario> = arrayOf(
    Usuario("ana@example.com", "AnaPass123"),
    Usuario("bruno@example.com", "BrunoPass123"),
    Usuario("carla@example.com", "CarlaPass123"),
    Usuario("diego@example.com", "DiegoPass123"),
    Usuario("eva@example.com", "EvaPass123")
)
