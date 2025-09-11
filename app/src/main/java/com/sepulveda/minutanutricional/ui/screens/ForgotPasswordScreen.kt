package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    tts: TtsHelper,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("Correo electrónico") }
    var info by remember { mutableStateOf<String?>(null) }

    val methods = listOf("Correo electrónico", "SMS", "Pregunta de seguridad")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Recuperar contraseña",
                        modifier = Modifier.semantics { heading() }
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
                    OutlinedButton(
                        onClick = {
                            tts.speak(
                                "Recuperar contraseña. Ingresa tu correo registrado y elige el método " +
                                        "de recuperación. Presiona Enviar instrucciones."
                            )
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics { contentDescription = "Escuchar instrucciones" }
                    ) { Text("Escuchar") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo registrado") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de correo registrado" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Método de recuperación (accesible)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Método de recuperación") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor() // 👈 volvemos a la versión simple
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "Selecciona método de recuperación. Actual: $selectedMethod"
                        }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    methods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                selectedMethod = method
                                expanded = false
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .semantics { contentDescription = "Método $method" }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val mensaje =
                        "Si el correo existe, te enviaremos instrucciones por $selectedMethod."
                    info = mensaje
                    tts.speak(mensaje)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Enviar instrucciones" },
                enabled = email.isNotBlank()
            ) { Text("Enviar instrucciones") }

            if (info != null) {
                Text(
                    text = info!!,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .semantics { stateDescription = info ?: "" }
                )
            }
        }
    }
}
