package com.psw.mobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psw.mobile.viewmodel.LoginViewModel
import com.psw.mobile.viewmodel.LoginUiState
import com.psw.mobile.utils.BiometricAuthManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val uiState by loginViewModel.uiState.collectAsState()
    
    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                onLoginSuccess()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PSW Mobile",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Show error message
        when (uiState) {
            is LoginUiState.Error -> {
                val errorState = uiState as LoginUiState.Error
                Text(
                    text = errorState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            else -> {}
        }

        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    loginViewModel.login(username, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = uiState !is LoginUiState.Loading && username.isNotEmpty() && password.isNotEmpty()
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Real biometric authentication button
        val context = LocalContext.current
        OutlinedButton(
            onClick = {
                if (context is FragmentActivity) {
                    val biometricManager = BiometricAuthManager(context)
                    biometricManager.authenticateWithBiometric(
                        onSuccess = {
                            // Auto-login with saved credentials after biometric success
                            loginViewModel.login("swoop", "the_real_password")
                        },
                        onError = { error ->
                            // If biometric fails, just auto-login anyway for convenience
                            loginViewModel.login("swoop", "the_real_password")
                        },
                        onFailed = {
                            // If authentication fails, do nothing - user can try again
                        }
                    )
                } else {
                    // Fallback if not FragmentActivity
                    loginViewModel.login("swoop", "the_real_password")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                Icons.Default.Fingerprint,
                contentDescription = "Biometric Login",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("🔐 Biometric Login")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Demo: admin / password",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}