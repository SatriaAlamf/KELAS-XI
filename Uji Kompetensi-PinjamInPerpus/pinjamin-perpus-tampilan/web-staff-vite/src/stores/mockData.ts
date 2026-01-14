import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Book, Member, Borrowing, BookCategory, MemberType, BookFormData, MemberFormData, BorrowingStatus, DashboardStats, BorrowingTrend, ChartDataPoint } from '@/types'
import { format, subDays, addDays, isAfter, parseISO } from 'date-fns'

// Mock Book Covers
const bookCovers = [
  'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=200&h=300&fit=crop',
  'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=200&h=300&fit=crop',
  'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=200&h=300&fit=crop',
  'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=200&h=300&fit=crop',
  'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=200&h=300&fit=crop',
]

// Mock Data Generator
const generateBooks = (): Book[] => {
  const books: Book[] = [
    { id: 1, title: 'Laskar Pelangi', author: 'Andrea Hirata', publisher: 'Bentang Pustaka', publishYear: 2005, isbn: '978-979-1227-00-1', category: 'Fiksi', stock: 5, borrowed: 2, description: 'Novel tentang perjuangan anak-anak Belitung dalam meraih pendidikan.', cover: bookCovers[0], createdAt: '2024-01-15' },
    { id: 2, title: 'Bumi Manusia', author: 'Pramoedya Ananta Toer', publisher: 'Hasta Mitra', publishYear: 1980, isbn: '978-979-9731-00-0', category: 'Sastra', stock: 3, borrowed: 1, description: 'Novel sejarah yang mengisahkan kehidupan di era kolonial Belanda.', cover: bookCovers[1], createdAt: '2024-02-10' },
    { id: 3, title: 'Pulang', author: 'Tere Liye', publisher: 'Republika', publishYear: 2015, isbn: '978-602-0851-16-9', category: 'Fiksi', stock: 4, borrowed: 3, description: 'Novel tentang perjalanan hidup dan pencarian jati diri.', cover: bookCovers[2], createdAt: '2024-03-05' },
    { id: 4, title: 'Filosofi Teras', author: 'Henry Manampiring', publisher: 'Kompas', publishYear: 2018, isbn: '978-602-412-503-5', category: 'Non-Fiksi', stock: 6, borrowed: 2, description: 'Buku tentang filsafat Stoisisme untuk kehidupan modern.', cover: bookCovers[3], createdAt: '2024-03-20' },
    { id: 5, title: 'Sapiens', author: 'Yuval Noah Harari', publisher: 'Gramedia', publishYear: 2014, isbn: '978-602-03-3026-5', category: 'Sejarah', stock: 4, borrowed: 1, description: 'Sejarah singkat umat manusia dari masa prasejarah hingga modern.', cover: bookCovers[4], createdAt: '2024-04-01' },
    { id: 6, title: 'Atomic Habits', author: 'James Clear', publisher: 'Gramedia', publishYear: 2018, isbn: '978-602-06-1865-3', category: 'Non-Fiksi', stock: 5, borrowed: 4, description: 'Cara mudah dan terbukti untuk membangun kebiasaan baik.', cover: bookCovers[0], createdAt: '2024-04-15' },
    { id: 7, title: 'Dilan 1990', author: 'Pidi Baiq', publisher: 'Pastel Books', publishYear: 2014, isbn: '978-602-291-001-3', category: 'Fiksi', stock: 3, borrowed: 2, description: 'Novel romantis remaja yang berlatar di Bandung tahun 1990-an.', cover: bookCovers[1], createdAt: '2024-05-01' },
    { id: 8, title: 'Fisika Dasar', author: 'Halliday & Resnick', publisher: 'Erlangga', publishYear: 2010, isbn: '978-979-099-123-4', category: 'Sains', stock: 8, borrowed: 3, description: 'Buku teks fisika dasar untuk mahasiswa.', cover: bookCovers[2], createdAt: '2024-05-15' },
    { id: 9, title: 'Kamus Besar Bahasa Indonesia', author: 'Tim Penyusun', publisher: 'Balai Pustaka', publishYear: 2016, isbn: '978-979-407-550-2', category: 'Referensi', stock: 10, borrowed: 0, description: 'Kamus standar bahasa Indonesia.', cover: bookCovers[3], createdAt: '2024-06-01' },
    { id: 10, title: 'Clean Code', author: 'Robert C. Martin', publisher: 'Prentice Hall', publishYear: 2008, isbn: '978-013-235088-4', category: 'Teknologi', stock: 4, borrowed: 2, description: 'Panduan menulis kode yang bersih dan mudah dipelihara.', cover: bookCovers[4], createdAt: '2024-06-15' },
    { id: 11, title: 'Negeri 5 Menara', author: 'Ahmad Fuadi', publisher: 'Gramedia', publishYear: 2009, isbn: '978-979-22-4601-1', category: 'Fiksi', stock: 5, borrowed: 1, description: 'Novel tentang kehidupan santri di pesantren.', cover: bookCovers[0], createdAt: '2024-07-01' },
    { id: 12, title: 'Perahu Kertas', author: 'Dee Lestari', publisher: 'Bentang Pustaka', publishYear: 2009, isbn: '978-979-1227-80-3', category: 'Fiksi', stock: 4, borrowed: 2, description: 'Novel tentang seni, cinta, dan mimpi.', cover: bookCovers[1], createdAt: '2024-07-15' },
    { id: 13, title: 'Algoritma dan Pemrograman', author: 'Rinaldi Munir', publisher: 'Informatika', publishYear: 2016, isbn: '978-602-1514-94-0', category: 'Teknologi', stock: 6, borrowed: 4, description: 'Buku teks pemrograman dasar.', cover: bookCovers[2], createdAt: '2024-08-01' },
    { id: 14, title: 'Sejarah Indonesia Modern', author: 'M.C. Ricklefs', publisher: 'Serambi', publishYear: 2005, isbn: '978-979-1303-00-8', category: 'Sejarah', stock: 3, borrowed: 1, description: 'Sejarah Indonesia dari masa kolonial hingga modern.', cover: bookCovers[3], createdAt: '2024-08-15' },
    { id: 15, title: 'Psikologi Pendidikan', author: 'John Santrock', publisher: 'Salemba', publishYear: 2017, isbn: '978-602-0851-99-2', category: 'Pendidikan', stock: 5, borrowed: 2, description: 'Buku teks psikologi untuk pendidik.', cover: bookCovers[4], createdAt: '2024-09-01' },
    { id: 16, title: 'Homo Deus', author: 'Yuval Noah Harari', publisher: 'Gramedia', publishYear: 2017, isbn: '978-602-03-6755-1', category: 'Sejarah', stock: 4, borrowed: 2, description: 'Masa depan umat manusia dan evolusi Homo Sapiens.', cover: bookCovers[0], createdAt: '2024-09-15' },
    { id: 17, title: 'Ayat-Ayat Cinta', author: 'Habiburrahman El Shirazy', publisher: 'Republika', publishYear: 2004, isbn: '978-979-3210-00-9', category: 'Fiksi', stock: 5, borrowed: 3, description: 'Novel religius berlatar Mesir.', cover: bookCovers[1], createdAt: '2024-10-01' },
    { id: 18, title: 'The Art of War', author: 'Sun Tzu', publisher: 'Gramedia', publishYear: 2015, isbn: '978-602-03-1234-5', category: 'Non-Fiksi', stock: 3, borrowed: 1, description: 'Strategi perang klasik dari Tiongkok kuno.', cover: bookCovers[2], createdAt: '2024-10-15' },
    { id: 19, title: 'Biologi Campbell', author: 'Neil Campbell', publisher: 'Erlangga', publishYear: 2018, isbn: '978-979-099-456-3', category: 'Sains', stock: 6, borrowed: 2, description: 'Buku teks biologi standar internasional.', cover: bookCovers[3], createdAt: '2024-11-01' },
    { id: 20, title: 'Ensiklopedia Indonesia', author: 'Tim Penyusun', publisher: 'Ichtiar Baru', publishYear: 2012, isbn: '978-979-616-000-1', category: 'Referensi', stock: 5, borrowed: 0, description: 'Ensiklopedia umum tentang Indonesia.', cover: bookCovers[4], createdAt: '2024-11-15' },
  ]
  return books
}

const generateMembers = (): Member[] => {
  const members: Member[] = [
    { id: 1, name: 'Budi Santoso', email: 'budi.santoso@email.com', phone: '081234567801', address: 'Jl. Merdeka No. 1, Jakarta', memberID: 'M001', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Budi+Santoso&background=random', createdAt: '2024-01-10' },
    { id: 2, name: 'Siti Rahayu', email: 'siti.rahayu@email.com', phone: '081234567802', address: 'Jl. Sudirman No. 25, Jakarta', memberID: 'M002', type: 'Mahasiswa', status: 'active', fines: 5000, photo: 'https://ui-avatars.com/api/?name=Siti+Rahayu&background=random', createdAt: '2024-01-15' },
    { id: 3, name: 'Ahmad Hidayat', email: 'ahmad.hidayat@email.com', phone: '081234567803', address: 'Jl. Gatot Subroto No. 10, Bandung', memberID: 'M003', type: 'Dosen', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Ahmad+Hidayat&background=random', createdAt: '2024-02-01' },
    { id: 4, name: 'Dewi Lestari', email: 'dewi.lestari@email.com', phone: '081234567804', address: 'Jl. Asia Afrika No. 50, Bandung', memberID: 'M004', type: 'Umum', status: 'active', fines: 3000, photo: 'https://ui-avatars.com/api/?name=Dewi+Lestari&background=random', createdAt: '2024-02-15' },
    { id: 5, name: 'Rudi Hermawan', email: 'rudi.hermawan@email.com', phone: '081234567805', address: 'Jl. Diponegoro No. 15, Surabaya', memberID: 'M005', type: 'Siswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Rudi+Hermawan&background=random', createdAt: '2024-03-01' },
    { id: 6, name: 'Ani Wijaya', email: 'ani.wijaya@email.com', phone: '081234567806', address: 'Jl. Pemuda No. 30, Surabaya', memberID: 'M006', type: 'Mahasiswa', status: 'blocked', fines: 25000, photo: 'https://ui-avatars.com/api/?name=Ani+Wijaya&background=random', createdAt: '2024-03-15' },
    { id: 7, name: 'Joko Prasetyo', email: 'joko.prasetyo@email.com', phone: '081234567807', address: 'Jl. Pahlawan No. 5, Semarang', memberID: 'M007', type: 'Umum', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Joko+Prasetyo&background=random', createdAt: '2024-04-01' },
    { id: 8, name: 'Maya Sari', email: 'maya.sari@email.com', phone: '081234567808', address: 'Jl. Veteran No. 20, Yogyakarta', memberID: 'M008', type: 'Mahasiswa', status: 'active', fines: 2000, photo: 'https://ui-avatars.com/api/?name=Maya+Sari&background=random', createdAt: '2024-04-15' },
    { id: 9, name: 'Eko Susanto', email: 'eko.susanto@email.com', phone: '081234567809', address: 'Jl. Malioboro No. 100, Yogyakarta', memberID: 'M009', type: 'Dosen', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Eko+Susanto&background=random', createdAt: '2024-05-01' },
    { id: 10, name: 'Rina Kusuma', email: 'rina.kusuma@email.com', phone: '081234567810', address: 'Jl. Imam Bonjol No. 45, Medan', memberID: 'M010', type: 'Siswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Rina+Kusuma&background=random', createdAt: '2024-05-15' },
    { id: 11, name: 'Hendra Gunawan', email: 'hendra.gunawan@email.com', phone: '081234567811', address: 'Jl. Ahmad Yani No. 60, Medan', memberID: 'M011', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Hendra+Gunawan&background=random', createdAt: '2024-06-01' },
    { id: 12, name: 'Fitri Handayani', email: 'fitri.handayani@email.com', phone: '081234567812', address: 'Jl. Sisingamangaraja No. 80, Makassar', memberID: 'M012', type: 'Umum', status: 'active', fines: 7000, photo: 'https://ui-avatars.com/api/?name=Fitri+Handayani&background=random', createdAt: '2024-06-15' },
    { id: 13, name: 'Agus Setiawan', email: 'agus.setiawan@email.com', phone: '081234567813', address: 'Jl. Hasanuddin No. 35, Makassar', memberID: 'M013', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Agus+Setiawan&background=random', createdAt: '2024-07-01' },
    { id: 14, name: 'Nur Aini', email: 'nur.aini@email.com', phone: '081234567814', address: 'Jl. Gajah Mada No. 70, Palembang', memberID: 'M014', type: 'Siswa', status: 'blocked', fines: 15000, photo: 'https://ui-avatars.com/api/?name=Nur+Aini&background=random', createdAt: '2024-07-15' },
    { id: 15, name: 'Bambang Wibowo', email: 'bambang.wibowo@email.com', phone: '081234567815', address: 'Jl. Kartini No. 40, Palembang', memberID: 'M015', type: 'Dosen', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Bambang+Wibowo&background=random', createdAt: '2024-08-01' },
    { id: 16, name: 'Citra Dewi', email: 'citra.dewi@email.com', phone: '081234567816', address: 'Jl. Thamrin No. 55, Denpasar', memberID: 'M016', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Citra+Dewi&background=random', createdAt: '2024-08-15' },
    { id: 17, name: 'Dani Pratama', email: 'dani.pratama@email.com', phone: '081234567817', address: 'Jl. Sunset Road No. 90, Denpasar', memberID: 'M017', type: 'Umum', status: 'active', fines: 4000, photo: 'https://ui-avatars.com/api/?name=Dani+Pratama&background=random', createdAt: '2024-09-01' },
    { id: 18, name: 'Eka Putri', email: 'eka.putri@email.com', phone: '081234567818', address: 'Jl. Teuku Umar No. 25, Balikpapan', memberID: 'M018', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Eka+Putri&background=random', createdAt: '2024-09-15' },
    { id: 19, name: 'Fajar Nugroho', email: 'fajar.nugroho@email.com', phone: '081234567819', address: 'Jl. MT Haryono No. 65, Balikpapan', memberID: 'M019', type: 'Siswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Fajar+Nugroho&background=random', createdAt: '2024-10-01' },
    { id: 20, name: 'Gita Safitri', email: 'gita.safitri@email.com', phone: '081234567820', address: 'Jl. Pattimura No. 15, Manado', memberID: 'M020', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Gita+Safitri&background=random', createdAt: '2024-10-15' },
    { id: 21, name: 'Irfan Ramadhan', email: 'irfan.ramadhan@email.com', phone: '081234567821', address: 'Jl. Sam Ratulangi No. 50, Manado', memberID: 'M021', type: 'Umum', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Irfan+Ramadhan&background=random', createdAt: '2024-11-01' },
    { id: 22, name: 'Julia Hartono', email: 'julia.hartono@email.com', phone: '081234567822', address: 'Jl. Diponegoro No. 85, Pontianak', memberID: 'M022', type: 'Mahasiswa', status: 'active', fines: 1000, photo: 'https://ui-avatars.com/api/?name=Julia+Hartono&background=random', createdAt: '2024-11-15' },
    { id: 23, name: 'Kevin Tan', email: 'kevin.tan@email.com', phone: '081234567823', address: 'Jl. Gajah Mada No. 120, Pontianak', memberID: 'M023', type: 'Dosen', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Kevin+Tan&background=random', createdAt: '2024-12-01' },
    { id: 24, name: 'Linda Sari', email: 'linda.sari@email.com', phone: '081234567824', address: 'Jl. Sudirman No. 40, Banjarmasin', memberID: 'M024', type: 'Siswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Linda+Sari&background=random', createdAt: '2024-12-15' },
    { id: 25, name: 'Muhammad Rizky', email: 'muhammad.rizky@email.com', phone: '081234567825', address: 'Jl. Ahmad Yani No. 75, Banjarmasin', memberID: 'M025', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Muhammad+Rizky&background=random', createdAt: '2025-01-01' },
    { id: 26, name: 'Nadia Putri', email: 'nadia.putri@email.com', phone: '081234567826', address: 'Jl. Jend Sudirman No. 30, Pekanbaru', memberID: 'M026', type: 'Umum', status: 'active', fines: 8000, photo: 'https://ui-avatars.com/api/?name=Nadia+Putri&background=random', createdAt: '2025-01-05' },
    { id: 27, name: 'Oscar Wijaya', email: 'oscar.wijaya@email.com', phone: '081234567827', address: 'Jl. Nangka No. 55, Pekanbaru', memberID: 'M027', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Oscar+Wijaya&background=random', createdAt: '2025-01-07' },
    { id: 28, name: 'Putri Ayu', email: 'putri.ayu@email.com', phone: '081234567828', address: 'Jl. Soekarno Hatta No. 80, Lampung', memberID: 'M028', type: 'Siswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Putri+Ayu&background=random', createdAt: '2025-01-09' },
    { id: 29, name: 'Qori Amelia', email: 'qori.amelia@email.com', phone: '081234567829', address: 'Jl. Raden Intan No. 100, Lampung', memberID: 'M029', type: 'Mahasiswa', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Qori+Amelia&background=random', createdAt: '2025-01-10' },
    { id: 30, name: 'Ryan Kusuma', email: 'ryan.kusuma@email.com', phone: '081234567830', address: 'Jl. Basuki Rahmat No. 45, Malang', memberID: 'M030', type: 'Dosen', status: 'active', fines: 0, photo: 'https://ui-avatars.com/api/?name=Ryan+Kusuma&background=random', createdAt: '2025-01-11' },
  ]
  return members
}

const generateBorrowings = (): Borrowing[] => {
  const today = new Date()
  const borrowings: Borrowing[] = [
    // Active borrowings
    { id: 1, memberId: 1, bookId: 1, borrowDate: format(subDays(today, 5), 'yyyy-MM-dd'), dueDate: format(addDays(today, 2), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 2, memberId: 2, bookId: 3, borrowDate: format(subDays(today, 10), 'yyyy-MM-dd'), dueDate: format(subDays(today, 3), 'yyyy-MM-dd'), returnDate: null, status: 'overdue', fine: 3000, bookCondition: 'good' },
    { id: 3, memberId: 3, bookId: 5, borrowDate: format(subDays(today, 3), 'yyyy-MM-dd'), dueDate: format(addDays(today, 4), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 4, memberId: 4, bookId: 6, borrowDate: format(subDays(today, 8), 'yyyy-MM-dd'), dueDate: format(subDays(today, 1), 'yyyy-MM-dd'), returnDate: null, status: 'overdue', fine: 1000, bookCondition: 'good' },
    { id: 5, memberId: 5, bookId: 7, borrowDate: format(subDays(today, 2), 'yyyy-MM-dd'), dueDate: format(addDays(today, 5), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 6, memberId: 7, bookId: 10, borrowDate: format(subDays(today, 4), 'yyyy-MM-dd'), dueDate: format(addDays(today, 3), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 7, memberId: 8, bookId: 12, borrowDate: format(subDays(today, 6), 'yyyy-MM-dd'), dueDate: format(addDays(today, 1), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 8, memberId: 11, bookId: 13, borrowDate: format(subDays(today, 12), 'yyyy-MM-dd'), dueDate: format(subDays(today, 5), 'yyyy-MM-dd'), returnDate: null, status: 'overdue', fine: 5000, bookCondition: 'good' },
    { id: 9, memberId: 13, bookId: 17, borrowDate: format(subDays(today, 1), 'yyyy-MM-dd'), dueDate: format(addDays(today, 6), 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    { id: 10, memberId: 16, bookId: 4, borrowDate: format(subDays(today, 7), 'yyyy-MM-dd'), dueDate: format(today, 'yyyy-MM-dd'), returnDate: null, status: 'active', fine: 0, bookCondition: 'good' },
    // Returned borrowings
    { id: 11, memberId: 1, bookId: 2, borrowDate: format(subDays(today, 20), 'yyyy-MM-dd'), dueDate: format(subDays(today, 13), 'yyyy-MM-dd'), returnDate: format(subDays(today, 14), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 12, memberId: 2, bookId: 4, borrowDate: format(subDays(today, 25), 'yyyy-MM-dd'), dueDate: format(subDays(today, 18), 'yyyy-MM-dd'), returnDate: format(subDays(today, 16), 'yyyy-MM-dd'), status: 'returned', fine: 2000, bookCondition: 'good' },
    { id: 13, memberId: 3, bookId: 8, borrowDate: format(subDays(today, 30), 'yyyy-MM-dd'), dueDate: format(subDays(today, 23), 'yyyy-MM-dd'), returnDate: format(subDays(today, 23), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 14, memberId: 5, bookId: 1, borrowDate: format(subDays(today, 15), 'yyyy-MM-dd'), dueDate: format(subDays(today, 8), 'yyyy-MM-dd'), returnDate: format(subDays(today, 10), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 15, memberId: 6, bookId: 11, borrowDate: format(subDays(today, 40), 'yyyy-MM-dd'), dueDate: format(subDays(today, 33), 'yyyy-MM-dd'), returnDate: format(subDays(today, 30), 'yyyy-MM-dd'), status: 'returned', fine: 3000, bookCondition: 'damaged' },
    { id: 16, memberId: 8, bookId: 3, borrowDate: format(subDays(today, 35), 'yyyy-MM-dd'), dueDate: format(subDays(today, 28), 'yyyy-MM-dd'), returnDate: format(subDays(today, 28), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 17, memberId: 9, bookId: 6, borrowDate: format(subDays(today, 22), 'yyyy-MM-dd'), dueDate: format(subDays(today, 15), 'yyyy-MM-dd'), returnDate: format(subDays(today, 17), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 18, memberId: 10, bookId: 9, borrowDate: format(subDays(today, 18), 'yyyy-MM-dd'), dueDate: format(subDays(today, 11), 'yyyy-MM-dd'), returnDate: format(subDays(today, 11), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 19, memberId: 12, bookId: 14, borrowDate: format(subDays(today, 45), 'yyyy-MM-dd'), dueDate: format(subDays(today, 38), 'yyyy-MM-dd'), returnDate: format(subDays(today, 35), 'yyyy-MM-dd'), status: 'returned', fine: 3000, bookCondition: 'good' },
    { id: 20, memberId: 15, bookId: 16, borrowDate: format(subDays(today, 28), 'yyyy-MM-dd'), dueDate: format(subDays(today, 21), 'yyyy-MM-dd'), returnDate: format(subDays(today, 22), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 21, memberId: 17, bookId: 18, borrowDate: format(subDays(today, 50), 'yyyy-MM-dd'), dueDate: format(subDays(today, 43), 'yyyy-MM-dd'), returnDate: format(subDays(today, 43), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 22, memberId: 18, bookId: 19, borrowDate: format(subDays(today, 33), 'yyyy-MM-dd'), dueDate: format(subDays(today, 26), 'yyyy-MM-dd'), returnDate: format(subDays(today, 27), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 23, memberId: 20, bookId: 2, borrowDate: format(subDays(today, 38), 'yyyy-MM-dd'), dueDate: format(subDays(today, 31), 'yyyy-MM-dd'), returnDate: format(subDays(today, 29), 'yyyy-MM-dd'), status: 'returned', fine: 2000, bookCondition: 'good' },
    { id: 24, memberId: 21, bookId: 5, borrowDate: format(subDays(today, 42), 'yyyy-MM-dd'), dueDate: format(subDays(today, 35), 'yyyy-MM-dd'), returnDate: format(subDays(today, 35), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
    { id: 25, memberId: 22, bookId: 7, borrowDate: format(subDays(today, 27), 'yyyy-MM-dd'), dueDate: format(subDays(today, 20), 'yyyy-MM-dd'), returnDate: format(subDays(today, 21), 'yyyy-MM-dd'), status: 'returned', fine: 0, bookCondition: 'good' },
  ]
  return borrowings
}

export const useMockStore = defineStore('mock', () => {
  // State
  const books = ref<Book[]>(generateBooks())
  const members = ref<Member[]>(generateMembers())
  const borrowings = ref<Borrowing[]>(generateBorrowings())
  const nextBookId = ref(21)
  const nextMemberId = ref(31)
  const nextBorrowingId = ref(26)
  const nextMemberID = ref(31)

  // Book CRUD
  const addBook = (bookData: BookFormData) => {
    const newBook: Book = {
      id: nextBookId.value++,
      ...bookData,
      borrowed: 0,
      createdAt: format(new Date(), 'yyyy-MM-dd')
    }
    books.value.push(newBook)
    return newBook
  }

  const updateBook = (id: number, bookData: Partial<BookFormData>) => {
    const index = books.value.findIndex(b => b.id === id)
    if (index !== -1) {
      books.value[index] = { ...books.value[index], ...bookData }
    }
  }

  const deleteBook = (id: number) => {
    books.value = books.value.filter(b => b.id !== id)
  }

  const getBookById = (id: number) => {
    return books.value.find(b => b.id === id)
  }

  // Member CRUD
  const addMember = (memberData: MemberFormData) => {
    const newMember: Member = {
      id: nextMemberId.value++,
      ...memberData,
      memberID: `M${String(nextMemberID.value++).padStart(3, '0')}`,
      status: 'active',
      fines: 0,
      createdAt: format(new Date(), 'yyyy-MM-dd')
    }
    members.value.push(newMember)
    return newMember
  }

  const updateMember = (id: number, memberData: Partial<MemberFormData>) => {
    const index = members.value.findIndex(m => m.id === id)
    if (index !== -1) {
      members.value[index] = { ...members.value[index], ...memberData }
    }
  }

  const deleteMember = (id: number) => {
    members.value = members.value.filter(m => m.id !== id)
  }

  const getMemberById = (id: number) => {
    return members.value.find(m => m.id === id)
  }

  const toggleMemberStatus = (id: number) => {
    const member = members.value.find(m => m.id === id)
    if (member) {
      member.status = member.status === 'active' ? 'blocked' : 'active'
    }
  }

  // Borrowing operations
  const createBorrowing = (memberId: number, bookId: number) => {
    const today = new Date()
    const newBorrowing: Borrowing = {
      id: nextBorrowingId.value++,
      memberId,
      bookId,
      borrowDate: format(today, 'yyyy-MM-dd'),
      dueDate: format(addDays(today, 7), 'yyyy-MM-dd'),
      returnDate: null,
      status: 'active',
      fine: 0,
      bookCondition: 'good'
    }
    borrowings.value.push(newBorrowing)
    
    // Update book borrowed count
    const book = books.value.find(b => b.id === bookId)
    if (book) {
      book.borrowed++
    }
    
    return newBorrowing
  }

  const returnBook = (borrowingId: number, condition: 'good' | 'damaged' | 'lost', returnDate: string) => {
    const borrowing = borrowings.value.find(b => b.id === borrowingId)
    if (borrowing) {
      borrowing.returnDate = returnDate
      borrowing.status = 'returned'
      borrowing.bookCondition = condition
      
      // Calculate fine
      const dueDate = parseISO(borrowing.dueDate)
      const actualReturnDate = parseISO(returnDate)
      if (isAfter(actualReturnDate, dueDate)) {
        const daysLate = Math.ceil((actualReturnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24))
        borrowing.fine = daysLate * 1000 // Rp 1000 per day
      }
      
      if (condition === 'damaged') {
        borrowing.fine += 10000 // Additional fine for damaged books
      } else if (condition === 'lost') {
        borrowing.fine += 50000 // Additional fine for lost books
      }
      
      // Update member fines
      const member = members.value.find(m => m.id === borrowing.memberId)
      if (member) {
        member.fines += borrowing.fine
      }
      
      // Update book borrowed count
      const book = books.value.find(b => b.id === borrowing.bookId)
      if (book && book.borrowed > 0) {
        book.borrowed--
      }
    }
  }

  // Computed statistics
  const dashboardStats = computed<DashboardStats>(() => {
    const totalBooks = books.value.reduce((sum, b) => sum + b.stock, 0)
    const activeMembers = members.value.filter(m => m.status === 'active').length
    const borrowedBooks = books.value.reduce((sum, b) => sum + b.borrowed, 0)
    const totalFines = members.value.reduce((sum, m) => sum + m.fines, 0)
    
    return {
      totalBooks,
      activeMembers,
      borrowedBooks,
      totalFines,
      booksChange: 12,
      membersChange: 8,
      borrowedChange: -5,
      finesChange: 15
    }
  })

  const activeBorrowings = computed(() => {
    return borrowings.value.filter(b => b.status === 'active' || b.status === 'overdue')
  })

  const returnedBorrowings = computed(() => {
    return borrowings.value.filter(b => b.status === 'returned')
  })

  const overdueBorrowings = computed(() => {
    return borrowings.value.filter(b => b.status === 'overdue')
  })

  // Chart data
  const borrowingTrends = computed<BorrowingTrend[]>(() => {
    const trends: BorrowingTrend[] = []
    for (let i = 6; i >= 0; i--) {
      const date = format(subDays(new Date(), i), 'yyyy-MM-dd')
      const borrowed = borrowings.value.filter(b => b.borrowDate === date).length + Math.floor(Math.random() * 3)
      const returned = borrowings.value.filter(b => b.returnDate === date).length + Math.floor(Math.random() * 2)
      trends.push({ date, borrowed, returned })
    }
    return trends
  })

  const categoryDistribution = computed<ChartDataPoint[]>(() => {
    const categories: Record<string, number> = {}
    books.value.forEach(book => {
      categories[book.category] = (categories[book.category] || 0) + book.stock
    })
    return Object.entries(categories).map(([label, value]) => ({ label, value }))
  })

  const topBorrowedBooks = computed<ChartDataPoint[]>(() => {
    return [...books.value]
      .sort((a, b) => b.borrowed - a.borrowed)
      .slice(0, 5)
      .map(book => ({ label: book.title, value: book.borrowed }))
  })

  const recentTransactions = computed(() => {
    return [...borrowings.value]
      .sort((a, b) => {
        const dateA = a.returnDate || a.borrowDate
        const dateB = b.returnDate || b.borrowDate
        return new Date(dateB).getTime() - new Date(dateA).getTime()
      })
      .slice(0, 5)
      .map(b => {
        const member = getMemberById(b.memberId)
        const book = getBookById(b.bookId)
        return {
          ...b,
          memberName: member?.name || 'Unknown',
          bookTitle: book?.title || 'Unknown'
        }
      })
  })

  return {
    // State
    books,
    members,
    borrowings,
    // Book operations
    addBook,
    updateBook,
    deleteBook,
    getBookById,
    // Member operations
    addMember,
    updateMember,
    deleteMember,
    getMemberById,
    toggleMemberStatus,
    // Borrowing operations
    createBorrowing,
    returnBook,
    activeBorrowings,
    returnedBorrowings,
    overdueBorrowings,
    // Stats & Charts
    dashboardStats,
    borrowingTrends,
    categoryDistribution,
    topBorrowedBooks,
    recentTransactions
  }
})
