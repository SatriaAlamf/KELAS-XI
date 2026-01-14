// Book Types
export interface Book {
  id: number
  title: string
  author: string
  publisher: string
  publishYear: number
  isbn: string
  category: BookCategory
  stock: number
  borrowed: number
  description: string
  cover: string
  createdAt: string
}

export type BookCategory = 'Fiksi' | 'Non-Fiksi' | 'Referensi' | 'Sains' | 'Sejarah' | 'Teknologi' | 'Sastra' | 'Pendidikan'

export interface BookFormData {
  title: string
  author: string
  publisher: string
  publishYear: number
  isbn: string
  category: BookCategory
  stock: number
  description: string
  cover: string
}

// Member Types
export interface Member {
  id: number
  name: string
  email: string
  phone: string
  address: string
  memberID: string
  type: MemberType
  status: MemberStatus
  fines: number
  photo: string
  createdAt: string
}

export type MemberType = 'Siswa' | 'Mahasiswa' | 'Umum' | 'Dosen'
export type MemberStatus = 'active' | 'blocked'

export interface MemberFormData {
  name: string
  email: string
  phone: string
  address: string
  type: MemberType
  photo: string
}

// Borrowing/Transaction Types
export interface Borrowing {
  id: number
  memberId: number
  bookId: number
  borrowDate: string
  dueDate: string
  returnDate: string | null
  status: BorrowingStatus
  fine: number
  bookCondition: BookCondition
}

export type BorrowingStatus = 'active' | 'returned' | 'overdue'
export type BookCondition = 'good' | 'damaged' | 'lost'

// Staff/Auth Types
export interface Staff {
  id: number
  name: string
  email: string
  phone: string
  photo: string
  role: string
}

export interface AuthState {
  isAuthenticated: boolean
  staff: Staff | null
}

// Settings Types
export interface SystemSettings {
  borrowDuration: number
  finePerDay: number
  maxBooksPerMember: number
  emailNotifications: boolean
  maintenanceMode: boolean
}

// Dashboard Stats
export interface DashboardStats {
  totalBooks: number
  activeMembers: number
  borrowedBooks: number
  totalFines: number
  booksChange: number
  membersChange: number
  borrowedChange: number
  finesChange: number
}

// Chart Data Types
export interface ChartDataPoint {
  label: string
  value: number
}

export interface BorrowingTrend {
  date: string
  borrowed: number
  returned: number
}

// Table Types
export interface TableColumn<T> {
  key: keyof T | string
  label: string
  sortable?: boolean
  width?: string
}

// Modal Types
export interface ModalProps {
  isOpen: boolean
  title: string
  size?: 'sm' | 'md' | 'lg' | 'xl'
}

// Toast Types
export type ToastType = 'success' | 'error' | 'warning' | 'info'

// QR Code Types
export interface QRData {
  type: 'book' | 'member'
  id: number
  identifier: string
}
