package com.ezycart

import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ezycart.domain.usecase.LoadingManager
import com.ezycart.presentation.ScannerViewModel
import com.ezycart.presentation.SplashViewModel
import com.ezycart.presentation.activation.ActivationScreen
import com.ezycart.presentation.common.components.BarcodeScannerListener
import com.ezycart.presentation.common.components.CustomRationaleDialog
import com.ezycart.presentation.common.components.GlobalLoadingOverlay
import com.ezycart.presentation.home.HomeScreen
import com.ezycart.presentation.login.LoginScreen
import com.meticha.permissions_compose.PermissionManagerConfig
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loadingManager: LoadingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = getColor(R.color.colorPrimaryDark)

        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = false

        PermissionManagerConfig.setCustomRationaleUI { permission, onDismiss, onConfirm ->
            CustomRationaleDialog(
                description = permission.description,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }

        //enableEdgeToEdge()
        setContent {

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val splashViewModel: SplashViewModel = hiltViewModel()
                    val scannerViewModel: ScannerViewModel = hiltViewModel()
                    val isActivated = splashViewModel.isDeviceActivated.collectAsState()
                    splashViewModel.getDeviceId(this)

                    BarcodeScannerListener(
                        onBarcodeScanned = { code ->
                            scannerViewModel.onScanned(code)
                        }
                    )

                    when (isActivated.value) {
                        null -> {
                            // Optional loading UI
                            Text("Checking device activation...")
                        }

                        else -> {
                            val startDestination =
                                if (isActivated.value == true) "login" else "activation"

                            NavHost(navController, startDestination = startDestination) {
                                composable("activation") {
                                    ActivationScreen(
                                        onLoginSuccess = {
                                            navController.navigate("login") {
                                                popUpTo("activation") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("login") {
                                    LoginScreen(

                                        onThemeChange = {
                                            // 🔹 Handle theme change
                                            //  Toast.makeText(this, "Theme change clicked", Toast.LENGTH_SHORT).show()
                                        },
                                        onLanguageChange = {
                                            // 🔹 Handle language change
                                            // Toast.makeText(this, "Language change clicked", Toast.LENGTH_SHORT).show()
                                        },
                                        onLoginSuccess = {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("home") {
                                    HomeScreen(
                                        onThemeChange = {
                                            // 🔹 Handle theme change
                                            //  Toast.makeText(this, "Theme change clicked", Toast.LENGTH_SHORT).show()
                                        },
                                        onLanguageChange = {
                                            // 🔹 Handle language change
                                            // Toast.makeText(this, "Language change clicked", Toast.LENGTH_SHORT).show()
                                        },
                                        /*onLoginSuccess = {
                                            navController.navigate("home") {
                                                popUpTo("activation") { inclusive = true }
                                            }
                                        }*/
                                    )
                                }
                            }

                        }
                    }
                    GlobalLoadingOverlay(loadingManager)
                }
            }
        }
    }

}



