package com.ezycart.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ezycart.R
import com.ezycart.data.remote.dto.CartItem
import com.ezycart.data.remote.dto.ShoppingCartDetails
import com.ezycart.domain.model.AppMode
import com.ezycart.presentation.ScannerViewModel
import com.ezycart.presentation.alertview.QrPaymentAlertView
import com.ezycart.presentation.common.components.BarcodeScannerListener
import com.ezycart.presentation.common.data.Constants
import com.google.accompanist.web.rememberWebViewState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),

    onThemeChange: () -> Unit,
    onLanguageChange: () -> Unit,
    onLogout: () -> Unit,
    onTransactionCalled: () -> Unit,
) {

    val showDialog = remember { mutableStateOf(false) }
    val cartCount = viewModel.cartCount.collectAsState()
    val employeeName = viewModel.employeeName.collectAsState()
    val priceInfo by viewModel.priceDetails.collectAsState()
    val productInfo by viewModel.productInfo.collectAsState()
    val shoppingCartInfo = viewModel.shoppingCartInfo.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var canShowPriceChecker = remember { mutableStateOf(true) }
    val appMode by viewModel.appMode.collectAsState()
    var scanBuffer = remember { mutableStateOf("") }
    // Correct way to declare the state
    var showQrDialog = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(priceInfo) {
        if (priceInfo != null) {
            showDialog.value = true
        }
    }
    if (showQrDialog.value) {
        shoppingCartInfo.value.let {
            val finalAmount= it?.finalAmount ?: 0.0
            QrPaymentAlert(
                amount = "$finalAmount",
                qrPainter = painterResource(id = R.drawable.baseline_qr_code_2_24), // replace with your QR
                onDismiss = { showQrDialog.value = false }
            )
        }
    }
    if (showDialog.value) {
        if (canShowPriceChecker.value){
            ProductPriceAlert(viewModel = viewModel) {
                showDialog.value = false
            }
        }else{
            productInfo?.let { viewModel.addProductToShoppingCart(it.barcode, 1) }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.initNewShopping()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusTarget()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    when (keyEvent.key) {
                        androidx.compose.ui.input.key.Key.Enter -> {
                            if (scanBuffer.value.isNotBlank()) {
                                viewModel.resetProductInfoDetails()
                                viewModel.getProductDetails(scanBuffer.value)
                                scanBuffer.value = ""
                            }
                            true
                        }
                        else -> {
                            val c = keyEvent.utf16CodePoint.toChar()
                            if (c.isLetterOrDigit()) {
                                scanBuffer.value += c
                            }
                            false
                        }
                    }
                } else false
            }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    appMode = appMode,
                    onTransActionSelected = {
                        scope.launch {
                            scope.launch { drawerState.close() }
                            onTransactionCalled()

                        }
                    },
                    onAppModeUpdated = { appMode ->
                        scope.launch { drawerState.close() }
                        viewModel.onAppModeChange(appMode)
                    },
                    isChecked = canShowPriceChecker.value,
                    onCheckedChange = {
                        scope.launch { drawerState.close() }
                        canShowPriceChecker.value = it
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    MyTopAppBar(
                        employeeName = employeeName.value,
                        cartCount = cartCount.value,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onFirstIconClick = { /* notifications */ },
                        onLogout = onLogout
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    val localView = LocalView.current
                   /* Button(
                        onClick = {
                            // Mock scanner input
                            scanBuffer.value = "6936489101973"

                            // Request focus and dispatch with delay
                            focusRequester.requestFocus()

                            // Use coroutine to ensure proper timing
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(50) // Small delay to ensure focus is acquired

                                val mockKeyEvent = android.view.KeyEvent(
                                    android.view.KeyEvent.ACTION_DOWN, // Try ACTION_DOWN first
                                    android.view.KeyEvent.KEYCODE_ENTER
                                )
                                localView.dispatchKeyEvent(mockKeyEvent)

                                // Also send ACTION_UP
                                val upEvent = android.view.KeyEvent(
                                    android.view.KeyEvent.ACTION_UP,
                                    android.view.KeyEvent.KEYCODE_ENTER
                                )
                                localView.dispatchKeyEvent(upEvent)
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Mock Enter Key")
                    }*/
                    PickersShoppingScreen(viewModel, onQrPaymentClick = {
                        showQrDialog.value = true
                    })
                }
            }
        }
    }
}


@Composable
fun EmptyCartScreen(
    isPickerModel: Boolean,
    onScanBarcode: () -> Unit,
    onEnterBarcodeManually: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_trolley_2),
            contentDescription = "appLogo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 280.dp, height = 280.dp)
                .graphicsLayer(
                    scaleX = -1f
                )
        )
        Text(
            text = "Cart is Empty",
            fontSize = 33.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Scan a product barcode to begin shopping",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        ManualBarcodeEntryButton(
            onEnterBarcodeManually, modifier = Modifier
                .width(230.dp)
                .height(48.dp), 20f
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (!isPickerModel){
            ScannerButton(
                onScanBarcode, Modifier
                    .width(230.dp)
                    .height(48.dp), 20f
            )
        }

    }
}

@Composable
fun PickersShoppingScreen(viewModel: HomeViewModel,onQrPaymentClick: () -> Unit) {
    val showScanner = remember { mutableStateOf(false) }
    val showManualBarCode = remember { mutableStateOf(false) }
    val scannedCode = remember { mutableStateOf<String?>(null) }
    val cartDataList = viewModel.cartDataList.collectAsState()
    val shoppingCartInfo = viewModel.shoppingCartInfo.collectAsState()
    val isPickerModel = viewModel.isPickerModel.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // Left 23%
        if (isPickerModel.value) {
            Box(
                modifier = Modifier
                    .weight(0.23f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp), // rounded corners
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White // set background color properly
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                    ) {


                        var selected = remember { mutableStateOf("Arivu-ID1234-05-Sep") }

                        RoundedDropdownSpinner(
                            items = listOf(
                                "Arivu-ID1234-05-Sep",
                                "Nava-ID1234-05-Sep",
                                "Azizi-ID1234-05-Sep"
                            ),
                            selectedItem = selected.value,
                            onItemSelected = { selected.value = it }
                        )

                        val groceries = listOf(
                            "Milk",
                            "Bread",
                            "Eggs",
                            "Rice",
                            "COCON YOGO",
                            "Apples",
                            "Tomatoes",
                            "Cheese",
                            "Chicken"
                        )

                        ProductPickList(items = groceries,cartItems = cartDataList.value)
                    }
                }
            }
        }


        // Center 49%
        Box(
            modifier = Modifier
                .weight(if (isPickerModel.value) 0.49f else 0.65f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            if (cartDataList.value.isEmpty()) {
                EmptyCartScreen(
                    isPickerModel.value,
                    //scannerViewModel,
                    onScanBarcode = { showScanner.value = true },
                    onEnterBarcodeManually = {showManualBarCode.value = true})
            } else {
                CartScreen(
                    isPickerModel.value,
                    cartItems = cartDataList.value,
                    onScanBarcode = { showScanner.value = true },
                    onEnterBarcodeManually = {showManualBarCode.value = true},
                    onClearCart = { viewModel.initNewShopping() },
                    onRemoveItem = {cartItem ->
                        viewModel.deleteProductFromShoppingCart(cartItem.barcode,cartItem.id)
                    },
                    onEditProduct = {barCode, id, quantity ->
                        viewModel.editProductInShoppingCart(barCode = barCode, quantity = quantity, id = id)
                    }
                )
            }
        }

        // Right 28%
        if (cartDataList.value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .weight(if (isPickerModel.value) 0.28f else 0.35f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp), // rounded corners
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White // set background color properly
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CheckoutSummaryScreen(shoppingCartInfo,onQrPaymentClick={onQrPaymentClick()})
                }
            }
        }
    }

    if (showScanner.value) {
        BarcodeScannerDialog(
            onDismiss = { showScanner.value = false },
            onBarcodeScanned = {
                scannedCode.value = it
                viewModel.resetProductInfoDetails()
                viewModel.getProductDetails(scannedCode.value.toString())
               // scannerViewModel.onScanned(scannedCode.value.toString())
                showScanner.value = false
            }
        )
    }
    if (showManualBarCode.value) {
        ManualBarcodeEntryDialog(
            onProceed = { barcode ->
                viewModel.resetProductInfoDetails()
                viewModel.getProductDetails(barcode)
               // scannerViewModel.onScanned(barcode)
                showManualBarCode.value=false
            },
            onCancel = {
                showManualBarCode.value=false
                println("Cancel clicked")
            },
            onDismiss = {
                showManualBarCode.value=false
            }
        )
    }
}

@Composable
fun CheckoutSummaryScreen(shoppingCartInfo: State<ShoppingCartDetails?>,onQrPaymentClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val paymentSummary = shoppingCartInfo.value
        Column {
            BillRow("Sub Total", "${Constants.currencySymbol} ${paymentSummary?.totalPrice ?: 0.0}", color = Color.Black)
            BillRow(
                "Promo Discount",
                "${Constants.currencySymbol} ${paymentSummary?.promotionSave ?: 0.0}",
                color = Color.Black
            )
            BillRow(
                "Special Discount",
                "${Constants.currencySymbol} ${paymentSummary?.totalDiscount ?: 0.0}",
                color = Color.Black
            )
            BillRow(
                "Coupon Discount",
                "${Constants.currencySymbol} ${paymentSummary?.couponAmount ?: 0.0}",
                color = Color.Black
            )
            BillRow(
                "Voucher Discount",
                "${Constants.currencySymbol} ${paymentSummary?.vourcherAmount ?: 0.0}",
                color = Color.Black
            )
            BillRow("Tax", "${Constants.currencySymbol} ${paymentSummary?.totalTax ?: 0.0}", color = Color.Black)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Spacer(modifier = Modifier.height(5.dp))
            BillRow(
                "Grand Total",
                "${Constants.currencySymbol} ${paymentSummary?.finalAmount ?: 0.0}",
                isBold = true,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Apply Coupon / Voucher Button
        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.colorGreen),      // background color
                contentColor = Color.White,              // default for text & icons
                disabledContainerColor = Color.Gray,     // background when disabled
                disabledContentColor = Color.LightGray   // text/icon when disabled
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_coupon_white),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Apply Coupon / Voucher",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            )
        }

        // Take up remaining space → pushes "Total Payable" to bottom
        Spacer(modifier = Modifier.weight(1f))

        // Total Payable
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedPayableText()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${Constants.currencySymbol} ${paymentSummary?.finalAmount ?: 0.0}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_contactless_24),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Credit / Debit Tap To Pay",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    onQrPaymentClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.colorAccent),      // background color
                    contentColor = Color.White,              // default for text & icons
                    disabledContainerColor = Color.Gray,     // background when disabled
                    disabledContentColor = Color.LightGray   // text/icon when disabled
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_qr_code_24),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR Payment",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedPayableText() {
    val infiniteTransition = rememberInfiniteTransition(label = "payableAnim")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f, // slightly bigger
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    Text(
        text = "Total Payable",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
    )
}

@Composable
fun ManualBarcodeEntryButton(
    onEnterBarcodeManually: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: Float
) {
    Button(
        onClick = onEnterBarcodeManually,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.colorGreen))
    ) {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Enter Barcode", style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = fontSize.sp
            )
        )
    }
}

@Composable
fun ScannerButton(onScanBarcode: () -> Unit, modifier: Modifier = Modifier, fontSize: Float) {
    Button(
        onClick = onScanBarcode,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.colorPrimary))
    ) {
        Icon(
            modifier = Modifier.size(33.dp),
            painter = painterResource(id = R.drawable.ic_scan),
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Scan Barcode",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = fontSize.sp
            )
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    isPickerModel: Boolean,
    cartItems: List<CartItem>,
    onScanBarcode: () -> Unit,
    onEnterBarcodeManually: () -> Unit,
    onClearCart: () -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    modifier: Modifier = Modifier,
    onEditProduct:(String,Int,Int) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {

            Row(

                modifier = Modifier
                    .background(color = Color.Transparent)
                    .fillMaxWidth()
                    .padding(12.dp),


                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onClearCart,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.colorRed))
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Clear Cart", style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
                ManualBarcodeEntryButton(
                    onEnterBarcodeManually, modifier = Modifier
                        .weight(1f)
                        .height(48.dp), 15f
                )
                if (!isPickerModel){
                    ScannerButton(
                        onScanBarcode, Modifier
                            .weight(1f)
                            .height(48.dp), 15f
                    )
                }
            }
        }
    ) { innerPadding ->
        // content: cart list
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 92.dp) // leave room for bottom bar
            ) {
                itemsIndexed(cartItems) { index, productData ->
                    CartItemCard(
                        productInfo = productData,
                        onRemove = { onRemoveItem(it) },
                        onEditProduct = { barCode, id, updatedQuantity->
                            onEditProduct(barCode, id, updatedQuantity)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    productInfo: CartItem,
    onRemove: (CartItem) -> Unit,
    modifier: Modifier = Modifier,
    onEditProduct: (String,Int,Int) -> Unit,
) {
    var showDeleteDialog = remember { mutableStateOf(false) }
    var showEditDialog = remember { mutableStateOf(false) }
    var selectedCartItem = remember { mutableStateOf<CartItem?>(null) }

    if (showDeleteDialog.value && selectedCartItem.value!=null) {
        DeleteProductDialog(
            productName = selectedCartItem.value?.productName ?: "",
            productCode = selectedCartItem.value?.barcode ?: "",
            oldPrice = "${Constants.currencySymbol} ${selectedCartItem.value?.originalPrice ?: 0.0}",
            newPrice = "${Constants.currencySymbol} ${selectedCartItem.value?.finalPrice ?: 0.0}",
            imageRes = selectedCartItem.value?.imageUrl ?: "",
            onRemove = {
                // handle remove
                onRemove(selectedCartItem.value!!)
                showDeleteDialog.value = false
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }
    if (showEditDialog.value && selectedCartItem.value!=null) {
        EditProductDialog (
            productName = selectedCartItem.value?.productName ?: "",
            productCode = selectedCartItem.value?.barcode ?: "",
            oldPrice = "${Constants.currencySymbol} ${selectedCartItem.value?.originalPrice ?: 0.0}",
            newPrice = "${Constants.currencySymbol} ${selectedCartItem.value?.finalPrice ?: 0.0}",
            imageRes = selectedCartItem.value?.imageUrl ?: "",
            currentQuantity = selectedCartItem.value?.quantity ?: 1,
            onEdit = {updatedQuantity->
                if (selectedCartItem.value?.quantity != updatedQuantity){
                    selectedCartItem.value?.let { onEditProduct(it.barcode,it.id,updatedQuantity) }
                }
                showEditDialog.value = false
            },
            onDismiss = { showEditDialog.value = false }
        )
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                if (productInfo.imageUrl != null) {
                    AsyncImage(
                        model = productInfo.imageUrl,
                        contentDescription = "${productInfo.id}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.ic_no_product_image),
                        error = painterResource(R.drawable.ic_no_product_image)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_no_product_image),
                        contentDescription = "${productInfo.id}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product name
            Column(
                modifier = Modifier.weight(1f) // take remaining space before price + icons
            ) {
                Text(
                    text = productInfo.productName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_discount_icon),
                        contentDescription = "discount",
                        tint = colorResource(R.color.colorOrange),
                        modifier = Modifier
                            .size(45.dp)
                            .padding(start = 4.dp, end = 10.dp)
                    )

                    Text(
                        text = "Buy One Get One",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.colorOrange)
                        )
                    )
                }
            }

            // Price (column to align nicely)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 8.dp)
            ) {

                Text(
                    text = "${productInfo.displayQty} x ${Constants.currencySymbol} ${"%.2f".format(productInfo.unitPrice)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Gray
                )
                if (productInfo.finalPriceBeforeDiscount != productInfo.finalPrice) {
                    Text(
                        text = "${Constants.currencySymbol} ${"%.2f".format(productInfo.finalPriceBeforeDiscount)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        textDecoration = TextDecoration.LineThrough,

                        )
                }

                Text(
                    text = "${Constants.currencySymbol} ${"%.2f".format(productInfo.finalPrice)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.colorPrimary)
                    )
                )
            }
            Spacer(Modifier.width(5.dp))
            // Delete icon
            Icon(
                painter = painterResource(id = R.drawable.ic_box_delete),
                contentDescription = "Delete ${productInfo.id}",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(45.dp)
                    .padding(start = 4.dp)
                    .clickable {
                        selectedCartItem.value = productInfo
                        showDeleteDialog.value = true
                    }
            )
            Spacer(Modifier.width(5.dp))
            // Edit icon
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_box),
                contentDescription = "Edit ${productInfo.id}",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(45.dp)
                    .padding(start = 4.dp)
                    .clickable {
                        selectedCartItem.value = productInfo
                        showEditDialog.value = true
                    }
            )


        }
    }
}


@Composable
fun BillRow(label: String, value: String, isBold: Boolean = false, color: Color = Color.Gray) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = color,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp
            )
            else MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = value,
            color = color,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp
            )
            else MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    employeeName:String="",
    cartCount: Int = 0,
    onMenuClick: () -> Unit,
    onFirstIconClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                text = if (employeeName.isEmpty()) {
                    "Hi, WELCOME"
                } else {
                    "WELCOME, $employeeName"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CartIconWithBadge(count = cartCount)
                Spacer(Modifier.width(20.dp))
                IconButton(
                    onClick = {
                        onLogout()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_logout_24),
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.width(50.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun CartIconWithBadge(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_cart), // your vector drawable
            contentDescription = "Cart",
            tint = Color.White,
            modifier = Modifier.fillMaxSize()
        )

        //if (count > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (4).dp, y = (-4).dp)
                    .size(if (count > 99) 26.dp else 20.dp)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 10.sp
                )
            }
        //}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedDropdownSpinner(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "Select Customer's Pick List",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

private fun normalize(s: String): String {
    return s.lowercase(Locale.getDefault())
        .replace(Regex("[^a-z0-9\\s]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

@Composable
fun ProductPickList(
    items: List<String>,
    cartItems: List<CartItem> // pass from ViewModel
) {
    // Precompute normalized cart product names once per cartItems change
    val normalizedCart = remember(cartItems) {
        cartItems.map { normalize(it.productName) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, name ->
            val normName = normalize(name)
            val nameTokens = normName.split(" ").filter { it.isNotBlank() }

            // isSelected -> true if any cart item matches the checklist item
            val isSelected = normalizedCart.any { cart ->
                // 1) whole-phrase word-boundary match: "green apple" in "green apple xyz"
                val phraseMatch = Regex("\\b${Regex.escape(normName)}\\b").containsMatchIn(cart)

                // 2) any token matches as whole word OR token-prefix (covers plural/suffix)
                val tokenMatch = nameTokens.any { token ->
                    val wholeWord = Regex("\\b${Regex.escape(token)}\\b").containsMatchIn(cart)
                    val prefix = cart.split(" ").any { cartToken ->
                        cartToken.startsWith(token)
                    }
                    wholeWord || prefix
                }

                phraseMatch || tokenMatch
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Count
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(0.2f)
                )

                // Name with strikethrough when matched
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        textDecoration = if (isSelected) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (isSelected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary // use your green
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Read-only checkbox that reflects cart membership
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {}, // no-op (keeps it controlled by cartDataList)
                    enabled = true, // ✅ keep it enabled so green shows
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF2E7D32), // your green
                        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        checkmarkColor = Color.White
                    )
                )
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var previewView = remember { mutableStateOf<PreviewView?>(null) }

    Dialog(onDismissRequest = {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(context))
        onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE3F2FD),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                // Preview container: clipped rounded corners + fade overlays
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PreviewView(ctx).also { pv ->
                                // keep your reference assignment intact
                                previewView.value = pv

                                val cameraProviderFuture =
                                    ProcessCameraProvider.getInstance(ctx)

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(pv.surfaceProvider)
                                    }

                                    val scanner = BarcodeScanning.getClient()
                                    val analysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also {
                                            it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                                val mediaImage = imageProxy.image
                                                if (mediaImage != null) {
                                                    val image = InputImage.fromMediaImage(
                                                        mediaImage,
                                                        imageProxy.imageInfo.rotationDegrees
                                                    )
                                                    scanner.process(image)
                                                        .addOnSuccessListener { barcodes ->
                                                            barcodes.firstOrNull()?.rawValue?.let { code ->
                                                                onBarcodeScanned(code)
                                                            }
                                                        }
                                                        .addOnCompleteListener {
                                                            imageProxy.close()
                                                        }
                                                } else {
                                                    imageProxy.close()
                                                }
                                            }
                                        }

                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        analysis
                                    )
                                }, ContextCompat.getMainExecutor(ctx))
                            }
                        }
                    )

                    // subtle top fade
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.52f), Color.Transparent)
                                )
                            )
                    )

                    // subtle bottom fade
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.52f))
                                )
                            )
                    )

                    // optional center guideline (thin translucent line)
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(0.8f)
                            .align(Alignment.Center)
                            .background(Color.White.copy(alpha = 0.24f))
                    )
                }

                // add a bit more spacing so the button sits clearly below preview
                Spacer(modifier = Modifier.height(20.dp))

                // Close button (keeps same release logic)
                Button(
                    onClick = {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProvider.unbindAll()
                        }, ContextCompat.getMainExecutor(context))
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Close Scanner", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun ProductPriceAlert(
    viewModel: HomeViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val priceInfo by viewModel.priceDetails.collectAsState()
    val productInfo by viewModel.productInfo.collectAsState()
    var isAdding = remember { mutableStateOf(false) }
    if (productInfo != null && priceInfo != null) {
        var lastClickTime = 0L
        val clickDebounceTime = 300L
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
            },
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Product Content in Row (left image, right info)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product Image (left side)
                        AsyncImage(
                            model = productInfo!!.imageUrl,
                            contentDescription = productInfo!!.productName,
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            placeholder = painterResource(R.drawable.ic_no_product_image),
                            error = painterResource(R.drawable.ic_no_product_image),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.width(16.dp))

                        // Product Details (right side)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = productInfo!!.productName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "SKU: ${productInfo!!.barcode}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(16.dp))

                            if (priceInfo!!.price != priceInfo!!.originalPrice) {
                                Text(
                                    text = "RM %.2f".format(priceInfo!!.originalPrice ?: 0.0),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp,
                                        textDecoration = TextDecoration.LineThrough,
                                    ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Text(
                                text = "RM %.2f".format(priceInfo!!.price ?: 0.0),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Buttons Row at the bottom - same style, different colors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Cancel Button (Red background)
                        Button(
                            onClick = { onDismiss() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                ),
                            )
                        }

                        // Add To Cart Button (Green background)
                        Button(
                            onClick = {
                                if (!isAdding.value) {
                                    isAdding.value = true
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime > clickDebounceTime) {
                                        lastClickTime = currentTime
                                        viewModel.addProductToShoppingCart(productInfo!!.barcode, 1)
                                        onDismiss()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Add To Cart",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                )
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            containerColor = Color.White
        )
    }
}

@Composable
fun DeleteProductDialog(
    productName: String,
    productCode: String,
    oldPrice: String,
    newPrice: String,
     imageRes: String,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.background(Color.White)) {

                // Top banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.colorPrimary))
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cart),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Are you sure want to delete this product?",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image + Sale badge
                    Box(modifier = Modifier.size(130.dp)) {
                        if (imageRes.isNotEmpty()) {
                            AsyncImage(
                                model = imageRes,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(R.drawable.ic_no_product_image),
                                error = painterResource(R.drawable.ic_no_product_image)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_no_product_image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Product info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = productName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color  = colorResource(R.color.colorPrimary))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "SKU: $productCode",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = oldPrice,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = newPrice,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.colorPrimary)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action buttons (Cancel + Remove)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // spacing between buttons
                ) {
                    // Cancel button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = colorResource(R.color.colorPrimary)
                        ),
                        border = BorderStroke(2.dp, colorResource(R.color.colorPrimary))
                    ) {
                        Text(
                            text = "CANCEL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold, color = colorResource(R.color.colorPrimary)
                            )
                        )
                    }

                    // Remove button
                    Button(
                        onClick = onRemove,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.colorRed)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "DELETE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun EditProductDialog(
    productName: String,
    productCode: String,
    oldPrice: String,
    newPrice: String,
    imageRes: String,
    currentQuantity: Int,
    onEdit: (Int) -> Unit, // pass selected quantity
    onDismiss: () -> Unit
) {
    var quantity = remember { mutableStateOf(currentQuantity) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.background(Color.White)) {

                // Top banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.colorPrimary))
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cart),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Edit Product",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image
                    Box(modifier = Modifier.size(130.dp)) {
                        if (imageRes.isNotEmpty()) {
                            AsyncImage(
                                model = imageRes,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(R.drawable.ic_no_product_image),
                                error = painterResource(R.drawable.ic_no_product_image)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_no_product_image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Product info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = productName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.colorPrimary)
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "SKU: $productCode",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = oldPrice,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = newPrice,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.colorPrimary)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { if (quantity.value > 1) quantity.value-- },
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorResource(R.color.colorRed), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_remove_24),
                            contentDescription = "Decrease",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )

                    }

                    Text(
                        text = quantity.value.toString(),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.colorPrimary)
                        )
                    )

                    IconButton(
                        onClick = { quantity.value++ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorResource(R.color.colorPrimary), CircleShape)
                    ) {
                        Icon(
                            imageVector =  Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )

                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = colorResource(R.color.colorPrimary)
                        ),
                        border = BorderStroke(2.dp, colorResource(R.color.colorPrimary))
                    ) {
                        Text(
                            text = "CANCEL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Button(
                        onClick = { onEdit(quantity.value) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.colorGreen)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "UPDATE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
@Composable
fun ManualBarcodeEntryDialog(
    onProceed: (String) -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    var barcode = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.95f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Enter bar code manually",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0D47A1),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // TextField
                TextField(
                    value = barcode.value,
                    onValueChange = { barcode.value = it },
                    placeholder = { Text("Enter barcode") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF2F2F2),
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        disabledContainerColor = Color(0xFFF2F2F2),
                        errorContainerColor = Color(0xFFF2F2F2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                        contentColor = colorResource(R.color.colorRed)
                    ),
                    border = BorderStroke(2.dp, colorResource(R.color.colorRed))
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Proceed Button
                    Button(
                        onClick = { onProceed(barcode.value) },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0D47A1)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Proceed",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    appMode: AppMode,
    onTransActionSelected: () -> Unit,
    onAppModeUpdated: (AppMode) -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(350.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(50.dp))
       /* Image(
            painter = painterResource(id = R.drawable.ic_merchant_logo),
            contentDescription = "Ad Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(70.dp)
        )

        Divider()*/

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTransActionSelected() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.transaction),
                contentDescription = "transaction",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "View Transaction",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
            text = "App Mode",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        SingleSelectCheckboxes(
            onSelectionChanged = onAppModeUpdated,
            selected = appMode
        )
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
           /* Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )*/
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Show Price Checker",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f) // 👈 pushes switch to the right
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }

    }
}
@Composable
fun SingleSelectCheckboxes(
    options: List<Pair<AppMode, String>> = listOf(
        AppMode.EzyCartPicker to "EzyCartPicker",
        AppMode.EzyLite to "EzyLite"
    ),
    selected: AppMode = AppMode.EzyCartPicker,
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


@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(url: String, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Report") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(url)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }

    // Handle back press
    BackPressHandler {
        navController.popBackStack()
    }
}
@Composable
fun BackPressHandler(onBackPressed: () -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)

    DisposableEffect(backPressedDispatcher) {
        val callback = object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }

        backPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }
}

@Composable
fun QrPaymentAlert(
    amount: String,
    qrPainter: Painter, // QR code image painter
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Top-right close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center), // keep column centered
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.id_duit_now_logo),
                    contentDescription = "Ad Banner",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                // QR Code Image
                Image(
                    painter = qrPainter,
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Amount
                Text(
                    text = "RM $amount",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
