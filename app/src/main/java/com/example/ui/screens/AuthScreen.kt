package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassColors
import com.example.ui.theme.appleLiquidGlass
import com.example.ui.viewmodel.PocketLibViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: PocketLibViewModel,
    onAuthSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    // Redirect to home if user was active
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onAuthSuccess()
        }
    }

    // Capture changes in viewModel's authState
    LaunchedEffect(authState) {
        when (authState) {
            "SUCCESS_LOGIN" -> {
                statusMessage = "Selamat datang, $username!"
                onAuthSuccess()
            }
            "SUCCESS_REGISTER" -> {
                statusMessage = "Pendaftaran berhasil! Silakan masuk."
                isLoginMode = true
                password = ""
                viewModel.clearAuthState()
            }
            "LOADING" -> {
                statusMessage = "Memproses..."
            }
            null -> {
                statusMessage = ""
            }
            else -> {
                // If it is any other string, it represents an error message
                statusMessage = authState ?: ""
            }
        }
    }

    // Shifting Liquid blobs
    val offset1X = remember { Animatable(0f) }
    val offset1Y = remember { Animatable(0f) }
    val offset2X = remember { Animatable(0f) }
    val offset2Y = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            offset1X.animateTo(
                targetValue = 120f,
                animationSpec = infiniteRepeatable(
                    animation = tween(5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset1Y.animateTo(
                targetValue = -150f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset2X.animateTo(
                targetValue = -140f,
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset2Y.animateTo(
                targetValue = 160f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.SunsetGlassGradient)
    ) {
        // Decorative moving background gradient blobs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = GlassColors.GlowViolet.copy(alpha = 0.4f),
                radius = 260.dp.toPx(),
                center = Offset(
                    x = size.width * 0.8f + offset1X.value.dp.toPx(),
                    y = size.height * 0.2f + offset1Y.value.dp.toPx()
                )
            )
            drawCircle(
                color = GlassColors.GlassPink.copy(alpha = 0.35f),
                radius = 290.dp.toPx(),
                center = Offset(
                    x = size.width * 0.15f + offset2X.value.dp.toPx(),
                    y = size.height * 0.75f + offset2Y.value.dp.toPx()
                )
            )
        }

        // Concentrated authentication card panel
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .appleLiquidGlass(
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 12.dp
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen header
            Text(
                text = if (isLoginMode) "Masuk" else "Daftar Akun",
                color = GlassColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = if (isLoginMode) "Lacak dan catat progres membacamu" else "Mulai petualangan membacamu hari ini",
                color = GlassColors.TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            // Input Fields
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (statusMessage.isNotEmpty()) viewModel.clearAuthState()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_input"),
                label = { Text("Username", color = GlassColors.TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username Icon",
                        tint = GlassColors.TextSecondary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GlassColors.TextPrimary,
                    unfocusedTextColor = GlassColors.TextPrimary,
                    focusedBorderColor = GlassColors.TextAccent,
                    unfocusedBorderColor = GlassColors.GlassBorderLight,
                    cursorColor = GlassColors.TextAccent,
                    focusedContainerColor = Color(0x11FFFFFF),
                    unfocusedContainerColor = Color(0x08FFFFFF)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (statusMessage.isNotEmpty()) viewModel.clearAuthState()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                label = { Text("Password", color = GlassColors.TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = GlassColors.TextSecondary
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = GlassColors.TextSecondary
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GlassColors.TextPrimary,
                    unfocusedTextColor = GlassColors.TextPrimary,
                    focusedBorderColor = GlassColors.TextAccent,
                    unfocusedBorderColor = GlassColors.GlassBorderLight,
                    cursorColor = GlassColors.TextAccent,
                    focusedContainerColor = Color(0x11FFFFFF),
                    unfocusedContainerColor = Color(0x08FFFFFF)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Status feedback message mapping
            AnimatedVisibility(visible = statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    color = if (authState == "LOADING") GlassColors.TextAccent else if (authState?.contains("SUCCESS") == true) GlassColors.GlowGreen else GlassColors.CoralPeach,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action submit button with apple glow glass style
            if (authState == "LOADING") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GlassColors.TextAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (isLoginMode) {
                            viewModel.login(username, password)
                        } else {
                            viewModel.register(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = BoxValues() // empty so container takes background custom overlay
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GlassColors.SoftIndigo, GlassColors.GlowViolet)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isLoginMode) "Masuk" else "Daftar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Toggle mode link
            Row(
                modifier = Modifier
                    .clickable {
                        isLoginMode = !isLoginMode
                        viewModel.clearAuthState()
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ",
                    color = GlassColors.TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = if (isLoginMode) "Daftar di sini" else "Masuk di sini",
                    color = GlassColors.TextAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Custom padding remover so button completely fits backing brush gradient
@Composable
private fun BoxValues() = androidx.compose.foundation.layout.PaddingValues(0.dp)
