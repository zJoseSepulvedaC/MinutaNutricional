package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("Correo electrónico") }
    val methods = listOf("Correo electrónico", "SMS", "Pregunta de seguridad")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar contraseña") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    methods.forEach { method ->
                        DropdownMenuItem(text = { Text(method) }, onClick = {
                            selectedMethod = method
                            expanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), enabled = email.isNotBlank()) {
                Text("Enviar instrucciones")
            }
        }
    }
}
