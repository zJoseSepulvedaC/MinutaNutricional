package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Modelo de datos
data class Recipe(
    val dayOfWeek: String,
    val name: String,
    val mealType: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val nutritionTips: List<String>,
    val calories: Int
)

// Datos de ejemplo (5 recetas semanales)
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
        steps = listOf("Precalentar horno", "Colocar ingredientes", "Hornear 45 min"),
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
        dayOfWeek = "Viernes", // corregido
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
fun WeeklyMenuScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Minuta semanal") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
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
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
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
                    }
                }
            }
        }
    }
}
