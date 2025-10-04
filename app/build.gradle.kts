plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // plugin kapt (Room compiler)
    // puedes usar kotlin("kapt") si prefieres; dejar id() también funciona
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.sepulveda.minutanutricional"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sepulveda.minutanutricional"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // si vas a firmar desde la configuración, agrega signingConfig aquí
        }
    }

    // Usa Java/Kotlin 17 (recomendado para Compose + AGP actuales)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core / lifecycle / activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Dependencias para androidTest (instrumented tests)
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Compose UI test - si usas Compose testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // BOM Compose
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navegación
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Iconos extendidos (Visibility / VisibilityOff)
    implementation("androidx.compose.material:material-icons-extended")

    // DataStore (recordar sesión)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- ROOM (SQLite) + KAPT + Coroutines + ViewModel ---
    implementation("androidx.room:room-runtime:2.8.1")
    implementation("androidx.room:room-ktx:2.8.1")

    // annotation processor para Room (kapt)
    kapt("androidx.room:room-compiler:2.8.1")
    // asegura que kapt también procese los tests si llegas a necesitarlo
    kaptTest("androidx.room:room-compiler:2.8.1")
    kaptAndroidTest("androidx.room:room-compiler:2.8.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // (opcional) util para tests con Room (unit tests)
    testImplementation("androidx.room:room-testing:2.8.1")

    // tooling / tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit) // si prefieres usar versión del catalogo
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
