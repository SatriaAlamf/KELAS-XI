package com.pinjaminperpus.mobile_android.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pinjaminperpus.mobile_android.data.MockData
import com.pinjaminperpus.mobile_android.data.model.Borrowing
import com.pinjaminperpus.mobile_android.ui.components.ExtendBorrowingDialog
import com.pinjaminperpus.mobile_android.ui.components.SuccessDialog
import com.pinjaminperpus.mobile_android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNotificationClick: () -> Unit,
    onReturnClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showExtendDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedBorrowing by remember { mutableStateOf<Borrowing?>(null) }
    
    val activeBorrowings = MockData.activeBorrowings
    val completedBorrowings = MockData.completedBorrowings
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Riwayat",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    actions = {
                        IconButton(onClick = onNotificationClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Primary,
                            height = 3.dp
                        )
                    },
                    divider = { }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Dipinjam",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = Primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Selesai",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = Primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> ActiveBorrowingsList(
                borrowings = activeBorrowings,
                modifier = Modifier.padding(padding),
                onReturnClick = onReturnClick,
                onExtendClick = { borrowing ->
                    selectedBorrowing = borrowing
                    showExtendDialog = true
                }
            )
            1 -> CompletedBorrowingsList(
                borrowings = completedBorrowings,
                modifier = Modifier.padding(padding)
            )
        }
    }
    
    // Dialogs
    if (showExtendDialog && selectedBorrowing != null) {
        ExtendBorrowingDialog(
            bookTitle = selectedBorrowing!!.book.title,
            currentDueDate = selectedBorrowing!!.dueDate,
            onConfirm = {
                showExtendDialog = false
                showSuccessDialog = true
            },
            onDismiss = { showExtendDialog = false }
        )
    }
    
    if (showSuccessDialog) {
        SuccessDialog(
            title = "Perpanjangan Berhasil!",
            message = "Masa peminjaman telah diperpanjang 7 hari.",
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
private fun ActiveBorrowingsList(
    borrowings: List<Borrowing>,
    modifier: Modifier = Modifier,
    onReturnClick: (String) -> Unit,
    onExtendClick: (Borrowing) -> Unit
) {
    if (borrowings.isEmpty()) {
        EmptyState(
            message = "Tidak ada buku yang sedang dipinjam",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(borrowings) { borrowing ->
                ActiveBorrowingCard(
                    borrowing = borrowing,
                    onReturnClick = { onReturnClick(borrowing.id) },
                    onExtendClick = { onExtendClick(borrowing) }
                )
            }
        }
    }
}

@Composable
private fun ActiveBorrowingCard(
    borrowing: Borrowing,
    onReturnClick: () -> Unit,
    onExtendClick: () -> Unit
) {
    val isUrgent = borrowing.daysRemaining <= 2
    val progress = 1f - (borrowing.daysRemaining / 14f).coerceIn(0f, 1f)
    
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Book Cover
                Box(
                    modifier = Modifier
                        .width(96.dp)
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
                        text = borrowing.book.title.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = borrowing.book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = borrowing.book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Due Date Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isUrgent) Warning.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUrgent) Icons.Default.PriorityHigh else Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isUrgent) Warning else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isUrgent) "Jatuh tempo besok" else "Jatuh tempo: ${borrowing.dueDate}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgent) Color(0xFFF57C00) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            // Progress Bar
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sisa waktu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${borrowing.daysRemaining} hari",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReturnClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUrgent) Primary else Primary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardReturn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isUrgent) Color.White else Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kembalikan",
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color.White else Primary
                    )
                }
                
                OutlinedButton(
                    onClick = onExtendClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Perpanjang",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedBorrowingsList(
    borrowings: List<Borrowing>,
    modifier: Modifier = Modifier
) {
    if (borrowings.isEmpty()) {
        EmptyState(
            message = "Belum ada riwayat peminjaman",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(borrowings) { borrowing ->
                CompletedBorrowingCard(borrowing = borrowing)
            }
        }
    }
}

@Composable
private fun CompletedBorrowingCard(borrowing: Borrowing) {
    val isOnTime = borrowing.fine == 0
    
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Book Cover
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = borrowing.book.title.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = borrowing.book.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = borrowing.book.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${borrowing.borrowDate} - ${borrowing.returnDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isOnTime) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isOnTime) "Tepat Waktu" else "Terlambat",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnTime) Success else Error
                    )
                }
            }
            
            if (!isOnTime) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Error.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Denda Keterlambatan",
                            style = MaterialTheme.typography.labelMedium,
                            color = Error
                        )
                        Text(
                            text = "Rp ${String.format("%,d", borrowing.fine)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
