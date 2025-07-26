package com.psw.mobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Company(
    val id: String,
    val name: String,
    val industry: String,
    val location: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterlistScreen(
    onBackPressed: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample data - in real app, this would come from database
    val companies = remember {
        listOf(
            Company("1", "Tech Solutions Inc", "Technology", "New York", "Active"),
            Company("2", "Green Energy Corp", "Energy", "California", "Active"),
            Company("3", "Healthcare Plus", "Healthcare", "Texas", "Pending"),
            Company("4", "Manufacturing Pro", "Manufacturing", "Michigan", "Active"),
            Company("5", "Finance First", "Finance", "Illinois", "Active"),
            Company("6", "Retail Chain Ltd", "Retail", "Florida", "Inactive"),
            Company("7", "Construction King", "Construction", "Nevada", "Active"),
            Company("8", "Food & Beverage Co", "Food", "Oregon", "Pending")
        )
    }
    
    val filteredCompanies = companies.filter { 
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.industry.contains(searchQuery, ignoreCase = true) ||
        it.location.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Master List") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search companies...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "${filteredCompanies.size} companies found",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCompanies) { company ->
                    CompanyCard(company = company)
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company: Company) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = company.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(status = company.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = company.industry,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = company.location,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Active" -> MaterialTheme.colorScheme.primary
        "Pending" -> MaterialTheme.colorScheme.secondary
        "Inactive" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}