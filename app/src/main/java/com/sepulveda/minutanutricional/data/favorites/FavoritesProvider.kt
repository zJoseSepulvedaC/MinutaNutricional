package com.sepulveda.minutanutricional.data.favorites

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.AUTHORITY
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.TABLE
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.ALL_COLUMNS
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.COL_CAL
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.COL_DAY
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.COL_ID
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.COL_MEAL
import com.sepulveda.minutanutricional.data.favorites.FavoritesContract.COL_NAME

class FavoritesProvider : ContentProvider() {

    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, TABLE, 1)
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        if (matcher.match(uri) != 1) return null
        val cols = projection ?: ALL_COLUMNS
        val cursor = MatrixCursor(cols)

        FavoritesRepository.all().forEach { fav ->
            val row = Array<Any?>(cols.size) { null }
            for (i in cols.indices) {
                when (cols[i]) {
                    COL_ID -> row[i] = fav.id
                    COL_DAY -> row[i] = fav.dayOfWeek
                    COL_NAME -> row[i] = fav.name
                    COL_MEAL -> row[i] = fav.mealType
                    COL_CAL -> row[i] = fav.calories
                }
            }
            cursor.addRow(row)
        }
        // Notifica que este cursor viene de un content provider con esta URI
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? =
        if (matcher.match(uri) == 1) "vnd.android.cursor.dir/vnd.minuta.favorite"
        else null

    // Solo lectura para cumplir rúbrica (no insert/update/delete)
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
