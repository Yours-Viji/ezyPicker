package com.ezycart.presentation.login

import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ezycart.R
import com.ezycart.domain.usecase.LoadingManager
import com.ezycart.presentation.ScannerViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onThemeChange: () -> Unit,
    onLanguageChange: () -> Unit,
    onLoginSuccess: () -> Unit,

) {

    val scannedCode by scannerViewModel.scannedCode.collectAsStateWithLifecycle()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 Header Row
        scannedCode?.let {code->
            Toast.makeText(context,"Scanned: $code",Toast.LENGTH_SHORT).show()
           // viewModel.onEmployeePinChange(code)
            viewModel.login(code)
            scannerViewModel.clear()
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: 3 logos
            Row(
                modifier = Modifier
                    //.weight(0.65f),
                   .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(50.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_retailetics), // replace with ad/banner
                    contentDescription = "appLogo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(width = 210.dp, height = 60.dp).padding(end = 10.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_group_arrow), // replace with ad/banner
                    contentDescription = "arrow",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(width = 140.dp, height = 40.dp).padding(end = 10.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_merchant_logo), // replace with ad/banner
                    contentDescription = "merchantLogo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(width = 240.dp, height = 60.dp).padding(end = 10.dp)
                )
            }

            // Right side: Theme + Language icons
            Row(
                modifier = Modifier.weight(0.35f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
               /* AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            setImageResource(R.drawable.toggle_button_off)
                        }
                    },
                    modifier = Modifier.size(100.dp, 40.dp).padding(end = 10.dp)
                )
                LanguageDropdown()*/

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Main Content (Split 50/50)
        Row (
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top half: Image
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shopping_cart_bg_image_normal), // replace with ad/banner
                    contentDescription = "Ad Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().padding(70.dp)
                )
            }

            // Bottom half: Center button
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(fontWeight = FontWeight.Bold,
                        text = "Sign In With Employee Code",
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Enter Your Employee Code Here",
                        fontSize = 22.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OtpPinView(
                        otpText = state.employeePin,
                        onOtpTextChange  = viewModel::onEmployeePinChange,
                        otpLength = 5
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            //viewModel.clearpref()
                            //scannerViewModel.onScanned("57024")
                            if (state.employeePin.length == 5) {
                                viewModel.login()
                            } else {
                                Toast.makeText(context  , "Please enter a valid 5-digit Employee PIN", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                    ) {
                        Text(fontWeight = FontWeight.Bold,
                            text = "Sign In",
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(fontWeight = FontWeight.Bold,
                        text = "(OR)",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(fontWeight = FontWeight.Bold,
                        text = "Scan your employee QR to Sing In",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary

                    )
                }

            }
        }
    }

}



@Composable
fun OtpPinView(
    modifier: Modifier = Modifier,
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    otpLength: Int = 5
) {
    // Make textFieldValue a mutable state so Compose can recompose correctly
    val textFieldValue = remember(otpText) {
        mutableStateOf(
            TextFieldValue(
                text = otpText,
                selection = TextRange(otpText.length)
            )
        )
    }

    Box(
        modifier = modifier.fillMaxWidth().height(64.dp)
    ) {
        BasicTextField(
            value = textFieldValue.value,
            onValueChange = { newValue ->
                if (newValue.text.length <= otpLength && newValue.text.all { it.isDigit() }) {
                    textFieldValue.value = newValue
                    onOtpTextChange(newValue.text)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent), // ✅ must use SolidColor
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // better than SpaceAround
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth() // don’t stretch full width
                ) {
                    repeat(otpLength) { index ->
                        val char = if (index < otpText.length) otpText[index] else ' '
                        OtpBox(
                            char = char,
                            isFocused = index == otpText.length
                        )
                    }
                }
            }
        )
    }
}


/**
 * A composable function for a single OTP input box.
 *
 * @param char The character to display in the box.
 * @param isFocused A boolean to indicate if this box is currently focused.
 */
@Composable
fun OtpBox(
    char: Char,
    isFocused: Boolean
) {
    val boxColor = if (isFocused) {
        Color.Transparent
    } else {
        Color.Transparent
    }

    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .background(boxColor)
            .border(2.dp, borderColor)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown() {
    var expanded = remember { mutableStateOf(false) }
    var selectedLanguage = remember { mutableStateOf("English") }

    Box(
        modifier = Modifier
            .size(200.dp, 53.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    setImageResource(R.drawable.language_toggle_background_light)
                    scaleType = ImageView.ScaleType.FIT_XY
                }
            },
            modifier = Modifier.matchParentSize()
        )

        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
            modifier = Modifier.width(200.dp)
        ) {
            OutlinedTextField(
                value = selectedLanguage.value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .height(53.dp),

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                ),

                trailingIcon = {}
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                listOf("English", "Malay").forEach { lang ->
                    DropdownMenuItem(
                        text = { Text( text = lang,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center) },
                        onClick = {
                            selectedLanguage.value = lang
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }

    }

