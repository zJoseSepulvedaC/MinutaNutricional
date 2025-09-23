package com.sepulveda.minutanutricional.data.favorites

import android.net.Uri

object FavoritesContract {
    const val AUTHORITY = "com.sepulveda.minutanutricional.favorites"
    const val TABLE = "recipes"

    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE")

    // Columnas del cursor
    const val COL_ID = "_id"               // requerido por cursors
    const val COL_DAY = "dayOfWeek"
    const val COL_NAME = "name"
    const val COL_MEAL = "mealType"
    const val COL_CAL = "calories"

    val ALL_COLUMNS = arrayOf(COL_ID, COL_DAY, COL_NAME, COL_MEAL, COL_CAL)
}
