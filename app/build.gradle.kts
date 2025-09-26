plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.ezycart"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ezycart"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Define flavor dimensions
    flavorDimensions += listOf("country")

    // Configure product flavors
    productFlavors {
        create("malaysia") {
            dimension = "country"
            applicationIdSuffix = ".my"
            versionNameSuffix = "-my"
            // Set default values that can be overridden in build types
            buildConfigField("String", "BASE_URL", "\"https://uat-api-retailetics-ops-mini-03.retailetics.com\"")
            buildConfigField("String", "CURRENCY_SYMBOL", "\"RM\"")
            buildConfigField("String", "ACTIVATION_CODE", "\"ALpxvmI0111\"")

        }

        create("saudi") {
            dimension = "country"
            applicationIdSuffix = ".sa"
            versionNameSuffix = "-sa"
            buildConfigField("String", "BASE_URL", "\"https://api-tamimi-ezylite-ops01.retailetics.com\"")
            buildConfigField("String", "CURRENCY_SYMBOL", "\"SAR\"")
            buildConfigField("String", "ACTIVATION_CODE", "\"ALpxvcc0022\"")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            // You can override values for debug builds if needed
            buildConfigField("String", "BASE_URL", "\"https://uat-api-retailetics-ops-mini-03.retailetics.com\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // This will create variants like: malaysiaDebug, malaysiaRelease, saudiDebug, saudiRelease
    variantFilter {
        if (name == "malaysiaDebug" || name == "saudiDebug" ||
            name == "malaysiaRelease" || name == "saudiRelease") {
            ignore = false
        } else {
            ignore = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        compose = true
        buildConfig = true // Enable build config generation
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ... your existing dependencies remain the same
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // DataStore
    implementation(libs.androidx.datastore)

    // Zxing
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

    // Permission
    implementation(libs.permissions.compose)

    // Coil
    implementation(libs.coil.compose)

    // Lottie
    implementation(libs.lottie.compose)

    //WebView
    implementation(libs.accompanist.webview)

    //ML-Kit for Barcode scanner
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    implementation(libs.mlkit.barcode)

    // Custom Toast
    implementation(libs.dynamic.toasts)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}