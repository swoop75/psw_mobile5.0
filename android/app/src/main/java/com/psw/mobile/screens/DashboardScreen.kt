package com.psw.mobile.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psw.mobile.viewmodel.DashboardViewModel
import com.psw.mobile.viewmodel.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMasterlist: () -> Unit,
    onNavigateToNewCompanies: () -> Unit,
    onLogout: () -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("PSW Dashboard") },
            actions = {
                IconButton(onClick = { dashboardViewModel.loadDashboardStats() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                IconButton(onClick = {
                    dashboardViewModel.logout()
                    onLogout()
                }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome to PSW Mobile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Manage your companies efficiently",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                DashboardCard(
                    title = "Master List",
                    description = "View and manage all companies",
                    icon = Icons.Default.Business,
                    onClick = onNavigateToMasterlist
                )
            }

            item {
                DashboardCard(
                    title = "New Companies",
                    description = "Review newly added companies",
                    icon = Icons.Default.Add,
                    onClick = onNavigateToNewCompanies
                )
            }

            item {
                // Quick Stats Card
                when (uiState) {
                    is DashboardUiState.Loading -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is DashboardUiState.Success -> {
                        val stats = uiState.stats
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Quick Stats",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatItem("Total Companies", stats.totalCompanies.toString())
                                    StatItem("New This Week", stats.newThisWeek.toString())
                                    StatItem("Pending", stats.pending.toString())
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StatItem("Active", stats.active.toString())
                                    StatItem("Inactive", stats.inactive.toString())
                                }
                            }
                        }
                    }
                    is DashboardUiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error loading stats",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = uiState.message,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Button(
                                    onClick = { dashboardViewModel.loadDashboardStats() },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}