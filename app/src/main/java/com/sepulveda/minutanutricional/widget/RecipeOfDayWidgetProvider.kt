package com.sepulveda.minutanutricional.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sepulveda.minutanutricional.MainActivity
import com.sepulveda.minutanutricional.R
import com.sepulveda.minutanutricional.ui.screens.weeklyRecipes

class RecipeOfDayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_recipe_of_day)

            // Simple: toma una receta según el día actual (mod N)
            val idx = (System.currentTimeMillis() / (24 * 60 * 60 * 1000)).toInt() % weeklyRecipes.size
            val recipe = weeklyRecipes[idx]

            views.setTextViewText(R.id.widget_title, "Receta del día")
            views.setTextViewText(R.id.widget_subtitle, "${recipe.dayOfWeek} · ${recipe.mealType}")
            views.setTextViewText(R.id.widget_name, recipe.name)

            // Al tocar el widget -> abre la app (MainActivity)
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        fun requestUpdate(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, RecipeOfDayWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                (RecipeOfDayWidgetProvider()).onUpdate(context, manager, ids)
            }
        }
    }
}
