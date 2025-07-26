package com.psw.mobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NewCompany(
    val id: String,
    val name: String,
    val industry: String,
    val location: String,
    val submittedBy: String,
    val submittedDate: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCompaniesScreen(
    onBackPressed: () -> Unit
) {
    // Sample data - in real app, this would come from database
    val newCompanies = remember {
        listOf(
            NewCompany(
                id = "1",
                name = "AI Innovations Ltd",
                industry = "Artificial Intelligence",
                location = "San Francisco, CA",
                submittedBy = "John Doe",
                submittedDate = "2024-01-15",
                description = "Leading AI research and development company"
            ),
            NewCompany(
                id = "2", 
                name = "Solar Power Systems",
                industry = "Renewable Energy",
                location = "Austin, TX",
                submittedBy = "Jane Smith",
                submittedDate = "2024-01-14",
                description = "Solar panel installation and maintenance services"
            ),
            NewCompany(
                id = "3",
                name = "Bio Medical Research",
                industry = "Healthcare",
                location = "Boston, MA", 
                submittedBy = "Dr. Wilson",
                submittedDate = "2024-01-13",
                description = "Cutting-edge medical research and drug development"
            ),
            NewCompany(
                id = "4",
                name = "Smart Logistics Inc",
                industry = "Transportation",
                location = "Chicago, IL",
                submittedBy = "Mike Johnson",
                submittedDate = "2024-01-12",
                description = "IoT-enabled supply chain and logistics solutions"
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("New Companies") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${newCompanies.size} companies pending review",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(newCompanies) { company ->
                    NewCompanyCard(company = company)
                }
            }
        }
    }
}

@Composable
fun NewCompanyCard(company: NewCompany) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "NEW",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = company.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    InfoRow(label = "Industry:", value = company.industry)
                    InfoRow(label = "Location:", value = company.location)
                }
                Column {
                    InfoRow(label = "Submitted by:", value = company.submittedBy)
                    InfoRow(label = "Date:", value = company.submittedDate)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Handle reject */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
                
                Button(
                    onClick = { /* Handle approve */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}