package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    tts: TtsHelper,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Chile") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val countries = listOf("Chile", "Argentina", "Perú", "México")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro", modifier = Modifier.semantics { heading() }) },
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
                                "Formulario de registro. Ingresa nombre, correo, " +
                                        "contraseña y confirmación. Selecciona tu país y acepta " +
                                        "los términos para habilitar el botón Registrarme."
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo (requerido)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de nombre completo" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico (requerido)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de correo electrónico" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña, mínimo 8 caracteres") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de contraseña" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
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
                        contentDescription = "Campo de confirmación de contraseña"
                        if (errorText != null) stateDescription = "Error: $errorText"
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCountry,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("País") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .semantics { contentDescription = "Selecciona país. Actual: $selectedCountry" }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .semantics { contentDescription = "País $country" }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Aceptar términos y condiciones" }
                )
                Text("Acepto los términos y condiciones")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorText = when {
                        name.isBlank() || email.isBlank() ->
                            "Completa los campos requeridos."
                        password.length < 8 ->
                            "La contraseña debe tener al menos 8 caracteres."
                        password != confirmPassword ->
                            "Las contraseñas no coinciden."
                        !acceptTerms ->
                            "Debes aceptar los términos y condiciones."
                        else -> null
                    }
                    if (errorText == null) onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Registrarme" },
                enabled = name.isNotBlank() && email.isNotBlank() &&
                        password.isNotBlank() && confirmPassword.isNotBlank() &&
                        acceptTerms
            ) { Text("Registrarme") }
        }
    }
}
