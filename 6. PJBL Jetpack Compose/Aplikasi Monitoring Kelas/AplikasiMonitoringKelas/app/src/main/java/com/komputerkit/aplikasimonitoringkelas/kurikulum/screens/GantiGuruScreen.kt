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
import androidx.compose.ui.window.Dialog
import com.komputerkit.aplikasimonitoringkelas.api.*
import com.komputerkit.aplikasimonitoringkelas.api.models.*
import com.komputerkit.aplikasimonitoringkelas.components.AppHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiGuruScreen(role: String, email: String, name: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { ApiClient.getApiService() }

    // Filter states
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf<KelasData?>(null) }
    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }

    // Data lists
    var kelasList by remember { mutableStateOf<List<KelasData>>(emptyList()) }
    var guruTidakMasukList by remember { mutableStateOf<List<GuruMengajarData>>(emptyList()) }
    var allGuruList by remember { mutableStateOf<List<GuruData>>(emptyList()) }

    // Loading states
    var isLoadingKelas by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(false) }

    // Modal state
    var showModal by remember { mutableStateOf(false) }
    var selectedGuruMengajar by remember { mutableStateOf<GuruMengajarData?>(null) }
    var selectedGuruPengganti by remember { mutableStateOf<GuruData?>(null) }
    var alasan by remember { mutableStateOf("") }
    var expandedGuruPengganti by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

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

    // Load all guru list
    LaunchedEffect(Unit) {
        val token = "Bearer ${tokenManager.getToken()}"
        when (val result = ApiHelper.safeApiCall { apiService.getAllGurus(token) }) {
            is ApiResult.Success -> {
                allGuruList = result.data.data
            }
            is ApiResult.Error -> {
                Toast.makeText(context, "Error load guru: ${result.message}", Toast.LENGTH_SHORT).show()
            }
            ApiResult.Loading -> {}
        }
    }

    // Load guru tidak masuk when hari and kelas selected
    LaunchedEffect(selectedHari, selectedKelas) {
        if (selectedKelas != null) {
            isLoadingData = true
            val token = "Bearer ${tokenManager.getToken()}"
            when (val result = ApiHelper.safeApiCall {
                apiService.getGuruTidakMasuk(
                    token,
                    GuruMengajarByHariKelasRequest(
                        hari = selectedHari,
                        kelasId = selectedKelas!!.id
                    )
                )
            }) {
                is ApiResult.Success -> {
                    guruTidakMasukList = result.data.data
                }
                is ApiResult.Error -> {
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    guruTidakMasukList = emptyList()
                }
                ApiResult.Loading -> {}
            }
            isLoadingData = false
        }
    }

    // Function to save guru pengganti
    fun saveGuruPengganti() {
        if (selectedGuruMengajar == null || selectedGuruPengganti == null || alasan.isBlank()) {
            Toast.makeText(context, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isSaving = true
            val token = "Bearer ${tokenManager.getToken()}"
            val request = GuruPenggantiRequest(
                guru_mengajar_id = selectedGuruMengajar!!.id,
                guru_pengganti_id = selectedGuruPengganti!!.id,
                alasan = alasan,
                tanggal_mulai = null,
                tanggal_selesai = null,
                keterangan = null,
                status = "aktif"
            )

            when (val result = ApiHelper.safeApiCall {
                apiService.storeGuruPengganti(token, request)
            }) {
                is ApiResult.Success -> {
                    Toast.makeText(context, "Guru pengganti berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    showModal = false
                    alasan = ""
                    selectedGuruPengganti = null
                    // Reload data
                    selectedKelas?.let { kelas ->
                        when (val reloadResult = ApiHelper.safeApiCall {
                            apiService.getGuruTidakMasuk(
                                token,
                                GuruMengajarByHariKelasRequest(hari = selectedHari, kelasId = kelas.id)
                            )
                        }) {
                            is ApiResult.Success -> {
                                guruTidakMasukList = reloadResult.data.data
                            }
                            else -> {}
                        }
                    }
                }
                is ApiResult.Error -> {
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                ApiResult.Loading -> {}
            }
            isSaving = false
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
                                apiService.getGuruTidakMasuk(
                                    token,
                                    GuruMengajarByHariKelasRequest(hari = selectedHari, kelasId = kelas.id)
                                )
                            }) {
                                is ApiResult.Success -> guruTidakMasukList = result.data.data
                                else -> {}
                            }
                            isLoadingData = false
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Kelola Guru Pengganti",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filter Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                        modifier = Modifier.fillMaxWidth()
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
                }
            }

            // Data List
            if (selectedKelas == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pilih Hari dan Kelas untuk melihat data",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (isLoadingData) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (guruTidakMasukList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada guru yang tidak masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(guruTidakMasukList) { guruMengajar ->
                        GuruTidakMasukCard(
                            guruMengajar = guruMengajar,
                            onTambahPengganti = {
                                selectedGuruMengajar = guruMengajar
                                showModal = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog for adding guru pengganti
    if (showModal) {
        Dialog(onDismissRequest = { showModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Tambah Guru Pengganti",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Info guru asli
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Guru Asli:",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = selectedGuruMengajar?.namaGuru ?: "-",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${selectedGuruMengajar?.mapel ?: "-"} • Jam ke-${selectedGuruMengajar?.jamKe ?: "-"}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Dropdown Guru Pengganti
                    ExposedDropdownMenuBox(
                        expanded = expandedGuruPengganti,
                        onExpandedChange = { expandedGuruPengganti = !expandedGuruPengganti },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedGuruPengganti?.namaGuru ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Guru Pengganti") },
                            placeholder = { Text("Pilih guru") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGuruPengganti) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGuruPengganti,
                            onDismissRequest = { expandedGuruPengganti = false }
                        ) {
                            allGuruList.forEach { guru ->
                                DropdownMenuItem(
                                    text = { Text(guru.namaGuru) },
                                        onClick = {
                                            selectedGuruPengganti = guru
                                            expandedGuruPengganti = false
                                        }
                                    )
                                }
                        }
                    }

                    // TextField Alasan
                    OutlinedTextField(
                        value = alasan,
                        onValueChange = { alasan = it },
                        label = { Text("Alasan") },
                        placeholder = { Text("Masukkan alasan penggantian") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        minLines = 3
                    )

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showModal = false
                                alasan = ""
                                selectedGuruPengganti = null
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = { saveGuruPengganti() },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving && selectedGuruPengganti != null && alasan.isNotBlank()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Simpan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuruTidakMasukCard(
    guruMengajar: GuruMengajarData,
    onTambahPengganti: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guruMengajar.namaGuru,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${guruMengajar.mapel} • Jam ke-${guruMengajar.jamKe}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (!guruMengajar.keterangan.isNullOrBlank()) {
                    Text(
                        text = "Keterangan: ${guruMengajar.keterangan}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Button
            Button(
                onClick = onTambahPengganti,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah")
            }
        }
    }
}
