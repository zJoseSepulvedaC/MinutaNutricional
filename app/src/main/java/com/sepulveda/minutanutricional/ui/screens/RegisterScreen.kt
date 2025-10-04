package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper
import com.sepulveda.minutanutricional.data.UsersRepository
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    tts: TtsHelper,
    repo: UsersRepository,
    onRegistered: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Chile") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val countries = listOf("Chile", "Argentina", "Perú", "México")

    fun normalizeEmail(s: String) = s.trim().lowercase()
    fun isEmailFormatOk(s: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(s)

    // Centralizar la acción de registro para usarla desde botón o IME Done
    fun performRegister() {
        // Validaciones UI
        nameError = if (name.isBlank()) "Completa tu nombre." else null
        val emailNorm = normalizeEmail(email)
        emailError = when {
            email.isBlank() -> "Completa tu correo."
            !isEmailFormatOk(emailNorm) -> "Formato de correo inválido."
            else -> null
        }
        passError = if (password.length < 8) "La contraseña debe tener al menos 8 caracteres." else null
        confirmError = if (confirmPassword != password) "Las contraseñas no coinciden." else null
        termsError = if (!acceptTerms) "Debes aceptar los términos para continuar." else null

        if (listOf(nameError, emailError, passError, confirmError, termsError).any { it != null }) {
            // Feedback audible y visual
            val firstError = listOf(nameError, emailError, passError, confirmError, termsError)
                .firstOrNull { it != null } ?: "Revise el formulario"
            tts.speak(firstError)
            scope.launch { snackbarHostState.showSnackbar(firstError) }
            return
        }

        // Ejecutar registro
        isLoading = true
        focusManager.clearFocus()

        val n = name
        val e = emailNorm
        val p = password

        scope.launch {
            try {
                val result = repo.register(n, e, p)
                isLoading = false
                result.onSuccess {
                    tts.speak("Registro exitoso. Bienvenido $n")
                    scope.launch { snackbarHostState.showSnackbar("Registro exitoso") }
                    onRegistered()
                }.onFailure { ex ->
                    val msg = ex.message ?: "Error al registrar"
                    emailError = if (msg.contains("Usuario ya registrado", ignoreCase = true) ||
                        msg.contains("ya registrado", ignoreCase = true)
                    ) "Correo ya registrado" else msg
                    tts.speak(emailError ?: "Error al registrar")
                    scope.launch { snackbarHostState.showSnackbar(emailError ?: "Error al registrar") }
                }
            } catch (e: Exception) {
                isLoading = false
                val msg = e.message ?: "Error inesperado"
                tts.speak(msg)
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    TextButton(
                        onClick = { if (!isLoading) onBack() },
                        enabled = !isLoading,
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
                        enabled = !isLoading,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics { contentDescription = "Escuchar instrucciones" }
                    ) { Text("Escuchar") }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                onValueChange = { name = it; nameError = null },
                label = { Text("Nombre completo (requerido)") },
                singleLine = true,
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de nombre completo"
                        nameError?.let { stateDescription = "Error: $it" }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Correo electrónico (requerido)") },
                singleLine = true,
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de correo electrónico"
                        emailError?.let { stateDescription = "Error: $it" }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passError = null },
                label = { Text("Contraseña, mínimo 8 caracteres") },
                singleLine = true,
                isError = passError != null,
                supportingText = { passError?.let { Text(it) } },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (icon, desc) = if (passVisible)
                        Icons.Filled.VisibilityOff to "Ocultar contraseña"
                    else Icons.Filled.Visibility to "Mostrar contraseña"
                    IconButton(
                        onClick = { passVisible = !passVisible },
                        enabled = !isLoading,
                        modifier = Modifier.semantics { contentDescription = desc }
                    ) { Icon(icon, contentDescription = null) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de contraseña"
                        passError?.let { stateDescription = "Error: $it" }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmError = null },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                isError = confirmError != null,
                supportingText = { confirmError?.let { Text(it) } },
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (icon, desc) = if (confirmVisible)
                        Icons.Filled.VisibilityOff to "Ocultar confirmación"
                    else Icons.Filled.Visibility to "Mostrar confirmación"
                    IconButton(
                        onClick = { confirmVisible = !confirmVisible },
                        enabled = !isLoading,
                        modifier = Modifier.semantics { contentDescription = desc }
                    ) { Icon(icon, contentDescription = null) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { performRegister() }),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de confirmación de contraseña"
                        confirmError?.let { stateDescription = "Error: $it" }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (!isLoading) expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCountry,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("País") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    enabled = !isLoading,
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
                    onCheckedChange = { acceptTerms = it; termsError = null },
                    enabled = !isLoading,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Aceptar términos y condiciones" }
                )
                Text("Acepto los términos y condiciones")
            }
            termsError?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { stateDescription = "Error: $it" }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { performRegister() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .semantics {
                        contentDescription = if (isLoading) "Registrando, espere" else "Registrarme"
                        if (isLoading) stateDescription = "Cargando"
                    }
            ) {
                if (isLoading) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Registrarme")
            }
        }
    }
}
