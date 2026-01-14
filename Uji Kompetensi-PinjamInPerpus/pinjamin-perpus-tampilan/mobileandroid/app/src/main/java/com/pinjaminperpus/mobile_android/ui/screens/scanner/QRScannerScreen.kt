package com.pinjaminperpus.mobile_android.ui.screens.scanner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pinjaminperpus.mobile_android.data.MockData
import com.pinjaminperpus.mobile_android.data.model.Book
import com.pinjaminperpus.mobile_android.ui.components.BorrowConfirmationDialog
import com.pinjaminperpus.mobile_android.ui.components.SuccessDialog
import com.pinjaminperpus.mobile_android.ui.theme.*
import kotlinx.coroutines.delay

enum class ScanMode {
    BORROW,
    RETURN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    mode: ScanMode = ScanMode.BORROW,
    onBackClick: () -> Unit,
    onModeChange: (ScanMode) -> Unit
) {
    var scannedBook by remember { mutableStateOf<Book?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBorrowDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    
    // Simulate scanning after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        scannedBook = MockData.books.firstOrNull()
        if (scannedBook != null) {
            showBottomSheet = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (mode == ScanMode.BORROW) "Scan Peminjaman" else "Scan Pengembalian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Toggle flash */ }) {
                        Icon(
                            imageVector = Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Camera Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1a1a1a),
                                Color(0xFF2d2d2d),
                                Color(0xFF1a1a1a)
                            )
                        )
                    )
            )
            
            // Scan Frame
            ScanFrame(
                modifier = Modifier.align(Alignment.Center)
            )
            
            // Mode Toggle
            ModeToggle(
                mode = mode,
                onModeChange = onModeChange,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
            
            // Instructions
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Arahkan kamera ke QR Code buku",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Posisikan QR Code di dalam bingkai",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    // Bottom Sheet
    if (showBottomSheet && scannedBook != null) {
        ScannedBookBottomSheet(
            book = scannedBook!!,
            mode = mode,
            onDismiss = { showBottomSheet = false },
            onConfirm = {
                showBottomSheet = false
                if (mode == ScanMode.BORROW) {
                    showBorrowDialog = true
                } else {
                    successMessage = "Buku berhasil dikembalikan!"
                    showSuccessDialog = true
                }
            }
        )
    }
    
    // Dialogs
    if (showBorrowDialog && scannedBook != null) {
        BorrowConfirmationDialog(
            book = scannedBook!!,
            onConfirm = {
                showBorrowDialog = false
                successMessage = "Peminjaman berhasil!\nJangan lupa kembalikan sebelum tanggal jatuh tempo."
                showSuccessDialog = true
            },
            onDismiss = { showBorrowDialog = false }
        )
    }
    
    if (showSuccessDialog) {
        SuccessDialog(
            title = "Berhasil!",
            message = successMessage,
            onDismiss = {
                showSuccessDialog = false
                onBackClick()
            }
        )
    }
}

@Composable
private fun ScanFrame(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )
    
    Box(
        modifier = modifier
            .size(280.dp)
    ) {
        // Corner markers
        val cornerSize = 40.dp
        val cornerWidth = 4.dp
        val cornerColor = Primary
        
        // Top-left corner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerSize)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cornerWidth)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(cornerWidth)
                    .background(cornerColor)
            )
        }
        
        // Top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerSize)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cornerWidth)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .width(cornerWidth)
                    .background(cornerColor)
            )
        }
        
        // Bottom-left corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerSize)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(cornerWidth)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(cornerWidth)
                    .background(cornerColor)
            )
        }
        
        // Bottom-right corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerSize)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .height(cornerWidth)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxHeight()
                    .width(cornerWidth)
                    .background(cornerColor)
            )
        }
        
        // Scanning line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = (280.dp * scanLineOffset))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Primary,
                            Primary,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun ModeToggle(
    mode: ScanMode,
    onModeChange: (ScanMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50),
                color = if (mode == ScanMode.BORROW) Primary else Color.Transparent,
                onClick = { onModeChange(ScanMode.BORROW) }
            ) {
                Text(
                    text = "Peminjaman",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (mode == ScanMode.BORROW) Color.White else Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
            
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50),
                color = if (mode == ScanMode.RETURN) Primary else Color.Transparent,
                onClick = { onModeChange(ScanMode.RETURN) }
            ) {
                Text(
                    text = "Pengembalian",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (mode == ScanMode.RETURN) Color.White else Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannedBookBottomSheet(
    book: Book,
    mode: ScanMode,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Buku Terdeteksi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Book Info
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Book Cover
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.3f),
                                    Primary.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = book.title.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "ISBN: ${book.isbn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Availability badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (book.isAvailable) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (book.isAvailable) "Tersedia" else "Tidak Tersedia",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (book.isAvailable) Success else Error
                        )
                    }
                }
            }
            
            // Action Button
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Icon(
                    imageVector = if (mode == ScanMode.BORROW) Icons.Default.LibraryAdd else Icons.Default.AssignmentReturn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (mode == ScanMode.BORROW) "Pinjam Buku" else "Kembalikan Buku",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Cancel Button
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Batal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
