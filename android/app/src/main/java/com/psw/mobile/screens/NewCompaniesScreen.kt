package com.psw.mobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psw.mobile.viewmodel.NewCompaniesViewModel
import com.psw.mobile.viewmodel.NewCompaniesUiState
import com.psw.mobile.data.model.NewCompany
import com.psw.mobile.data.model.Broker
import com.psw.mobile.data.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCompaniesScreen(
    onBackPressed: () -> Unit,
    newCompaniesViewModel: NewCompaniesViewModel = viewModel()
) {
    val uiState by newCompaniesViewModel.uiState.collectAsState()
    val brokers by newCompaniesViewModel.brokers.collectAsState()
    val countries by newCompaniesViewModel.countries.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("New Companies") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { newCompaniesViewModel.loadNewCompanies() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search and filter controls
            var searchText by remember { mutableStateOf("") }
            var selectedStatus by remember { mutableStateOf("pending") }
            var selectedBroker by remember { mutableStateOf("all") }
            var selectedCountry by remember { mutableStateOf("all") }
            var showStatusDropdown by remember { mutableStateOf(false) }
            var showBrokerDropdown by remember { mutableStateOf(false) }
            var showCountryDropdown by remember { mutableStateOf(false) }
            
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    newCompaniesViewModel.applyFilters(
                        search = it,
                        status = selectedStatus,
                        broker = if (selectedBroker == "all") null else selectedBroker,
                        country = if (selectedCountry == "all") null else selectedCountry
                    )
                },
                label = { Text("Search companies...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )
            
            // Filter controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                Text("Filters:", fontSize = 14.sp)
            }
            
            // Status filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", fontSize = 12.sp, modifier = Modifier.width(60.dp))
                
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showStatusDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            when (selectedStatus) {
                                "pending" -> "Pending"
                                "bought" -> "Bought"
                                "blocked" -> "Blocked"
                                "no" -> "No"
                                "all" -> "All"
                                else -> "Pending"
                            },
                            fontSize = 12.sp
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        listOf(
                            "pending" to "Pending", 
                            "bought" to "Bought", 
                            "blocked" to "Blocked", 
                            "no" to "No", 
                            "all" to "All"
                        ).forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedStatus = value
                                    showStatusDropdown = false
                                    newCompaniesViewModel.applyFilters(
                                        search = if (searchText.isBlank()) null else searchText,
                                        status = value,
                                        broker = if (selectedBroker == "all") null else selectedBroker,
                                        country = if (selectedCountry == "all") null else selectedCountry
                                    )
                                }
                            )
                        }
                    }
                }
            }
            
            // Broker and Country filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Broker filter
                Column(modifier = Modifier.weight(1f)) {
                    Text("Broker:", fontSize = 12.sp)
                    Box {
                        OutlinedButton(
                            onClick = { showBrokerDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (selectedBroker == "all") "All Brokers" else {
                                    brokers.find { it.id == selectedBroker }?.name ?: "All Brokers"
                                },
                                fontSize = 12.sp
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showBrokerDropdown,
                            onDismissRequest = { showBrokerDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Brokers") },
                                onClick = {
                                    selectedBroker = "all"
                                    showBrokerDropdown = false
                                    newCompaniesViewModel.applyFilters(
                                        search = if (searchText.isBlank()) null else searchText,
                                        status = selectedStatus,
                                        broker = null,
                                        country = if (selectedCountry == "all") null else selectedCountry
                                    )
                                }
                            )
                            brokers.forEach { broker: Broker ->
                                DropdownMenuItem(
                                    text = { Text(broker.name) },
                                    onClick = {
                                        selectedBroker = broker.id
                                        showBrokerDropdown = false
                                        newCompaniesViewModel.applyFilters(
                                            search = if (searchText.isBlank()) null else searchText,
                                            status = selectedStatus,
                                            broker = broker.id,
                                            country = if (selectedCountry == "all") null else selectedCountry
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Country filter
                Column(modifier = Modifier.weight(1f)) {
                    Text("Country:", fontSize = 12.sp)
                    Box {
                        OutlinedButton(
                            onClick = { showCountryDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (selectedCountry == "all") "All Countries" else {
                                    countries.find { it.id == selectedCountry }?.name ?: "All Countries"
                                },
                                fontSize = 12.sp
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showCountryDropdown,
                            onDismissRequest = { showCountryDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Countries") },
                                onClick = {
                                    selectedCountry = "all"
                                    showCountryDropdown = false
                                    newCompaniesViewModel.applyFilters(
                                        search = if (searchText.isBlank()) null else searchText,
                                        status = selectedStatus,
                                        broker = if (selectedBroker == "all") null else selectedBroker,
                                        country = null
                                    )
                                }
                            )
                            countries.forEach { country: Country ->
                                DropdownMenuItem(
                                    text = { Text(country.name) },
                                    onClick = {
                                        selectedCountry = country.id
                                        showCountryDropdown = false
                                        newCompaniesViewModel.applyFilters(
                                            search = if (searchText.isBlank()) null else searchText,
                                            status = selectedStatus,
                                            broker = if (selectedBroker == "all") null else selectedBroker,
                                            country = country.id
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            when (uiState) {
                is NewCompaniesUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is NewCompaniesUiState.Success -> {
                    val successState = uiState as NewCompaniesUiState.Success
                    val newCompanies = successState.companies
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
                is NewCompaniesUiState.Error -> {
                    val errorState = uiState as NewCompaniesUiState.Error
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading new companies",
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
                            onClick = { newCompaniesViewModel.loadNewCompanies() },
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
                    company.ticker?.let { 
                        InfoRow(label = "Ticker:", value = it)
                    }
                }
                Column {
                    InfoRow(label = "Status:", value = company.status)
                    company.brokerName?.let { 
                        InfoRow(label = "Broker:", value = it)
                    }
                    if (company.yield_percent > 0) {
                        InfoRow(label = "Yield:", value = "${company.yield_percent}%")
                    }
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