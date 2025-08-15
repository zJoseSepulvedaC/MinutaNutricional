package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
    // Estados para los campos
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Usuario") }
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }

    val roles = listOf("Usuario", "Administrador")
    val languages = listOf("Español", "Inglés", "Francés")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it }
                )
                Text("Recordar sesión")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Radio buttons
            Text("Tipo de usuario:")
            roles.forEach { role ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (role == selectedRole),
                        onClick = { selectedRole = role }
                    )
                    Text(role)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // ComboBox (DropdownMenu)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Idioma") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                selectedLanguage = language
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón entrar
            Button(
                onClick = { onNavigate("weekly") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            // Botones de navegación
            TextButton(onClick = { onNavigate("register") }) {
                Text("Crear cuenta")
            }
            TextButton(onClick = { onNavigate("forgot") }) {
                Text("Olvidé mi contraseña")
            }
        }
    }
}
