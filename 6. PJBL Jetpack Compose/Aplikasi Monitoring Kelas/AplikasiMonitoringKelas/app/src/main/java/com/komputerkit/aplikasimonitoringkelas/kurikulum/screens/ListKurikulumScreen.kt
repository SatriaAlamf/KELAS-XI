package com.komputerkit.aplikasimonitoringkelas.kurikulum.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.aplikasimonitoringkelas.api.*
import com.komputerkit.aplikasimonitoringkelas.api.models.*
import com.komputerkit.aplikasimonitoringkelas.components.AppHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListKurikulumScreen(role: String, email: String, name: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { ApiClient.getApiService() }

    // Filter states
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf<KelasData?>(null) }
    var selectedStatusFilter by remember { mutableStateOf("Semua") }
    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    // Data lists
    var kelasList by remember { mutableStateOf<List<KelasData>>(emptyList()) }
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarData>>(emptyList()) }
    var filteredList by remember { mutableStateOf<List<GuruMengajarData>>(emptyList()) }

    // Loading states
    var isLoadingKelas by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(false) }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val statusFilterList = listOf("Semua", "Belum Ada Pengganti", "Sudah Ada Pengganti")

    // Load kelas list on mount
    LaunchedEffect(Unit) {
        isLoadingKelas = true
        val token = "Bearer ${tokenManager.getToken()}"
        when (val result = ApiHelper.safeApiCall { apiService.getAllKelas(token) }) {
            is ApiResult.Success -> {
                kelasList = result.data.data
            }
            is ApiResult.Error -> {
                Toast.makeText(context, "Error load kelas: ${result.message}", Toast.LENGTH_SHORT).show()
            }
            ApiResult.Loading -> {}
        }
        isLoadingKelas = false
    }

    // Load guru mengajar when hari and kelas selected
    LaunchedEffect(selectedHari, selectedKelas) {
        if (selectedKelas != null) {
            isLoadingData = true
            val token = "Bearer ${tokenManager.getToken()}"
            when (val result = ApiHelper.safeApiCall {
                apiService.getGuruMengajarByHariKelas(
                    token,
                    GuruMengajarByHariKelasRequest(
                        hari = selectedHari,
                        kelasId = selectedKelas!!.id
                    )
                )
            }) {
                is ApiResult.Success -> {
                    guruMengajarList = result.data.data
                }
                is ApiResult.Error -> {
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    guruMengajarList = emptyList()
                }
                ApiResult.Loading -> {}
            }
            isLoadingData = false
        }
    }

    // Apply status filter
    LaunchedEffect(guruMengajarList, selectedStatusFilter) {
        filteredList = when (selectedStatusFilter) {
            "Belum Ada Pengganti" -> guruMengajarList.filter { it.status == "tidak_masuk" && (it.guruPengganti == null || it.guruPengganti.isEmpty()) }
            "Sudah Ada Pengganti" -> guruMengajarList.filter { it.guruPengganti != null && it.guruPengganti.isNotEmpty() }
            else -> guruMengajarList
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                userName = name,
                userEmail = email,
                onLogoutClick = onLogout,
                onRefreshClick = {
                    // Reload data
                    selectedKelas?.let { kelas ->
                        scope.launch {
                            isLoadingData = true
                            val token = "Bearer ${tokenManager.getToken()}"
                            when (val result = ApiHelper.safeApiCall {
                                apiService.getGuruMengajarByHariKelas(
                                    token,
                                    GuruMengajarByHariKelasRequest(hari = selectedHari, kelasId = kelas.id)
                                )
                            }) {
                                is ApiResult.Success -> guruMengajarList = result.data.data
                                else -> {}
                            }
                            isLoadingData = false
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            item {
                Text(
                    text = "Monitoring Guru Mengajar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
            }

            // Filter Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Filter Data",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Dropdown Hari
                        ExposedDropdownMenuBox(
                            expanded = expandedHari,
                            onExpandedChange = { expandedHari = !expandedHari },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedHari,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hari") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedHari,
                                onDismissRequest = { expandedHari = false }
                            ) {
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

                        // Dropdown Kelas
                        ExposedDropdownMenuBox(
                            expanded = expandedKelas,
                            onExpandedChange = { expandedKelas = !expandedKelas },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedKelas?.nama_kelas ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kelas") },
                                placeholder = { Text("Pilih Kelas") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedKelas,
                                onDismissRequest = { expandedKelas = false }
                            ) {
                                if (isLoadingKelas) {
                                    DropdownMenuItem(
                                        text = { Text("Loading...") },
                                        onClick = {}
                                    )
                                } else {
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

                        // Dropdown Status Pengganti
                        ExposedDropdownMenuBox(
                            expanded = expandedStatus,
                            onExpandedChange = { expandedStatus = !expandedStatus },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedStatusFilter,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Status Pengganti") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatus,
                                onDismissRequest = { expandedStatus = false }
                            ) {
                                statusFilterList.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status) },
                                        onClick = {
                                            selectedStatusFilter = status
                                            expandedStatus = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Statistics
            if (selectedKelas != null && !isLoadingData) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total",
                            value = guruMengajarList.size.toString(),
                            color = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Belum",
                            value = guruMengajarList.filter { it.status == "tidak_masuk" && (it.guruPengganti == null || it.guruPengganti.isEmpty()) }.size.toString(),
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Sudah",
                            value = guruMengajarList.filter { it.guruPengganti != null && it.guruPengganti.isNotEmpty() }.size.toString(),
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Data List
            if (selectedKelas == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pilih Hari dan Kelas untuk melihat data",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (isLoadingData) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
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
                                text = "Tidak ada data untuk filter ini",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(filteredList) { guruMengajar ->
                    GuruMengajarDetailCard(guruMengajar = guruMengajar)
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = color
            )
        }
    }
}

@Composable
fun GuruMengajarDetailCard(guruMengajar: GuruMengajarData) {
    val hasGuruPengganti = guruMengajar.guruPengganti != null && guruMengajar.guruPengganti.isNotEmpty()
    val backgroundColor = when {
        guruMengajar.status == "masuk" -> Color(0xFFE8F5E9)
        hasGuruPengganti -> Color(0xFFE3F2FD)
        else -> Color(0xFFFFF3E0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header - Nama Guru
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guruMengajar.namaGuru,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = guruMengajar.status)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Info Jadwal
            InfoRow(label = "Mata Pelajaran", value = guruMengajar.mapel)
            InfoRow(label = "Jam Ke", value = guruMengajar.jamKe)
            
            if (!guruMengajar.keterangan.isNullOrBlank()) {
                InfoRow(label = "Keterangan", value = guruMengajar.keterangan)
            }

            // Guru Pengganti Section
            if (hasGuruPengganti) {
                val pengganti = guruMengajar.guruPengganti.first()
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Guru Pengganti",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                InfoRow(
                    label = "Nama",
                    value = pengganti.guruPenggantiData?.namaGuru ?: "-"
                )
                
                val tanggalMulai = pengganti.tanggalMulai ?: "-"
                val tanggalSelesai = pengganti.tanggalSelesai ?: "-"
                val durasiText = if (tanggalMulai != "-" && tanggalSelesai != "-") {
                    "$tanggalMulai s/d $tanggalSelesai"
                } else {
                    "-"
                }
                InfoRow(label = "Durasi", value = durasiText)
                InfoRow(label = "Alasan", value = pengganti.alasan)
                
                StatusPenggantiBoidge(status = pengganti.status)
            } else if (guruMengajar.status == "tidak_masuk") {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Belum ada guru pengganti",
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "masuk" -> "Masuk" to Color(0xFF4CAF50)
        "tidak_masuk" -> "Tidak Masuk" to Color(0xFFFF5722)
        else -> status to Color.Gray
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StatusPenggantiBoidge(status: String) {
    val (text, color) = when (status) {
        "aktif" -> "Aktif" to Color(0xFF2196F3)
        "selesai" -> "Selesai" to Color.Gray
        else -> status to Color.Gray
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = "Status: $text",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
