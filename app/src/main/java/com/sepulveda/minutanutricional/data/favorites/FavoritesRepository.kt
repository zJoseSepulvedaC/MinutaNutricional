package com.sepulveda.minutanutricional.data.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FavoriteRecipe(
    val id: Long,           // simple autoincrement demo
    val dayOfWeek: String,
    val name: String,
    val mealType: String,
    val calories: Int
)

object FavoritesRepository {
    private var nextId = 1L
    private val _favorites = MutableStateFlow<List<FavoriteRecipe>>(emptyList())
    val favorites = _favorites.asStateFlow()

    fun add(day: String, name: String, meal: String, cal: Int) {
        if (_favorites.value.any { it.name == name && it.dayOfWeek == day && it.mealType == meal }) return
        _favorites.value = _favorites.value + FavoriteRecipe(nextId++, day, name, meal, cal)
    }

    fun remove(name: String, day: String) {
        _favorites.value = _favorites.value.filterNot { it.name == name && it.dayOfWeek == day }
    }

    fun all(): List<FavoriteRecipe> = _favorites.value
}
