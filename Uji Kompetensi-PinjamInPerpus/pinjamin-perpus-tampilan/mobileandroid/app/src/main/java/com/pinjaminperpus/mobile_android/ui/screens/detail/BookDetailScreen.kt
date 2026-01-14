package com.pinjaminperpus.mobile_android.ui.screens.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinjaminperpus.mobile_android.data.MockData
import com.pinjaminperpus.mobile_android.data.model.Book
import com.pinjaminperpus.mobile_android.data.model.Review
import com.pinjaminperpus.mobile_android.ui.components.BorrowConfirmationDialog
import com.pinjaminperpus.mobile_android.ui.components.SuccessDialog
import com.pinjaminperpus.mobile_android.ui.theme.*
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    onBorrowSuccess: () -> Unit
) {
    val book = remember { MockData.getBookById(bookId) }
    var selectedTab by remember { mutableStateOf(0) }
    var showBorrowDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    
    val reviews = remember { MockData.getReviewsForBook(bookId) }
    
    if (book == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Buku tidak ditemukan")
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Favorite Button
                    OutlinedButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Borrow Button
                    Button(
                        onClick = { showBorrowDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (book.isAvailable) Primary else Color.Gray
                        ),
                        enabled = book.isAvailable
                    ) {
                        Text(
                            text = if (book.isAvailable) "Pinjam Buku" else "Tidak Tersedia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (book.isAvailable) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Book Cover
            Box(
                modifier = Modifier
                    .width(176.dp)
                    .aspectRatio(2f / 3f)
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.4f),
                                Primary.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = book.title.take(2).uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Book Info
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) { index ->
                    val rating = book.rating
                    Icon(
                        imageVector = when {
                            index < rating.toInt() -> Icons.Filled.Star
                            index < rating -> Icons.Filled.StarHalf
                            else -> Icons.Filled.StarOutline
                        },
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", book.rating),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(${formatNumber(book.reviewCount)} Ulasan)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Availability Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = if (book.isAvailable) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (book.isAvailable) Success.copy(alpha = 0.2f) else Error.copy(alpha = 0.2f),
                            if (book.isAvailable) Success.copy(alpha = 0.2f) else Error.copy(alpha = 0.2f)
                        )
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (book.isAvailable) Success else Error,
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = if (book.isAvailable) "Tersedia • ${book.availableStock} Stok" else "Tidak Tersedia",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (book.isAvailable) Success else Error,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Metadata Cards
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetadataCard("Kategori", book.category)
                MetadataCard("Halaman", "${book.pages} Hal")
                MetadataCard("Bahasa", book.language)
                MetadataCard("ISBN", book.isbn.takeLast(7))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Deskripsi",
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
                            text = "Ulasan",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selectedContentColor = Primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                when (selectedTab) {
                    0 -> DescriptionTab(book)
                    1 -> ReviewsTab(reviews)
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
    
    // Dialogs
    if (showBorrowDialog && book != null) {
        BorrowConfirmationDialog(
            book = book,
            onConfirm = {
                showBorrowDialog = false
                showSuccessDialog = true
            },
            onDismiss = { showBorrowDialog = false }
        )
    }
    
    if (showSuccessDialog) {
        SuccessDialog(
            title = "Peminjaman Berhasil!",
            message = "Buku ${book.title} berhasil dipinjam. Silakan ambil buku di perpustakaan.",
            onDismiss = {
                showSuccessDialog = false
                onBorrowSuccess()
            }
        )
    }
}

@Composable
private fun MetadataCard(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DescriptionTab(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun ReviewsTab(reviews: List<Review>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada ulasan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ulasan Pembaca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Terbaru",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary
                    )
                }
            }
            
            reviews.forEach { review ->
                ReviewItem(review = review)
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    val colors = listOf(
        Color(0xFFFFF3E0), // Amber
        Color(0xFFE3F2FD), // Blue
        Color(0xFFF3E5F5), // Purple
        Color(0xFFE8F5E9)  // Green
    )
    val textColors = listOf(
        Color(0xFFFF8F00),
        Color(0xFF1976D2),
        Color(0xFF7B1FA2),
        Color(0xFF388E3C)
    )
    val colorIndex = review.userInitials.hashCode().mod(colors.size).let { if (it < 0) -it else it }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(50),
                    color = colors[colorIndex]
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = review.userInitials,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColors[colorIndex]
                        )
                    }
                }
                
                Column {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                repeat(review.rating) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${review.likes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Balas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Divider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000 -> String.format("%.1fk", number / 1000f)
        else -> number.toString()
    }
}


