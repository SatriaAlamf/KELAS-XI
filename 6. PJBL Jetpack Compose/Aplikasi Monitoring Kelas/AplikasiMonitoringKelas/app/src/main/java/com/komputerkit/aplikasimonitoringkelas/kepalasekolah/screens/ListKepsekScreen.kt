    package com.komputerkit.aplikasimonitoringkelas.kepalasekolah.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.aplikasimonitoringkelas.api.*
import com.komputerkit.aplikasimonitoringkelas.api.models.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.components.AppHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListKepsekScreen(role: String, email: String, name: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { ApiClient.getApiService() }
    
    // Filter States
    var selectedHari by remember { mutableStateOf<String?>(null) }
    var selectedKelas by remember { mutableStateOf<KelasData?>(null) }
    var selectedStatusMengajar by remember { mutableStateOf<String?>(null) }
    var selectedStatusPengganti by remember { mutableStateOf<String?>(null) }
    var selectedDurasiMin by remember { mutableStateOf("") }
    var selectedDurasiMax by remember { mutableStateOf("") }
    var selectedHasPengganti by remember { mutableStateOf<String?>(null) }
    
    // Dropdown expanded states
    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }
    var expandedStatusMengajar by remember { mutableStateOf(false) }
    var expandedStatusPengganti by remember { mutableStateOf(false) }
    var expandedHasPengganti by remember { mutableStateOf(false) }
    
    // Data states
    var kelasList by remember { mutableStateOf<List<KelasData>>(emptyList()) }
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarKepsekData>>(emptyList()) }
    var summary by remember { mutableStateOf<KepsekSummary?>(null) }
    
    var isLoadingKelas by remember { mutableStateOf(false) }
    var isLoadingGuruMengajar by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(true) }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val statusMengajarList = listOf("masuk", "tidak_masuk")
    val statusPenggantiList = listOf("aktif", "selesai", "semua", "tanpa_pengganti")
    val hasPenggantiList = listOf("true", "false", "all")
    
    // Load kelas list on start
    LaunchedEffect(Unit) {
        isLoadingKelas = true
        val token = "Bearer ${tokenManager.getToken()}"
        when (val result = ApiHelper.safeApiCall { apiService.getAllKelas(token) }) {
            is ApiResult.Success -> {
                kelasList = result.data.data
            }
            is ApiResult.Error -> {
                Toast.makeText(context, "Error loading kelas: ${result.message}", Toast.LENGTH_SHORT).show()
            }
            ApiResult.Loading -> {}
        }
        isLoadingKelas = false
    }
    
    // Function to load comprehensive data
    fun loadComprehensiveData() {
        scope.launch {
            isLoadingGuruMengajar = true
            val token = "Bearer ${tokenManager.getToken()}"
            
            when (val result = ApiHelper.safeApiCall {
                apiService.getKepsekComprehensiveList(
                    token = token,
                    hari = selectedHari,
                    kelasId = selectedKelas?.id,
                    statusMengajar = selectedStatusMengajar,
                    statusPengganti = selectedStatusPengganti,
                    durasiMin = if (selectedDurasiMin.isNotEmpty()) selectedDurasiMin.toIntOrNull() else null,
                    durasiMax = if (selectedDurasiMax.isNotEmpty()) selectedDurasiMax.toIntOrNull() else null,
                    hasPengganti = selectedHasPengganti
                )
            }) {
                is ApiResult.Success -> {
                    guruMengajarList = result.data.data
                    summary = result.data.summary
                    Toast.makeText(
                        context,
                        "Data berhasil dimuat: ${result.data.data.size} records",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ApiResult.Error -> {
                    Toast.makeText(
                        context,
                        "Error: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                ApiResult.Loading -> {}
            }
            isLoadingGuruMengajar = false
        }
    }
    
    // Function to reset filters
    fun resetFilters() {
        selectedHari = null
        selectedKelas = null
        selectedStatusMengajar = null
        selectedStatusPengganti = null
        selectedDurasiMin = ""
        selectedDurasiMax = ""
        selectedHasPengganti = null
        guruMengajarList = emptyList()
        summary = null
    }

    Column(Modifier.fillMaxSize()) {
        AppHeader(
            userName = name,
            userEmail = email,
            onLogoutClick = onLogout,
            onRefreshClick = { loadComprehensiveData() }
        )
        
        // Summary Cards
        if (summary != null) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SummaryCard(
                        title = "Total",
                        value = summary!!.totalGuruMengajar.toString(),
                        icon = Icons.Default.List,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
                        )
                    )
                }
                item {
                    SummaryCard(
                        title = "Masuk",
                        value = summary!!.totalMasuk.toString(),
                        icon = Icons.Default.CheckCircle,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                        )
                    )
                }
                item {
                    SummaryCard(
                        title = "Tidak Masuk",
                        value = summary!!.totalTidakMasuk.toString(),
                        icon = Icons.Default.Close,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFF44336), Color(0xFFC62828))
                        )
                    )
                }
                item {
                    SummaryCard(
                        title = "Dengan Pengganti",
                        value = summary!!.totalDenganPengganti.toString(),
                        icon = Icons.Default.Person,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF2196F3), Color(0xFF1565C0))
                        )
                    )
                }
                item {
                    SummaryCard(
                        title = "Tanpa Pengganti",
                        value = summary!!.totalTanpaPengganti.toString(),
                        icon = Icons.Default.AccountCircle,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                        )
                    )
                }
                item {
                    SummaryCard(
                        title = "Pengganti Aktif",
                        value = summary!!.totalPenggantiAktif.toString(),
                        icon = Icons.Default.Star,
                        gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))
                        )
                    )
                }
            }
        }
        
        // Filter Toggle Button
        OutlinedButton(
            onClick = { showFilters = !showFilters },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (showFilters) "Sembunyikan Filter" else "Tampilkan Filter")
        }
        
        // Filter Section
        if (showFilters) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Filter Komprehensif",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Row 1: Hari dan Kelas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Filter Hari
                        ExposedDropdownMenuBox(
                            expanded = expandedHari,
                            onExpandedChange = { expandedHari = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedHari ?: "Semua Hari",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hari") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedHari,
                                onDismissRequest = { expandedHari = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Hari") },
                                    onClick = {
                                        selectedHari = null
                                        expandedHari = false
                                    }
                                )
                                hariList.forEach { hari ->
                                    DropdownMenuItem(
                                        text = { Text(hari) },
                                        onClick = {
                                            selectedHari = hari
                                            expandedHari = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Filter Kelas
                        ExposedDropdownMenuBox(
                            expanded = expandedKelas,
                            onExpandedChange = { expandedKelas = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedKelas?.nama_kelas ?: "Semua Kelas",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kelas") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedKelas,
                                onDismissRequest = { expandedKelas = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Kelas") },
                                    onClick = {
                                        selectedKelas = null
                                        expandedKelas = false
                                    }
                                )
                                kelasList.forEach { kelas ->
                                    DropdownMenuItem(
                                        text = { Text(kelas.nama_kelas) },
                                        onClick = {
                                            selectedKelas = kelas
                                            expandedKelas = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Row 2: Status Mengajar dan Status Pengganti
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Filter Status Mengajar
                        ExposedDropdownMenuBox(
                            expanded = expandedStatusMengajar,
                            onExpandedChange = { expandedStatusMengajar = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedStatusMengajar?.replaceFirstChar { it.uppercase() }?.replace("_", " ") ?: "Semua Status",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Status Mengajar") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatusMengajar) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatusMengajar,
                                onDismissRequest = { expandedStatusMengajar = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Status") },
                                    onClick = {
                                        selectedStatusMengajar = null
                                        expandedStatusMengajar = false
                                    }
                                )
                                statusMengajarList.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.replaceFirstChar { it.uppercase() }.replace("_", " ")) },
                                        onClick = {
                                            selectedStatusMengajar = status
                                            expandedStatusMengajar = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Filter Status Pengganti
                        ExposedDropdownMenuBox(
                            expanded = expandedStatusPengganti,
                            onExpandedChange = { expandedStatusPengganti = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedStatusPengganti?.replaceFirstChar { it.uppercase() }?.replace("_", " ") ?: "Semua Pengganti",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Status Pengganti") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatusPengganti) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatusPengganti,
                                onDismissRequest = { expandedStatusPengganti = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Pengganti") },
                                    onClick = {
                                        selectedStatusPengganti = null
                                        expandedStatusPengganti = false
                                    }
                                )
                                statusPenggantiList.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.replace("_", " ").replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            selectedStatusPengganti = status
                                            expandedStatusPengganti = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Row 3: Durasi Min dan Max
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedDurasiMin,
                            onValueChange = { selectedDurasiMin = it.filter { char -> char.isDigit() } },
                            label = { Text("Durasi Min (hari)") },
                            placeholder = { Text("0") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = selectedDurasiMax,
                            onValueChange = { selectedDurasiMax = it.filter { char -> char.isDigit() } },
                            label = { Text("Durasi Max (hari)") },
                            placeholder = { Text("999") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Filter Has Pengganti
                    ExposedDropdownMenuBox(
                        expanded = expandedHasPengganti,
                        onExpandedChange = { expandedHasPengganti = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = when (selectedHasPengganti) {
                                "true" -> "Hanya dengan Pengganti"
                                "false" -> "Hanya tanpa Pengganti"
                                else -> "Semua (Ada/Tidak)"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter Pengganti") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHasPengganti) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedHasPengganti,
                            onDismissRequest = { expandedHasPengganti = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua (Ada/Tidak)") },
                                onClick = {
                                    selectedHasPengganti = null
                                    expandedHasPengganti = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hanya dengan Pengganti") },
                                onClick = {
                                    selectedHasPengganti = "true"
                                    expandedHasPengganti = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hanya tanpa Pengganti") },
                                onClick = {
                                    selectedHasPengganti = "false"
                                    expandedHasPengganti = false
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { resetFilters() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset")
                        }
                        
                        Button(
                            onClick = { loadComprehensiveData() },
                            enabled = !isLoadingGuruMengajar,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isLoadingGuruMengajar) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Tampilkan")
                        }
                    }
                }
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // List Section
        if (guruMengajarList.isEmpty() && !isLoadingGuruMengajar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Gunakan filter untuk menampilkan data",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guruMengajarList) { guruMengajar ->
                    GuruMengajarKepsekCard(guruMengajar = guruMengajar)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun GuruMengajarKepsekCard(guruMengajar: GuruMengajarKepsekData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (guruMengajar.status.lowercase()) {
                "tidak masuk" -> MaterialTheme.colorScheme.errorContainer
                "masuk" -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Hari + Jam + Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${guruMengajar.hari} • Jam ke-${guruMengajar.jamKe}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
                
                // Status Badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.error
                            "masuk" -> Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                ) {
                    Text(
                        text = guruMengajar.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            
            // Kelas Info (with jurusan and tingkat)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Kelas",
                    modifier = Modifier.size(20.dp),
                    tint = when (guruMengajar.status.lowercase()) {
                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = guruMengajar.kelas,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    if (guruMengajar.jurusan != null || guruMengajar.tingkat != null) {
                        Text(
                            text = "${guruMengajar.tingkat ?: ""} ${guruMengajar.jurusan ?: ""}".trim(),
                            fontSize = 12.sp,
                            color = when (guruMengajar.status.lowercase()) {
                                "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Guru Info (with kode_guru)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Guru",
                    modifier = Modifier.size(20.dp),
                    tint = when (guruMengajar.status.lowercase()) {
                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = guruMengajar.namaGuru,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    if (guruMengajar.kodeGuru != null) {
                        Text(
                            text = "Kode: ${guruMengajar.kodeGuru}",
                            fontSize = 11.sp,
                            color = when (guruMengajar.status.lowercase()) {
                                "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mata Pelajaran
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Mapel",
                    modifier = Modifier.size(20.dp),
                    tint = when (guruMengajar.status.lowercase()) {
                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = guruMengajar.mapel,
                    fontSize = 15.sp,
                    color = when (guruMengajar.status.lowercase()) {
                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
            
            // Keterangan (if available)
            if (!guruMengajar.keterangan.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Keterangan",
                        modifier = Modifier.size(18.dp),
                        tint = when (guruMengajar.status.lowercase()) {
                            "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Keterangan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (guruMengajar.status.lowercase()) {
                                "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                        Text(
                            text = guruMengajar.keterangan,
                            fontSize = 13.sp,
                            color = when (guruMengajar.status.lowercase()) {
                                "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
            }
            
            // Guru Pengganti Section
            if (guruMengajar.guruPengganti.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Guru Pengganti (${guruMengajar.guruPengganti.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (guruMengajar.status.lowercase()) {
                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                guruMengajar.guruPengganti.forEach { pengganti ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (pengganti.status.lowercase()) {
                                "aktif" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                                "selesai" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pengganti.guruPenggantiData?.namaGuru ?: "N/A",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when (guruMengajar.status.lowercase()) {
                                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                                
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (pengganti.status.lowercase()) {
                                            "aktif" -> Color(0xFF2196F3)
                                            "selesai" -> Color(0xFF4CAF50)
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                ) {
                                    Text(
                                        text = pengganti.status.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            if (pengganti.durasiHari != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Durasi: ${pengganti.durasiHari} hari",
                                    fontSize = 12.sp,
                                    color = when (guruMengajar.status.lowercase()) {
                                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    }
                                )
                            }
                            
                            if (pengganti.tanggalMulai != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateText = if (pengganti.tanggalSelesai != null) {
                                    "${pengganti.tanggalMulai} - ${pengganti.tanggalSelesai}"
                                } else {
                                    "Mulai: ${pengganti.tanggalMulai}"
                                }
                                Text(
                                    text = dateText,
                                    fontSize = 11.sp,
                                    color = when (guruMengajar.status.lowercase()) {
                                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                    }
                                )
                            }
                            
                            if (!pengganti.alasan.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Alasan: ${pengganti.alasan}",
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = when (guruMengajar.status.lowercase()) {
                                        "tidak masuk" -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (guruMengajar.status.lowercase() == "tidak masuk") {
                // Show "no substitute" indicator for tidak masuk without pengganti
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Belum ada guru pengganti",
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

