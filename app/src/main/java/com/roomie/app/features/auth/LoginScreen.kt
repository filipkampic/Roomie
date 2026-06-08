package com.roomie.app.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieButton
import com.roomie.app.core.ui.components.RoomieLogo
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.TealPrimary

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetSent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            RoomieLogo()

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue managing your household",
                style = MaterialTheme.typography.bodyMedium,
                color = NavySecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CardPadding)
                ) {
                    RoomieTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email Address",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                    RoomieTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "Hide password"
                                    else
                                        "Show password",
                                    tint = NavySecondary
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Dimens.SpaceXS)
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showResetDialog = true }) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TealPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            RoomieButton(
                text = "Sign In",
                onClick = { viewModel.login(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank(),
                isLoading = uiState is AuthUiState.Loading
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            TextButton(onClick = {
                viewModel.resetState()
                navController.navigate(Screen.Register.route)
            }) {
                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account?")
                        withStyle(SpanStyle(color = TealPrimary)) {
                            append(" Sign Up")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavySecondary
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                showResetDialog = false
                resetSent = false
                resetEmail = ""
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Reset Password") },
            text = {
                if (resetSent) {
                    Text("Password reset email sent to $resetEmail")
                } else {
                    Column {
                        Text("Enter your email address")
                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                        RoomieTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            placeholder = "Email Address",
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                if (!resetSent) {
                    TextButton(
                        onClick = {
                            viewModel.sendPasswordReset(resetEmail)
                            resetSent = true
                        },
                        enabled = resetEmail.isNotBlank()
                    ) {
                        Text("Send", color = TealPrimary)
                    }
                } else {
                    TextButton(onClick = {
                        showResetDialog = false
                        resetSent = false
                        resetEmail = ""
                    }) {
                        Text("OK", color = TealPrimary)
                    }
                }
            },
            dismissButton = {
                if (!resetSent) {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}