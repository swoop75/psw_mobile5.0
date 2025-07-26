package com.psw.mobile.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psw.mobile.viewmodel.DashboardViewModel
import com.psw.mobile.viewmodel.DashboardUiState
import kotlin.math.cos
import kotlin.math.sin

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
                        val successState = uiState as DashboardUiState.Success
                        val stats = successState.stats
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
                        val errorState = uiState as DashboardUiState.Error
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
                                    text = errorState.message,
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

            // Charts Section
            if (uiState is DashboardUiState.Success) {
                val stats = (uiState as DashboardUiState.Success).stats
                
                item {
                    // Company Status Pie Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Company Status Distribution",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            PieChart(
                                data = listOf(
                                    PieChartData("Active", stats.active.toFloat(), Color(0xFF4CAF50)),
                                    PieChartData("Inactive", stats.inactive.toFloat(), Color(0xFFF44336)),
                                    PieChartData("Pending", stats.pending.toFloat(), Color(0xFFFF9800))
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }

                item {
                    // Weekly Growth Bar Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Weekly New Companies",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            BarChart(
                                data = listOf(
                                    BarChartData("Mon", 5f),
                                    BarChartData("Tue", 8f),
                                    BarChartData("Wed", 3f),
                                    BarChartData("Thu", 12f),
                                    BarChartData("Fri", 7f),
                                    BarChartData("Sat", 2f),
                                    BarChartData("Sun", 1f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }

                item {
                    // Recent Companies Table
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Recent Companies",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            RecentCompaniesTable(
                                companies = listOf(
                                    CompanyTableData("Tech Corp", "Active", "2024-01-20"),
                                    CompanyTableData("Green Energy", "Pending", "2024-01-19"),
                                    CompanyTableData("Food Services", "Active", "2024-01-18"),
                                    CompanyTableData("Digital Media", "Inactive", "2024-01-17"),
                                    CompanyTableData("Healthcare Plus", "Active", "2024-01-16")
                                )
                            )
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

// Data classes for charts and tables
data class PieChartData(val label: String, val value: Float, val color: Color)
data class BarChartData(val label: String, val value: Float)
data class CompanyTableData(val name: String, val status: String, val date: String)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    
    Row(modifier = modifier) {
        // Pie chart
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .weight(1f)
        ) {
            val radius = size.minDimension / 2 * 0.8f
            val center = Offset(size.width / 2, size.height / 2)
            var startAngle = 0f
            
            data.forEach { item ->
                val sweepAngle = (item.value / total) * 360f
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }
        
        // Legend
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            data.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(item.color, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = item.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${item.value.toInt()}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    
    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barWidth = size.width / data.size * 0.6f
            val spacing = size.width / data.size * 0.4f
            
            data.forEachIndexed { index, item ->
                val barHeight = (item.value / maxValue) * size.height * 0.8f
                val x = index * (barWidth + spacing) + spacing / 2
                val y = size.height - barHeight
                
                drawRect(
                    color = Color(0xFF2196F3),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
                
                // Value will be shown in legend instead of on bars
            }
        }
        
        // X-axis labels with values
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.value.toInt().toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RecentCompaniesTable(
    companies: List<CompanyTableData>
) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Company",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Status",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Date",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        
        // Rows
        companies.forEach { company ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = company.name,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(2f)
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = company.status,
                        fontSize = 12.sp,
                        color = when (company.status) {
                            "Active" -> Color(0xFF4CAF50)
                            "Inactive" -> Color(0xFFF44336)
                            "Pending" -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .background(
                                when (company.status) {
                                    "Active" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                    "Inactive" -> Color(0xFFF44336).copy(alpha = 0.1f)
                                    "Pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Text(
                    text = company.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
        }
    }
}