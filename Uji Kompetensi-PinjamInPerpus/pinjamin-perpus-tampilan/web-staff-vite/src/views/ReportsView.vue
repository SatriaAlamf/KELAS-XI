<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMockStore } from '@/stores/mockData'
import { BaseButton, BaseCard, BaseSelect } from '@/components/common'
import { LineChart, BarChart, DoughnutChart } from '@/components/charts'
import { Download, FileSpreadsheet, FileText, Calendar, TrendingUp, Book, Users } from 'lucide-vue-next'
import { format, subDays } from 'date-fns'
import { id as localeId } from 'date-fns/locale'
import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

const mockStore = useMockStore()

// State
const selectedPeriod = ref('7days')
const isExporting = ref(false)

const periodOptions = [
  { value: '7days', label: '7 Hari Terakhir' },
  { value: '30days', label: '30 Hari Terakhir' },
  { value: '90days', label: '3 Bulan Terakhir' },
  { value: '365days', label: '1 Tahun Terakhir' }
]

// Chart Data
const borrowingTrends = computed(() => mockStore.borrowingTrends)
const categoryDistribution = computed(() => mockStore.categoryDistribution)
const topBooks = computed(() => mockStore.topBorrowedBooks)

const trendLabels = computed(() => 
  borrowingTrends.value.map(t => format(new Date(t.date), 'dd MMM', { locale: localeId }))
)

const trendDatasets = computed(() => [
  {
    label: 'Peminjaman',
    data: borrowingTrends.value.map(t => t.borrowed),
    borderColor: '#10b981',
    backgroundColor: 'rgba(16, 185, 129, 0.1)'
  },
  {
    label: 'Pengembalian',
    data: borrowingTrends.value.map(t => t.returned),
    borderColor: '#3b82f6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)'
  }
])

const categoryLabels = computed(() => categoryDistribution.value.map(c => c.label))
const categoryData = computed(() => categoryDistribution.value.map(c => c.value))

const topBookLabels = computed(() => topBooks.value.map(b => b.label.length > 20 ? b.label.substring(0, 20) + '...' : b.label))
const topBookData = computed(() => topBooks.value.map(b => b.value))

// Top Lists
const topBorrowedBooks = computed(() => {
  return [...mockStore.books]
    .sort((a, b) => b.borrowed - a.borrowed)
    .slice(0, 10)
})

const topActiveMembers = computed(() => {
  const memberBorrowCounts = new Map<number, number>()
  
  mockStore.borrowings.forEach(b => {
    const count = memberBorrowCounts.get(b.memberId) || 0
    memberBorrowCounts.set(b.memberId, count + 1)
  })
  
  return [...memberBorrowCounts.entries()]
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
    .map(([memberId, count]) => ({
      member: mockStore.getMemberById(memberId),
      count
    }))
    .filter(item => item.member)
})

// Export Functions
const exportBorrowingsExcel = async () => {
  isExporting.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  const data = mockStore.borrowings.map(b => ({
    'ID': b.id,
    'Member': mockStore.getMemberById(b.memberId)?.name || '',
    'Buku': mockStore.getBookById(b.bookId)?.title || '',
    'Tanggal Pinjam': format(new Date(b.borrowDate), 'dd/MM/yyyy'),
    'Jatuh Tempo': format(new Date(b.dueDate), 'dd/MM/yyyy'),
    'Tanggal Kembali': b.returnDate ? format(new Date(b.returnDate), 'dd/MM/yyyy') : '-',
    'Status': b.status === 'active' ? 'Aktif' : b.status === 'returned' ? 'Dikembalikan' : 'Telat',
    'Denda (Rp)': b.fine
  }))
  
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Peminjaman')
  XLSX.writeFile(wb, `Laporan_Peminjaman_${format(new Date(), 'yyyy-MM-dd')}.xlsx`)
  
  isExporting.value = false
}

const exportBooksExcel = async () => {
  isExporting.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  const data = mockStore.books.map(b => ({
    'ID': b.id,
    'Judul': b.title,
    'Penulis': b.author,
    'Penerbit': b.publisher,
    'Tahun': b.publishYear,
    'ISBN': b.isbn,
    'Kategori': b.category,
    'Total Stok': b.stock,
    'Dipinjam': b.borrowed,
    'Tersedia': b.stock - b.borrowed
  }))
  
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Buku')
  XLSX.writeFile(wb, `Data_Buku_${format(new Date(), 'yyyy-MM-dd')}.xlsx`)
  
  isExporting.value = false
}

const exportMembersExcel = async () => {
  isExporting.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  const data = mockStore.members.map(m => ({
    'ID': m.id,
    'ID Member': m.memberID,
    'Nama': m.name,
    'Email': m.email,
    'No. HP': m.phone,
    'Alamat': m.address,
    'Tipe': m.type,
    'Status': m.status === 'active' ? 'Aktif' : 'Diblokir',
    'Tunggakan (Rp)': m.fines,
    'Terdaftar': format(new Date(m.createdAt), 'dd/MM/yyyy')
  }))
  
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Member')
  XLSX.writeFile(wb, `Data_Member_${format(new Date(), 'yyyy-MM-dd')}.xlsx`)
  
  isExporting.value = false
}

const exportBorrowingsPDF = async () => {
  isExporting.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  const doc = new jsPDF()
  
  // Header
  doc.setFontSize(18)
  doc.text('Laporan Peminjaman Perpustakaan', 14, 22)
  doc.setFontSize(11)
  doc.text(`Tanggal: ${format(new Date(), 'dd MMMM yyyy', { locale: localeId })}`, 14, 30)
  
  // Table
  const tableData = mockStore.borrowings.map(b => [
    mockStore.getMemberById(b.memberId)?.name || '',
    mockStore.getBookById(b.bookId)?.title || '',
    format(new Date(b.borrowDate), 'dd/MM/yy'),
    b.returnDate ? format(new Date(b.returnDate), 'dd/MM/yy') : '-',
    b.status === 'active' ? 'Aktif' : b.status === 'returned' ? 'Kembali' : 'Telat',
    `Rp ${b.fine.toLocaleString('id-ID')}`
  ])
  
  autoTable(doc, {
    head: [['Member', 'Buku', 'Pinjam', 'Kembali', 'Status', 'Denda']],
    body: tableData,
    startY: 40,
    styles: { fontSize: 8 },
    headStyles: { fillColor: [16, 185, 129] }
  })
  
  doc.save(`Laporan_Peminjaman_${format(new Date(), 'yyyy-MM-dd')}.pdf`)
  
  isExporting.value = false
}

const formatCurrency = (value: number) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(value)
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Laporan & Statistik</h1>
        <p class="text-gray-500">Analisis data perpustakaan</p>
      </div>
      <div class="flex items-center gap-3">
        <Calendar class="w-5 h-5 text-gray-400" />
        <BaseSelect v-model="selectedPeriod" :options="periodOptions" class="w-48" />
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Trend Chart -->
      <BaseCard title="Tren Peminjaman & Pengembalian">
        <template #header>
          <div class="flex items-center justify-between w-full">
            <div class="flex items-center gap-2">
              <TrendingUp class="w-5 h-5 text-primary-500" />
              <h3 class="font-semibold text-gray-900">Tren Peminjaman & Pengembalian</h3>
            </div>
          </div>
        </template>
        <LineChart :labels="trendLabels" :datasets="trendDatasets" :height="280" />
      </BaseCard>

      <!-- Category Distribution -->
      <BaseCard>
        <template #header>
          <div class="flex items-center gap-2">
            <Book class="w-5 h-5 text-primary-500" />
            <h3 class="font-semibold text-gray-900">Distribusi Kategori Buku</h3>
          </div>
        </template>
        <DoughnutChart :labels="categoryLabels" :data="categoryData" :height="280" />
      </BaseCard>
    </div>

    <!-- Top Books Chart -->
    <BaseCard>
      <template #header>
        <div class="flex items-center gap-2">
          <Book class="w-5 h-5 text-amber-500" />
          <h3 class="font-semibold text-gray-900">Buku Terpopuler</h3>
        </div>
      </template>
      <BarChart :labels="topBookLabels" :data="topBookData" label="Jumlah Dipinjam" :height="300" />
    </BaseCard>

    <!-- Top Lists -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Top 10 Books -->
      <BaseCard :no-padding="true">
        <template #header>
          <div class="flex items-center gap-2">
            <Book class="w-5 h-5 text-primary-500" />
            <h3 class="font-semibold text-gray-900">Top 10 Buku Terpopuler</h3>
          </div>
        </template>
        <div class="divide-y divide-gray-100">
          <div
            v-for="(book, index) in topBorrowedBooks"
            :key="book.id"
            class="flex items-center gap-4 px-6 py-3 hover:bg-gray-50"
          >
            <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center font-bold text-primary-600">
              {{ index + 1 }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 truncate">{{ book.title }}</p>
              <p class="text-sm text-gray-500">{{ book.author }}</p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-gray-900">{{ book.borrowed }}</p>
              <p class="text-xs text-gray-500">kali dipinjam</p>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- Top 10 Members -->
      <BaseCard :no-padding="true">
        <template #header>
          <div class="flex items-center gap-2">
            <Users class="w-5 h-5 text-blue-500" />
            <h3 class="font-semibold text-gray-900">Top 10 Member Teraktif</h3>
          </div>
        </template>
        <div class="divide-y divide-gray-100">
          <div
            v-for="(item, index) in topActiveMembers"
            :key="item.member?.id"
            class="flex items-center gap-4 px-6 py-3 hover:bg-gray-50"
          >
            <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center font-bold text-blue-600">
              {{ index + 1 }}
            </div>
            <img :src="item.member?.photo" class="w-10 h-10 rounded-full object-cover" />
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 truncate">{{ item.member?.name }}</p>
              <p class="text-sm text-gray-500">{{ item.member?.memberID }}</p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-gray-900">{{ item.count }}</p>
              <p class="text-xs text-gray-500">peminjaman</p>
            </div>
          </div>
        </div>
      </BaseCard>
    </div>

    <!-- Export Section -->
    <BaseCard title="Export Laporan">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- Export Borrowings -->
        <div class="p-4 border border-gray-200 rounded-xl hover:border-primary-300 hover:bg-primary-50/50 transition-colors">
          <div class="flex items-center gap-3 mb-3">
            <div class="p-2 bg-green-100 rounded-lg">
              <FileSpreadsheet class="w-5 h-5 text-green-600" />
            </div>
            <div>
              <h4 class="font-medium text-gray-900">Laporan Peminjaman</h4>
              <p class="text-xs text-gray-500">Export data peminjaman</p>
            </div>
          </div>
          <div class="flex gap-2">
            <BaseButton size="sm" class="flex-1" :loading="isExporting" @click="exportBorrowingsExcel">
              <Download class="w-4 h-4" />
              Excel
            </BaseButton>
            <BaseButton size="sm" variant="secondary" class="flex-1" :loading="isExporting" @click="exportBorrowingsPDF">
              <FileText class="w-4 h-4" />
              PDF
            </BaseButton>
          </div>
        </div>

        <!-- Export Books -->
        <div class="p-4 border border-gray-200 rounded-xl hover:border-primary-300 hover:bg-primary-50/50 transition-colors">
          <div class="flex items-center gap-3 mb-3">
            <div class="p-2 bg-blue-100 rounded-lg">
              <Book class="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <h4 class="font-medium text-gray-900">Data Buku</h4>
              <p class="text-xs text-gray-500">Export seluruh data buku</p>
            </div>
          </div>
          <BaseButton size="sm" class="w-full" :loading="isExporting" @click="exportBooksExcel">
            <Download class="w-4 h-4" />
            Export Excel
          </BaseButton>
        </div>

        <!-- Export Members -->
        <div class="p-4 border border-gray-200 rounded-xl hover:border-primary-300 hover:bg-primary-50/50 transition-colors">
          <div class="flex items-center gap-3 mb-3">
            <div class="p-2 bg-purple-100 rounded-lg">
              <Users class="w-5 h-5 text-purple-600" />
            </div>
            <div>
              <h4 class="font-medium text-gray-900">Data Member</h4>
              <p class="text-xs text-gray-500">Export seluruh data member</p>
            </div>
          </div>
          <BaseButton size="sm" class="w-full" :loading="isExporting" @click="exportMembersExcel">
            <Download class="w-4 h-4" />
            Export Excel
          </BaseButton>
        </div>
      </div>
    </BaseCard>
  </div>
</template>
