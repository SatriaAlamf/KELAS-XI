package com.pinjaminperpus.mobile_android.data

import com.pinjaminperpus.mobile_android.data.model.*

/**
 * Mock data repository for the PinjamIn Perpus app
 * Contains all dummy data for books, users, borrowings, notifications, etc.
 */
object MockData {

    // ===== CATEGORIES =====
    val categories = listOf(
        Category("1", "Semua", "auto_stories"),
        Category("2", "Fiksi", "menu_book"),
        Category("3", "Sains", "science"),
        Category("4", "Sejarah", "history_edu"),
        Category("5", "Biografi", "person_book"),
        Category("6", "Komik", "comic_bubble"),
        Category("7", "Teknologi", "computer"),
        Category("8", "Bisnis", "trending_up"),
        Category("9", "Self-Help", "psychology"),
        Category("10", "Anak-anak", "child_care")
    )

    // ===== BANNERS =====
    val banners = listOf(
        Banner(
            id = "1",
            title = "Jam Buka Baru\nPerpustakaan",
            subtitle = "Senin-Jumat: 08:00 - 20:00",
            tag = "NEWS",
            tagColor = "primary"
        ),
        Banner(
            id = "2",
            title = "Koleksi Novel\nTerpopuler 2025",
            subtitle = "Temukan buku favoritmu",
            tag = "FEATURED",
            tagColor = "secondary"
        ),
        Banner(
            id = "3",
            title = "Gratis Denda\nBulan Januari",
            subtitle = "Promo khusus member baru",
            tag = "PROMO",
            tagColor = "tertiary"
        )
    )

    // ===== BOOKS =====
    val books = listOf(
        Book(
            id = "1",
            title = "Laskar Pelangi",
            author = "Andrea Hirata",
            category = "Fiksi",
            isbn = "978-602-8519-67-7",
            description = "Laskar Pelangi adalah novel pertama karya Andrea Hirata yang diterbitkan oleh Bentang Pustaka pada tahun 2005. Novel ini bercerita tentang kehidupan 10 anak dari keluarga miskin yang bersekolah (SD dan SMP) di sebuah sekolah Muhammadiyah di Belitung yang penuh dengan keterbatasan.\n\nMereka bersekolah dan belajar pada kelas yang sama dari kelas 1 SD sampai kelas 3 SMP, dan menyebut diri mereka sebagai Laskar Pelangi. Pada bagian-bagian akhir cerita, anggota Laskar Pelangi bertambah satu anak perempuan yang bernama Flo, seorang murid pindahan.\n\nKeterbatasan yang ada bukan membuat mereka putus asa, tetapi malah membuat mereka terpacu untuk dapat melakukan sesuatu yang lebih baik. Cerita ini mengajarkan tentang semangat, persahabatan, dan cinta.",
            rating = 4.8f,
            reviewCount = 1204,
            totalStock = 5,
            availableStock = 3,
            pages = 529,
            language = "Indonesia",
            year = 2005
        ),
        Book(
            id = "2",
            title = "Atomic Habits",
            author = "James Clear",
            category = "Self-Help",
            isbn = "978-0-7352-1131-3",
            description = "Atomic Habits adalah buku panduan praktis yang akan mengajarkan Anda cara membentuk kebiasaan baik dan menghilangkan kebiasaan buruk. James Clear menjelaskan bagaimana perubahan kecil dapat membawa hasil yang luar biasa.\n\nBuku ini menggabungkan penelitian ilmiah dengan kisah-kisah inspiratif dari atlet, seniman, dan pemimpin bisnis yang telah menggunakan metode ini untuk mencapai kesuksesan.",
            rating = 4.9f,
            reviewCount = 3420,
            totalStock = 4,
            availableStock = 1,
            pages = 320,
            language = "Indonesia",
            year = 2018
        ),
        Book(
            id = "3",
            title = "Laut Bercerita",
            author = "Leila S. Chudori",
            category = "Fiksi",
            isbn = "978-602-291-511-7",
            description = "Laut Bercerita adalah novel karya Leila S. Chudori yang menceritakan tentang aktivis mahasiswa yang hilang pada masa Orde Baru dan dampaknya terhadap keluarga yang ditinggalkan.",
            rating = 4.7f,
            reviewCount = 890,
            totalStock = 3,
            availableStock = 0,
            pages = 394,
            language = "Indonesia",
            year = 2017
        ),
        Book(
            id = "4",
            title = "Sapiens: A Brief History of Humankind",
            author = "Yuval Noah Harari",
            category = "Sejarah",
            isbn = "978-0-06-231609-7",
            description = "Sapiens menceritakan sejarah umat manusia dari Zaman Batu hingga era modern. Yuval Noah Harari mengeksplorasi bagaimana Homo sapiens mendominasi Bumi dan membangun peradaban.",
            rating = 4.8f,
            reviewCount = 2156,
            totalStock = 6,
            availableStock = 4,
            pages = 498,
            language = "Indonesia",
            year = 2011
        ),
        Book(
            id = "5",
            title = "Filosofi Teras",
            author = "Henry Manampiring",
            category = "Self-Help",
            isbn = "978-602-291-613-8",
            description = "Filosofi Teras membahas filsafat Stoa (Stoicism) yang dapat membantu menghadapi tantangan hidup dengan lebih tenang dan bijaksana. Buku ini menjelaskan konsep-konsep stoikisme dengan bahasa yang mudah dipahami.",
            rating = 4.6f,
            reviewCount = 1567,
            totalStock = 5,
            availableStock = 5,
            pages = 346,
            language = "Indonesia",
            year = 2018
        ),
        Book(
            id = "6",
            title = "The Midnight Library",
            author = "Matt Haig",
            category = "Fiksi",
            isbn = "978-0-525-55947-4",
            description = "The Midnight Library bercerita tentang Nora Seed yang menemukan dirinya di sebuah perpustakaan ajaib di antara hidup dan mati. Setiap buku berisi kemungkinan hidup berbeda yang bisa ia jalani.",
            rating = 4.8f,
            reviewCount = 1200,
            totalStock = 4,
            availableStock = 2,
            pages = 304,
            language = "Indonesia",
            year = 2020
        ),
        Book(
            id = "7",
            title = "Psychology of Money",
            author = "Morgan Housel",
            category = "Bisnis",
            isbn = "978-0-85719-768-0",
            description = "Psychology of Money mengeksplorasi hubungan emosional manusia dengan uang. Morgan Housel menyajikan 19 cerita pendek yang mengungkap cara berpikir tentang uang dan kekayaan.",
            rating = 4.9f,
            reviewCount = 3400,
            totalStock = 5,
            availableStock = 3,
            pages = 256,
            language = "Indonesia",
            year = 2020
        ),
        Book(
            id = "8",
            title = "Steve Jobs",
            author = "Walter Isaacson",
            category = "Biografi",
            isbn = "978-1-4516-4853-9",
            description = "Biografi resmi Steve Jobs yang ditulis berdasarkan lebih dari 40 wawancara dengan Jobs sendiri, serta wawancara dengan keluarga, teman, pesaing, dan rekan kerja.",
            rating = 4.7f,
            reviewCount = 2890,
            totalStock = 3,
            availableStock = 0,
            pages = 656,
            language = "Indonesia",
            year = 2011
        ),
        Book(
            id = "9",
            title = "Design of Everyday Things",
            author = "Don Norman",
            category = "Teknologi",
            isbn = "978-0-465-05065-9",
            description = "The Design of Everyday Things adalah buku klasik tentang desain yang berpusat pada pengguna. Don Norman menjelaskan prinsip-prinsip desain yang baik dan bagaimana menerapkannya.",
            rating = 4.5f,
            reviewCount = 1890,
            totalStock = 4,
            availableStock = 2,
            pages = 368,
            language = "Indonesia",
            year = 2013
        ),
        Book(
            id = "10",
            title = "Bumi",
            author = "Tere Liye",
            category = "Fiksi",
            isbn = "978-602-291-259-8",
            description = "Bumi adalah novel fantasi pertama dari serial Bumi karya Tere Liye. Cerita mengikuti petualangan Raib, gadis berusia 15 tahun yang menemukan bahwa ia memiliki kekuatan khusus.",
            rating = 4.6f,
            reviewCount = 2345,
            totalStock = 6,
            availableStock = 4,
            pages = 440,
            language = "Indonesia",
            year = 2014
        ),
        Book(
            id = "11",
            title = "Thinking, Fast and Slow",
            author = "Daniel Kahneman",
            category = "Sains",
            isbn = "978-0-374-53355-7",
            description = "Daniel Kahneman menjelaskan dua sistem dalam otak manusia: Sistem 1 yang cepat dan intuitif, dan Sistem 2 yang lambat dan analitis. Buku ini membantu memahami bagaimana kita berpikir dan membuat keputusan.",
            rating = 4.7f,
            reviewCount = 1678,
            totalStock = 3,
            availableStock = 1,
            pages = 512,
            language = "Indonesia",
            year = 2011
        ),
        Book(
            id = "12",
            title = "Rich Dad Poor Dad",
            author = "Robert Kiyosaki",
            category = "Bisnis",
            isbn = "978-1-61268-017-1",
            description = "Rich Dad Poor Dad menceritakan pelajaran keuangan yang dipelajari Robert Kiyosaki dari dua figur ayah yang berbeda - ayah kandungnya (Poor Dad) dan ayah temannya (Rich Dad).",
            rating = 4.5f,
            reviewCount = 4567,
            totalStock = 5,
            availableStock = 3,
            pages = 336,
            language = "Indonesia",
            year = 1997
        ),
        Book(
            id = "13",
            title = "Pulang",
            author = "Tere Liye",
            category = "Fiksi",
            isbn = "978-602-0822-28-0",
            description = "Pulang bercerita tentang Bujang, anak dari keluarga petani sederhana yang terlibat dalam dunia perkelahian dan akhirnya menjadi bagian dari organisasi besar.",
            rating = 4.7f,
            reviewCount = 1890,
            totalStock = 4,
            availableStock = 2,
            pages = 400,
            language = "Indonesia",
            year = 2015
        ),
        Book(
            id = "14",
            title = "Educated",
            author = "Tara Westover",
            category = "Biografi",
            isbn = "978-0-399-59050-4",
            description = "Educated adalah memoar Tara Westover tentang perjalanannya dari keluarga Mormon yang ekstrem di Idaho hingga meraih PhD dari Cambridge University.",
            rating = 4.8f,
            reviewCount = 2345,
            totalStock = 3,
            availableStock = 1,
            pages = 352,
            language = "Indonesia",
            year = 2018
        ),
        Book(
            id = "15",
            title = "Homo Deus",
            author = "Yuval Noah Harari",
            category = "Sains",
            isbn = "978-0-06-246431-6",
            description = "Homo Deus mengeksplorasi masa depan umat manusia, membahas bagaimana teknologi dan sains mungkin mengubah masyarakat dan apa artinya menjadi manusia.",
            rating = 4.6f,
            reviewCount = 1567,
            totalStock = 4,
            availableStock = 2,
            pages = 450,
            language = "Indonesia",
            year = 2015
        )
    )

    // ===== CURRENT USER =====
    val currentUser = User(
        id = "user-001",
        name = "Rina Pustaka",
        email = "rina.pustaka@email.com",
        phone = "081234567890",
        membershipId = "2023-LIB-8891",
        totalBorrowed = 42,
        currentlyBorrowing = 3,
        readingPoints = 1250
    )

    // ===== ACTIVE BORROWINGS =====
    val activeBorrowings = listOf(
        Borrowing(
            id = "borrow-001",
            book = books[1], // Atomic Habits
            borrowDate = "25 Des 2025",
            dueDate = "8 Jan 2026",
            isActive = true,
            daysRemaining = 1,
            isOverdue = false
        ),
        Borrowing(
            id = "borrow-002",
            book = books[2], // Laut Bercerita
            borrowDate = "28 Des 2025",
            dueDate = "14 Jan 2026",
            isActive = true,
            daysRemaining = 7,
            isOverdue = false
        ),
        Borrowing(
            id = "borrow-003",
            book = books[8], // Design of Everyday Things
            borrowDate = "30 Des 2025",
            dueDate = "20 Jan 2026",
            isActive = true,
            daysRemaining = 13,
            isOverdue = false
        )
    )

    // ===== COMPLETED BORROWINGS =====
    val completedBorrowings = listOf(
        Borrowing(
            id = "borrow-101",
            book = books[0], // Laskar Pelangi
            borrowDate = "1 Des 2025",
            dueDate = "15 Des 2025",
            returnDate = "14 Des 2025",
            isActive = false,
            fine = 0
        ),
        Borrowing(
            id = "borrow-102",
            book = books[4], // Filosofi Teras
            borrowDate = "15 Nov 2025",
            dueDate = "29 Nov 2025",
            returnDate = "2 Des 2025",
            isActive = false,
            fine = 3000
        ),
        Borrowing(
            id = "borrow-103",
            book = books[6], // Psychology of Money
            borrowDate = "1 Nov 2025",
            dueDate = "15 Nov 2025",
            returnDate = "15 Nov 2025",
            isActive = false,
            fine = 0
        )
    )

    // ===== REVIEWS =====
    val reviews = listOf(
        Review(
            id = "review-001",
            bookId = "1",
            userName = "Rina Wulandari",
            userInitials = "RW",
            rating = 5,
            comment = "Novel legendaris yang tidak pernah bosan dibaca ulang. Sangat menginspirasi semangat belajar anak-anak Indonesia. Terima kasih perpus!",
            date = "2 jam yang lalu",
            likes = 12
        ),
        Review(
            id = "review-002",
            bookId = "1",
            userName = "Dimas Anggara",
            userInitials = "DA",
            rating = 4,
            comment = "Cerita yang menyentuh hati. Mengingatkan masa-masa sekolah dulu. Wajib baca untuk semua kalangan!",
            date = "Kemarin",
            likes = 8
        ),
        Review(
            id = "review-003",
            bookId = "1",
            userName = "Siti Aminah",
            userInitials = "SA",
            rating = 5,
            comment = "Buku ini mengajarkan tentang arti persahabatan dan perjuangan. Andrea Hirata memang luar biasa!",
            date = "3 hari yang lalu",
            likes = 15
        ),
        Review(
            id = "review-004",
            bookId = "2",
            userName = "Budi Santoso",
            userInitials = "BS",
            rating = 5,
            comment = "Buku yang mengubah cara pandang saya tentang kebiasaan. Sangat praktis dan mudah diterapkan.",
            date = "1 minggu yang lalu",
            likes = 24
        )
    )

    // ===== NOTIFICATIONS =====
    val notifications = listOf(
        Notification(
            id = "notif-001",
            title = "Buku Jatuh Tempo",
            message = "Atomic Habits harus dikembalikan besok. Perpanjang sekarang jika perlu.",
            type = NotificationType.DUE_REMINDER,
            timestamp = "2 jam yang lalu",
            isRead = false,
            bookTitle = "Atomic Habits",
            actionLabel = "Perpanjang Peminjaman"
        ),
        Notification(
            id = "notif-002",
            title = "Peminjaman Berhasil",
            message = "Anda telah berhasil meminjam Sapiens. Selamat membaca!",
            type = NotificationType.BORROW_SUCCESS,
            timestamp = "5 jam yang lalu",
            isRead = false,
            bookTitle = "Sapiens"
        ),
        Notification(
            id = "notif-003",
            title = "Buku Baru Tersedia",
            message = "The Psychology of Money sekarang tersedia di perpustakaan. Jadilah yang pertama membaca!",
            type = NotificationType.NEW_BOOK,
            timestamp = "Kemarin",
            isRead = true,
            bookTitle = "The Psychology of Money"
        ),
        Notification(
            id = "notif-004",
            title = "Pengembalian Berhasil",
            message = "Terima kasih telah mengembalikan Laskar Pelangi tepat waktu. +50 poin baca!",
            type = NotificationType.RETURN_SUCCESS,
            timestamp = "3 hari yang lalu",
            isRead = true,
            bookTitle = "Laskar Pelangi"
        ),
        Notification(
            id = "notif-005",
            title = "Promo Gratis Denda",
            message = "Nikmati bebas denda selama bulan Januari untuk semua pengembalian terlambat.",
            type = NotificationType.PROMO,
            timestamp = "1 minggu yang lalu",
            isRead = true
        )
    )

    // ===== HELPER FUNCTIONS =====
    
    fun getBookById(id: String): Book? = books.find { it.id == id }
    
    fun getBooksByCategory(category: String): List<Book> {
        return if (category == "Semua") books else books.filter { it.category == category }
    }
    
    fun getAvailableBooks(): List<Book> = books.filter { it.isAvailable }
    
    fun getBorrowedBooks(): List<Book> = books.filter { !it.isAvailable }
    
    fun getPopularBooks(): List<Book> = books.sortedByDescending { it.reviewCount }.take(6)
    
    fun getRecommendedBooks(): List<Book> = books.sortedByDescending { it.rating }.take(4)
    
    fun getReviewsForBook(bookId: String): List<Review> = reviews.filter { it.bookId == bookId }
    
    fun getUnreadNotificationsCount(): Int = notifications.count { !it.isRead }
    
    fun searchBooks(query: String): List<Book> {
        val lowercaseQuery = query.lowercase()
        return books.filter { 
            it.title.lowercase().contains(lowercaseQuery) ||
            it.author.lowercase().contains(lowercaseQuery) ||
            it.category.lowercase().contains(lowercaseQuery) ||
            it.isbn.contains(query)
        }
    }
}
