package com.sepulveda.minutanutricional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sepulveda.minutanutricional.accessibility.TtsHelper
import com.sepulveda.minutanutricional.data.UserPrefs
import com.sepulveda.minutanutricional.data.UsersRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    tts: TtsHelper,
    repo: UsersRepository,
    onNavigate: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Usuario") }
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }

    var userError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val roles = listOf("Usuario", "Administrador")
    val languages = listOf("Español", "Inglés", "Francés")

    val snackbarHostState = remember { SnackbarHostState() }

    fun normalize(s: String) = s.trim().lowercase()

    // función centralizada para realizar login (llamada desde botón o IME Done)
    fun performLogin() {
        // Validaciones UI
        userError = when {
            username.isBlank() -> "Ingresa tu usuario o correo."
            else -> null
        }
        passError = when {
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
            else -> null
        }
        if (userError != null || passError != null) {
            // dar feedback por voz si falla validación
            tts.speak(userError ?: passError ?: "Error de validación")
            return
        }

        // Ejecutar login
        isLoading = true
        focusManager.clearFocus()

        val emailNorm = normalize(username)
        val pass = password

        scope.launch {
            try {
                val result = repo.login(emailNorm, pass)
                isLoading = false
                result.onSuccess {
                    // Persistir preferencia de sesión (ejecutar en coroutine)
                    try {
                        UserPrefs.setLastEmail(context, emailNorm)
                        UserPrefs.setRemember(context, rememberMe)
                    } catch (e: Exception) {
                        // no bloquear si falla el guardado de prefs; informar en snackbar
                        snackbarHostState.showSnackbar("No se pudieron guardar preferencias localmente.")
                    }

                    // feedback audible y navegación
                    tts.speak("Bienvenido ${it.name ?: "usuario"}. Accediendo al menú principal.")
                    onNavigate("weekly")
                }.onFailure { ex ->
                    passError = ex.message ?: "Usuario o contraseña incorrectos."
                    tts.speak(passError ?: "Error al iniciar sesión")
                    snackbarHostState.showSnackbar(passError ?: "Error al iniciar sesión")
                }
            } catch (e: Exception) {
                isLoading = false
                val msg = e.message ?: "Error inesperado"
                passError = msg
                tts.speak(msg)
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login", modifier = Modifier.semantics { heading() }) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                onValueChange = { username = it; userError = null },
                label = { Text("Usuario o correo") },
                singleLine = true,
                isError = userError != null,
                supportingText = { userError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de usuario o correo"
                        userError?.let { stateDescription = "Error: $it" }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Contraseña con toggle de visibilidad
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passError = null },
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passError != null,
                supportingText = { passError?.let { Text(it) } },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (icon, desc) = if (passwordVisible)
                        Icons.Filled.VisibilityOff to "Ocultar contraseña"
                    else Icons.Filled.Visibility to "Mostrar contraseña"

                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !isLoading,
                        modifier = Modifier.semantics { contentDescription = desc }
                    ) { Icon(icon, contentDescription = null) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { performLogin() }),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo de contraseña"
                        passError?.let { stateDescription = "Error: $it" }
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
                    enabled = !isLoading,
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (role == selectedRole),
                            onClick = { selectedRole = role },
                            enabled = !isLoading,
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
                onExpandedChange = { if (!isLoading) expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Idioma") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    enabled = !isLoading,
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
                    onClick = { performLogin() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .semantics {
                            contentDescription = if (isLoading) "Iniciando sesión, espere" else "Botón Iniciar sesión"
                            if (isLoading) stateDescription = "Cargando"
                        }
                ) {
                    if (isLoading) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Entrar")
                }

                OutlinedButton(
                    onClick = {
                        // instrucciones de voz
                        tts.speak(
                            "Pantalla de inicio de sesión. " +
                                    "Ingresa tu usuario o correo y tu contraseña. " +
                                    "Puedes elegir tipo de usuario e idioma. " +
                                    "Pulsa Entrar para continuar."
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "Escuchar instrucciones" }
                ) { Text("Escuchar") }
            }

            TextButton(
                onClick = { onNavigate("register") },
                enabled = !isLoading,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Crear cuenta" }
            ) { Text("Crear cuenta") }

            TextButton(
                onClick = { onNavigate("forgot") },
                enabled = !isLoading,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { contentDescription = "Olvidé mi contraseña" }
            ) { Text("Olvidé mi contraseña") }
        }
    }
}
