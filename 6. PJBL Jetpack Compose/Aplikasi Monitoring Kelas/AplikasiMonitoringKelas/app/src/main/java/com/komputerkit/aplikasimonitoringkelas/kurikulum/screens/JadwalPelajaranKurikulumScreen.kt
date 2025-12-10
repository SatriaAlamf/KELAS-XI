package com.komputerkit.aplikasimonitoringkelas.kurikulum.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.aplikasimonitoringkelas.api.ApiClient
import com.komputerkit.aplikasimonitoringkelas.api.TokenManager
import com.komputerkit.aplikasimonitoringkelas.api.models.KelasData
import com.komputerkit.aplikasimonitoringkelas.api.models.JadwalData
import com.komputerkit.aplikasimonitoringkelas.components.AppHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPelajaranKurikulumScreen(role: String, email: String, name: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = TokenManager(context)
    val apiService = ApiClient.getApiService()
    
    // States untuk spinner
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf<KelasData?>(null) }
    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }
    
    // Data lists
    var kelasList by remember { mutableStateOf<List<KelasData>>(emptyList()) }
    var jadwalList by remember { mutableStateOf<List<JadwalData>>(emptyList()) }
    
    // Loading states
    var isLoadingKelas by remember { mutableStateOf(false) }
    var isLoadingJadwal by remember { mutableStateOf(false) }
    
    // Hardcoded hari list (Senin - Minggu)
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    
    // Function to load jadwal by kelas and hari
    fun loadJadwal() {
        if (selectedKelas == null) return
        
        scope.launch {
            isLoadingJadwal = true
            try {
                android.util.Log.d("JadwalKurikulum", "Loading jadwal for kelas=${selectedKelas!!.id}, hari=$selectedHari")
                val token = "Bearer ${tokenManager.getToken()}"
                val response = apiService.getJadwalByKelasAndHari(
                    token = token,
                    kelasId = selectedKelas!!.id,
                    hari = selectedHari
                )
                
                if (response.isSuccessful && response.body() != null) {
                    jadwalList = response.body()!!.data
                    android.util.Log.d("JadwalKurikulum", "✓ Loaded ${jadwalList.size} jadwal")
                } else {
                    android.util.Log.e("JadwalKurikulum", "✗ Response failed: ${response.code()}")
                    jadwalList = emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("JadwalKurikulum", "✗ Error loading jadwal: ${e.message}")
                e.printStackTrace()
                jadwalList = emptyList()
            } finally {
                isLoadingJadwal = false
            }
        }
    }
    
    // Function to load kelas from API
    fun loadKelas() {
        scope.launch {
            isLoadingKelas = true
            try {
                android.util.Log.d("JadwalKurikulum", "Loading kelas...")
                val response = apiService.getKelas()
                kelasList = response.data
                android.util.Log.d("JadwalKurikulum", "✓ Loaded ${kelasList.size} kelas")
                
                // Auto-select first kelas
                if (kelasList.isNotEmpty() && selectedKelas == null) {
                    selectedKelas = kelasList[0]
                    loadJadwal()
                }
            } catch (e: Exception) {
                android.util.Log.e("JadwalKurikulum", "✗ Error loading kelas: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoadingKelas = false
            }
        }
    }
    
    // Auto-load kelas on screen open
    LaunchedEffect(Unit) {
        loadKelas()
    }
    
    // Reload jadwal when hari or kelas changes
    LaunchedEffect(selectedHari, selectedKelas) {
        if (selectedKelas != null) {
            loadJadwal()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppHeader(
            userName = name,
            userEmail = email,
            onLogoutClick = onLogout,
            onRefreshClick = {
                loadKelas()
                if (selectedKelas != null) loadJadwal()
            }
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filter Cards Container
            item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Spinner Hari
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pilih Hari",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedHari,
                        onExpandedChange = { expandedHari = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedHari,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(),
                            shape = RoundedCornerShape(12.dp)
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
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Spinner Kelas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "School",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pilih Kelas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedKelas,
                        onExpandedChange = { expandedKelas = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedKelas?.nama_kelas ?: if (isLoadingKelas) "Loading..." else "Pilih Kelas",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                if (isLoadingKelas) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(),
                            shape = RoundedCornerShape(12.dp)
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
        }

        // Header info
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jadwal Pelajaran",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${selectedKelas?.nama_kelas ?: "-"} • $selectedHari",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Loading indicator
        if (isLoadingJadwal) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Empty state
        if (!isLoadingJadwal && jadwalList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak ada jadwal",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Daftar jadwal - Modern Design
        items(jadwalList) { jadwal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Time Badge
                    Card(
                        modifier = Modifier.size(56.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Time",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${jadwal.jam_ke}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = jadwal.mapel?.nama_mapel ?: "-",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Teacher",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${jadwal.guru?.kode_guru ?: "-"} • ${jadwal.guru?.nama_guru ?: "-"}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    }
}
