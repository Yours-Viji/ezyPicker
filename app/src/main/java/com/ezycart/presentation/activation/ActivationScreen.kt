package com.ezycart.presentation.activation

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ezycart.R
import com.ezycart.domain.model.AppMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import com.pranavpandey.android.dynamic.toasts.DynamicToast


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ActivationScreen(
    viewModel: ActivationViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val permissions = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.CAMERA,
                description = "Camera access is needed to take photos. Please grant this permission.",
                isRequired = true
            ),
        )
    )
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isActivated = viewModel.isDeviceActivated.collectAsState()


    LaunchedEffect(cameraPermissionState.status) {
        when {
            !cameraPermissionState.status.isGranted ->{
                cameraPermissionState.launchPermissionRequest()
            }

        }
    }
    LaunchedEffect(isActivated.value) {
        if (isActivated.value == false) {
            viewModel.getDeviceInfo()
        }
    }

    LaunchedEffect(state.isActivationSuccessful) {
        if (state.isActivationSuccessful) {
            onLoginSuccess()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 350.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ezycart_lite_icon),
                contentDescription = "EzyCart Logo",
                modifier = Modifier.size(width = 250.dp, height = 80.dp)
            )
            /*Text(
                text = "By",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Image(
                painter = painterResource(id = R.drawable.ic_retailetics),
                contentDescription = "EzyCart Logo",
                modifier = Modifier.size(width = 250.dp, height = 40.dp)
            )*/

            Spacer(modifier = Modifier.height(20.dp))

          //  OutletSelectionDropdown()

           // Spacer(modifier = Modifier.height(10.dp))

          /*  OutlinedTextField(
                value = state.trolleyNumber,
                onValueChange = viewModel::onTrolleyNumberChange,
                label = { Text("Trolley Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )*/

          //  Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.activationCode,
                onValueChange = viewModel::onActivationCodeChange,
                label = { Text("Activation Code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
           /* Spacer(modifier = Modifier.height(10.dp))
            SingleSelectCheckboxes(
                onSelectionChanged = viewModel::onAppModeChange,
                selected = state.appMode
            )*/
            Spacer(modifier = Modifier.height(20.dp))
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                // Login button
                Button(
                    onClick = {
                        if (state.activationCode.isEmpty()){
                            DynamicToast.makeError(context, "Please enter a valid Activation Code").show();
                        }
                       /* else if (state.trolleyNumber.isEmpty()){
                            DynamicToast.makeError(context, "Please enter a valid Trolley Number").show();
                        }*/else{
                            viewModel.activateDevice()
                            permissions.requestPermission()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Activate", fontSize = 18.sp)
                }

            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }


        }
        // "Powered By" section (aligned to bottom-end)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Product Of",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Image(
                painter = painterResource(id = R.drawable.ic_retailetics),
                contentDescription = "Retailetics Logo",
                modifier = Modifier.size(width = 150.dp, height = 40.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutletSelectionDropdown() {
    val outlets = listOf("1 Mont' Kiara", "Bangsar Village", "Sunway Giza", "Sierra Fresco")
    var expanded = remember { mutableStateOf(false) }
    var selectedOutlet = remember { mutableStateOf("Select Outlet") }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedOutlet.value,
            onValueChange = {},
            label = { Text("Select Outlet") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            shape = RoundedCornerShape(8.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            outlets.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOutlet.value = selectionOption
                        expanded.value = false
                    }
                )
            }
        }
    }
}
@Composable
fun SingleSelectCheckboxes(
    options: List<Pair<AppMode, String>> = listOf(
        AppMode.EzyCartPicker to "EzyCartPicker",
        AppMode.EzyLite to "EzyLite"
    ),
    selected:AppMode = AppMode.EzyCartPicker,
    modifier: Modifier = Modifier,
    onSelectionChanged: (AppMode) -> Unit = {}
) {
    var selected = remember { mutableStateOf(selected) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (mode, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        selected.value = mode
                        onSelectionChanged(mode)
                    }
                    .padding(4.dp)
            ) {
                Checkbox(
                    checked = selected.value == mode,
                    onCheckedChange = {
                        selected.value = mode
                        onSelectionChanged(mode)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}



