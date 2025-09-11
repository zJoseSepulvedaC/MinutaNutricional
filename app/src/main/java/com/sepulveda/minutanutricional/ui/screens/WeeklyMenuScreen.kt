package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper

// ---------- Modelo de datos ----------
data class Recipe(
    val dayOfWeek: String,
    val name: String,
    val mealType: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val nutritionTips: List<String>,
    val calories: Int
)

// ---------- Datos de ejemplo ----------
val weeklyRecipes = listOf(
    Recipe(
        dayOfWeek = "Lunes",
        name = "Ensalada de Quinoa",
        mealType = "Almuerzo",
        ingredients = listOf("Quinoa", "Tomate", "Pepino", "Aceite de oliva", "Limón"),
        steps = listOf("Cocinar quinoa", "Picar verduras", "Mezclar y aliñar"),
        nutritionTips = listOf("Alta en fibra", "Proteínas vegetales"),
        calories = 420
    ),
    Recipe(
        dayOfWeek = "Martes",
        name = "Pollo al horno con verduras",
        mealType = "Cena",
        ingredients = listOf("Pollo", "Zanahoria", "Zapallo italiano", "Aceite de oliva", "Sal"),
        steps = listOf("Precalentar horno", "Colocar ingredientes", "Hornear 45 minutos"),
        nutritionTips = listOf("Bajo en grasas", "Fuente de proteínas magras"),
        calories = 500
    ),
    Recipe(
        dayOfWeek = "Miércoles",
        name = "Avena con frutas",
        mealType = "Desayuno",
        ingredients = listOf("Avena", "Leche", "Banana", "Fresas", "Miel"),
        steps = listOf("Cocinar avena", "Agregar frutas", "Endulzar con miel"),
        nutritionTips = listOf("Energía sostenida", "Vitaminas y antioxidantes"),
        calories = 350
    ),
    Recipe(
        dayOfWeek = "Jueves",
        name = "Sopa de lentejas",
        mealType = "Almuerzo",
        ingredients = listOf("Lentejas", "Zanahoria", "Papa", "Cebolla", "Ajo"),
        steps = listOf("Cocinar lentejas", "Agregar verduras", "Condimentar al gusto"),
        nutritionTips = listOf("Rica en hierro", "Bajo índice glucémico"),
        calories = 380
    ),
    Recipe(
        dayOfWeek = "Viernes",
        name = "Pescado a la plancha con ensalada",
        mealType = "Cena",
        ingredients = listOf("Filete de pescado", "Lechuga", "Tomate", "Aceite de oliva"),
        steps = listOf("Cocinar pescado", "Preparar ensalada", "Servir"),
        nutritionTips = listOf("Omega-3 beneficioso", "Bajo en grasas saturadas"),
        calories = 410
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyMenuScreen(
    tts: TtsHelper,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Minuta semanal",
                        modifier = Modifier.semantics { heading() } // Encabezado para TalkBack
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics { contentDescription = "Atrás" }
                    ) { Text("Atrás") }
                },
                actions = {
                    // Lee en voz alta el resumen de toda la semana
                    OutlinedButton(
                        onClick = {
                            val resumen = weeklyRecipes.joinToString(". ") { r ->
                                "${r.dayOfWeek}: ${r.mealType} ${r.name}, ${r.calories} kilocalorías"
                            }
                            tts.speak("Resumen de la minuta semanal. $resumen.")
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics { contentDescription = "Escuchar resumen semanal" }
                    ) { Text("Escuchar todo") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(weeklyRecipes) { recipe ->
                RecipeCard(recipe = recipe, tts = tts)
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, tts: TtsHelper) {
    val resumenCard = "${recipe.dayOfWeek}, ${recipe.mealType}: ${recipe.name}. " +
            "Calorías: ${recipe.calories}."

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            // Resumen que TalkBack puede leer al enfocar la tarjeta
            .semantics { contentDescription = resumenCard }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${recipe.dayOfWeek} - ${recipe.mealType}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Ingredientes:", style = MaterialTheme.typography.titleSmall)
            Text("• " + recipe.ingredients.joinToString("\n• "))

            Spacer(modifier = Modifier.height(8.dp))
            Text("Pasos:", style = MaterialTheme.typography.titleSmall)
            Text(recipe.steps.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n"))

            Spacer(modifier = Modifier.height(8.dp))
            Text("Recomendaciones:", style = MaterialTheme.typography.titleSmall)
            Text("• " + recipe.nutritionTips.joinToString("\n• "))

            Spacer(modifier = Modifier.height(8.dp))
            Text("Calorías: ${recipe.calories} kcal")

            Spacer(modifier = Modifier.height(12.dp))
            // Botón para leer esta receta en voz alta
            OutlinedButton(
                onClick = {
                    val texto = buildString {
                        append("${recipe.dayOfWeek}. ${recipe.mealType}: ${recipe.name}. ")
                        append("Ingredientes: ${recipe.ingredients.joinToString(", ")}. ")
                        append("Pasos: ${recipe.steps.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString(". ")}. ")
                        append("Recomendaciones: ${recipe.nutritionTips.joinToString(", ")}. ")
                        append("Calorías: ${recipe.calories}.")
                    }
                    tts.speak(texto)
                },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Escuchar receta de ${recipe.dayOfWeek}" }
            ) { Text("Escuchar receta") }
        }
    }
}
