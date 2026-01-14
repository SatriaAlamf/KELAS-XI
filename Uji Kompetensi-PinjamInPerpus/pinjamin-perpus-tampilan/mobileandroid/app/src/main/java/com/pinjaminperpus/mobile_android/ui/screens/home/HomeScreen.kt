package com.pinjaminperpus.mobile_android.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pinjaminperpus.mobile_android.data.MockData
import com.pinjaminperpus.mobile_android.data.model.Book
import com.pinjaminperpus.mobile_android.data.model.Category
import com.pinjaminperpus.mobile_android.ui.components.*
import com.pinjaminperpus.mobile_android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQrScanClick: () -> Unit,
    onSeeAllClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    
    val categories = MockData.categories
    val banners = MockData.banners
    val popularBooks = MockData.getPopularBooks()
    val recommendedBooks = MockData.getRecommendedBooks()
    val unreadNotifications = MockData.getUnreadNotificationsCount()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQrScanClick,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan QR",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo and Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "📚",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = "PinjamIn",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Perpus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification Button with Badge
                    Box {
                        IconButton(onClick = onNotificationClick) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (unreadNotifications > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Error, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = 8.dp)
                            )
                        }
                    }
                    
                    // Profile Button
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = onProfileClick),
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = MockData.currentUser.name.take(1),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }
            }
            
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Cari judul, penulis, ISBN..."
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Banner Carousel
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(banners) { banner ->
                    BannerCard(
                        title = banner.title,
                        tag = banner.tag,
                        tagColor = when (banner.tagColor) {
                            "secondary" -> Secondary
                            "tertiary" -> Tertiary
                            else -> Primary
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Categories Section
            SectionHeader(
                title = "Kategori Buku",
                actionLabel = "Lihat Semua",
                onActionClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.take(6).forEach { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category.name,
                        onClick = { selectedCategory = category.name }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Popular Books Section
            SectionHeader(
                title = "Buku Populer",
                subtitle = "Sedang banyak dipinjam minggu ini",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(popularBooks) { book ->
                    BookCardCompact(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Recommendations Section
            SectionHeader(
                title = "Rekomendasi Untuk Anda",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                recommendedBooks.forEach { book ->
                    BookCardListItem(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Space for FAB and bottom nav
        }
    }
}
