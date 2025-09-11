package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper
import com.sepulveda.minutanutricional.data.usuariosDemo
import androidx.compose.ui.semantics.role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    tts: TtsHelper,
    onNavigate: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Usuario") }
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val roles = listOf("Usuario", "Administrador")
    val languages = listOf("Español", "Inglés", "Francés")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Login", modifier = Modifier.semantics { heading() })
                }
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
            // Usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario o correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de usuario o correo" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = errorText != null,
                supportingText = { if (errorText != null) Text(errorText!!) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de contraseña"
                        if (errorText != null) stateDescription = "Error: $errorText"
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Recordar sesión
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Recordar sesión" }
                )
                Text("Recordar sesión")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Tipo de usuario (radio)
            Text("Tipo de usuario:", modifier = Modifier.semantics { heading() })
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                roles.forEach { role ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (role == selectedRole),
                            onClick = { selectedRole = role },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .semantics {
                                    contentDescription = "Seleccionar rol $role"
                                    this.role = Role.RadioButton
                                }
                        )
                        Text(role)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Idioma (combo)
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
                        .semantics { contentDescription = "Selecciona idioma. Actual: $selectedLanguage" }
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
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .semantics { contentDescription = "Idioma $language" }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Acciones
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        errorText = when {
                            username.isBlank() -> "Ingresa tu usuario o correo."
                            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
                            usuariosDemo.none { it.usuario == username && it.password == password } ->
                                "Usuario o contraseña incorrectos."
                            else -> null
                        }
                        if (errorText == null) onNavigate("weekly")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Botón Iniciar sesión" }
                ) { Text("Entrar") }

                OutlinedButton(
                    onClick = {
                        tts.speak(
                            "Pantalla de inicio de sesión. " +
                                    "Ingresa tu usuario o correo y tu contraseña. " +
                                    "Puedes elegir tipo de usuario e idioma. " +
                                    "Pulsa Entrar para continuar."
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Escuchar instrucciones" }
                ) { Text("Escuchar") }
            }

            TextButton(
                onClick = { onNavigate("register") },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Crear cuenta" }
            ) { Text("Crear cuenta") }

            TextButton(
                onClick = { onNavigate("forgot") },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Olvidé mi contraseña" }
            ) { Text("Olvidé mi contraseña") }
        }
    }
}
