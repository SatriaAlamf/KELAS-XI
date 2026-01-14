<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMockStore } from '@/stores/mockData'
import { useSettingsStore } from '@/stores/auth'
import type { Borrowing } from '@/types'
import { DataTable, BaseButton, BaseCard, BaseBadge, BaseModal, BaseInput, BaseSelect, SearchInput, ConfirmDialog } from '@/components/common'
import { Plus, RefreshCw, Clock, CheckCircle, AlertTriangle, FileText } from 'lucide-vue-next'
import { format, differenceInDays, parseISO } from 'date-fns'
import { id as localeId } from 'date-fns/locale'

const mockStore = useMockStore()
const settingsStore = useSettingsStore()

// State
const activeTab = ref<'active' | 'history'>('active')
const searchQuery = ref('')
const overdueOnly = ref(false)
const isReturnModalOpen = ref(false)
const isNewBorrowingModalOpen = ref(false)
const selectedBorrowing = ref<Borrowing | null>(null)
const isLoading = ref(false)

// Return form state
const returnDate = ref(format(new Date(), 'yyyy-MM-dd'))
const bookCondition = ref<'good' | 'damaged' | 'lost'>('good')

// New borrowing form state
const newBorrowingMemberId = ref<number | ''>('')
const newBorrowingBookId = ref<number | ''>('')

// Computed
const activeBorrowings = computed(() => {
  let borrowings = mockStore.activeBorrowings
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    borrowings = borrowings.filter(b => {
      const member = mockStore.getMemberById(b.memberId)
      const book = mockStore.getBookById(b.bookId)
      return member?.name.toLowerCase().includes(query) || 
             book?.title.toLowerCase().includes(query)
    })
  }
  
  if (overdueOnly.value) {
    borrowings = borrowings.filter(b => b.status === 'overdue')
  }
  
  return borrowings.map(b => ({
    ...b,
    memberName: mockStore.getMemberById(b.memberId)?.name || 'Unknown',
    bookTitle: mockStore.getBookById(b.bookId)?.title || 'Unknown'
  }))
})

const returnedBorrowings = computed(() => {
  let borrowings = mockStore.returnedBorrowings
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    borrowings = borrowings.filter(b => {
      const member = mockStore.getMemberById(b.memberId)
      const book = mockStore.getBookById(b.bookId)
      return member?.name.toLowerCase().includes(query) || 
             book?.title.toLowerCase().includes(query)
    })
  }
  
  return borrowings.map(b => ({
    ...b,
    memberName: mockStore.getMemberById(b.memberId)?.name || 'Unknown',
    bookTitle: mockStore.getBookById(b.bookId)?.title || 'Unknown'
  }))
})

const activeColumns = [
  { key: 'memberName', label: 'Member', sortable: true },
  { key: 'bookTitle', label: 'Buku', sortable: true },
  { key: 'borrowDate', label: 'Tgl Pinjam', sortable: true },
  { key: 'dueDate', label: 'Jatuh Tempo', sortable: true },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Aksi', width: '120px' }
]

const historyColumns = [
  { key: 'memberName', label: 'Member', sortable: true },
  { key: 'bookTitle', label: 'Buku', sortable: true },
  { key: 'borrowDate', label: 'Tgl Pinjam', sortable: true },
  { key: 'returnDate', label: 'Tgl Kembali', sortable: true },
  { key: 'fine', label: 'Denda', sortable: true },
  { key: 'bookCondition', label: 'Kondisi' }
]

const availableMembers = computed(() => 
  mockStore.members.filter(m => m.status === 'active').map(m => ({
    value: m.id,
    label: `${m.name} (${m.memberID})`
  }))
)

const availableBooks = computed(() => 
  mockStore.books.filter(b => b.stock - b.borrowed > 0).map(b => ({
    value: b.id,
    label: `${b.title} (${b.stock - b.borrowed} tersedia)`
  }))
)

const conditionOptions = [
  { value: 'good', label: 'Baik' },
  { value: 'damaged', label: 'Rusak (+Rp 10.000)' },
  { value: 'lost', label: 'Hilang (+Rp 50.000)' }
]

// Methods
const openReturnModal = (borrowing: Borrowing) => {
  selectedBorrowing.value = borrowing
  returnDate.value = format(new Date(), 'yyyy-MM-dd')
  bookCondition.value = 'good'
  isReturnModalOpen.value = true
}

const calculateFine = computed(() => {
  if (!selectedBorrowing.value) return 0
  
  const dueDate = parseISO(selectedBorrowing.value.dueDate)
  const returnDateParsed = parseISO(returnDate.value)
  let fine = 0
  
  const daysLate = differenceInDays(returnDateParsed, dueDate)
  if (daysLate > 0) {
    fine = daysLate * settingsStore.settings.finePerDay
  }
  
  if (bookCondition.value === 'damaged') {
    fine += 10000
  } else if (bookCondition.value === 'lost') {
    fine += 50000
  }
  
  return fine
})

const handleReturn = async () => {
  if (!selectedBorrowing.value) return
  
  isLoading.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  mockStore.returnBook(selectedBorrowing.value.id, bookCondition.value, returnDate.value)
  
  isLoading.value = false
  isReturnModalOpen.value = false
}

const handleNewBorrowing = async () => {
  if (!newBorrowingMemberId.value || !newBorrowingBookId.value) return
  
  isLoading.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  mockStore.createBorrowing(Number(newBorrowingMemberId.value), Number(newBorrowingBookId.value))
  
  isLoading.value = false
  isNewBorrowingModalOpen.value = false
  newBorrowingMemberId.value = ''
  newBorrowingBookId.value = ''
}

const formatDate = (date: string) => format(new Date(date), 'dd MMM yyyy', { locale: localeId })
const formatCurrency = (value: number) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(value)

const getDaysRemaining = (dueDate: string) => {
  const days = differenceInDays(parseISO(dueDate), new Date())
  if (days < 0) return `Telat ${Math.abs(days)} hari`
  if (days === 0) return 'Hari ini'
  return `${days} hari lagi`
}
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Transaksi</h1>
        <p class="text-gray-500">Kelola peminjaman dan pengembalian buku</p>
      </div>
      <BaseButton @click="isNewBorrowingModalOpen = true">
        <Plus class="w-4 h-4" />
        Peminjaman Baru
      </BaseButton>
    </div>

    <!-- Tabs -->
    <div class="flex gap-4 border-b border-gray-200">
      <button
        @click="activeTab = 'active'"
        :class="[
          'pb-4 px-2 font-medium text-sm transition-colors relative',
          activeTab === 'active'
            ? 'text-primary-600'
            : 'text-gray-500 hover:text-gray-700'
        ]"
      >
        <div class="flex items-center gap-2">
          <Clock class="w-4 h-4" />
          Peminjaman Aktif
          <span class="px-2 py-0.5 text-xs rounded-full bg-primary-100 text-primary-700">
            {{ activeBorrowings.length }}
          </span>
        </div>
        <div
          v-if="activeTab === 'active'"
          class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-500"
        />
      </button>
      <button
        @click="activeTab = 'history'"
        :class="[
          'pb-4 px-2 font-medium text-sm transition-colors relative',
          activeTab === 'history'
            ? 'text-primary-600'
            : 'text-gray-500 hover:text-gray-700'
        ]"
      >
        <div class="flex items-center gap-2">
          <FileText class="w-4 h-4" />
          Riwayat Pengembalian
        </div>
        <div
          v-if="activeTab === 'history'"
          class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-500"
        />
      </button>
    </div>

    <!-- Filters -->
    <BaseCard>
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="flex-1">
          <SearchInput v-model="searchQuery" placeholder="Cari member atau judul buku..." />
        </div>
        <div v-if="activeTab === 'active'" class="flex items-center gap-2">
          <input
            id="overdueOnly"
            type="checkbox"
            v-model="overdueOnly"
            class="w-4 h-4 rounded border-gray-300 text-primary-500 focus:ring-primary-500"
          />
          <label for="overdueOnly" class="text-sm text-gray-600 cursor-pointer">
            Hanya yang telat
          </label>
        </div>
      </div>
    </BaseCard>

    <!-- Active Borrowings Table -->
    <BaseCard v-if="activeTab === 'active'" :no-padding="true">
      <DataTable :columns="activeColumns" :data="activeBorrowings" :loading="isLoading">
        <template #memberName="{ row }">
          <div class="font-medium text-gray-900">{{ row.memberName }}</div>
        </template>
        
        <template #bookTitle="{ row }">
          <div class="text-gray-600">{{ row.bookTitle }}</div>
        </template>
        
        <template #borrowDate="{ value }">
          {{ formatDate(value) }}
        </template>
        
        <template #dueDate="{ row }">
          <div>{{ formatDate(row.dueDate) }}</div>
          <div :class="[
            'text-xs mt-0.5',
            row.status === 'overdue' ? 'text-red-500 font-medium' : 'text-gray-400'
          ]">
            {{ getDaysRemaining(row.dueDate) }}
          </div>
        </template>
        
        <template #status="{ row }">
          <BaseBadge :variant="row.status === 'overdue' ? 'danger' : 'info'">
            <AlertTriangle v-if="row.status === 'overdue'" class="w-3 h-3 mr-1" />
            {{ row.status === 'overdue' ? 'Telat' : 'Aktif' }}
          </BaseBadge>
        </template>
        
        <template #actions="{ row }">
          <BaseButton size="sm" @click.stop="openReturnModal(row)">
            <RefreshCw class="w-4 h-4" />
            Kembalikan
          </BaseButton>
        </template>
      </DataTable>
    </BaseCard>

    <!-- History Table -->
    <BaseCard v-else :no-padding="true">
      <DataTable :columns="historyColumns" :data="returnedBorrowings" :loading="isLoading">
        <template #borrowDate="{ value }">
          {{ formatDate(value) }}
        </template>
        
        <template #returnDate="{ value }">
          {{ formatDate(value) }}
        </template>
        
        <template #fine="{ value }">
          <span :class="value > 0 ? 'text-red-600 font-medium' : 'text-gray-500'">
            {{ value > 0 ? formatCurrency(value) : '-' }}
          </span>
        </template>
        
        <template #bookCondition="{ value }">
          <BaseBadge :variant="value === 'good' ? 'success' : value === 'damaged' ? 'warning' : 'danger'">
            {{ value === 'good' ? 'Baik' : value === 'damaged' ? 'Rusak' : 'Hilang' }}
          </BaseBadge>
        </template>
      </DataTable>
    </BaseCard>

    <!-- Return Modal -->
    <BaseModal
      :is-open="isReturnModalOpen"
      title="Proses Pengembalian"
      size="md"
      @close="isReturnModalOpen = false"
    >
      <div v-if="selectedBorrowing" class="space-y-6">
        <!-- Borrowing Info -->
        <div class="p-4 bg-gray-50 rounded-xl space-y-3">
          <div class="flex justify-between">
            <span class="text-gray-500">Member</span>
            <span class="font-medium">{{ mockStore.getMemberById(selectedBorrowing.memberId)?.name }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">Buku</span>
            <span class="font-medium">{{ mockStore.getBookById(selectedBorrowing.bookId)?.title }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">Tanggal Pinjam</span>
            <span>{{ formatDate(selectedBorrowing.borrowDate) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">Jatuh Tempo</span>
            <span :class="selectedBorrowing.status === 'overdue' ? 'text-red-600 font-medium' : ''">
              {{ formatDate(selectedBorrowing.dueDate) }}
            </span>
          </div>
        </div>

        <!-- Return Form -->
        <div class="space-y-4">
          <BaseInput
            v-model="returnDate"
            label="Tanggal Pengembalian"
            type="date"
            required
          />
          
          <BaseSelect
            v-model="bookCondition"
            label="Kondisi Buku"
            :options="conditionOptions"
            required
          />
        </div>

        <!-- Fine Calculation -->
        <div class="p-4 bg-primary-50 rounded-xl">
          <div class="flex justify-between items-center">
            <span class="text-gray-700 font-medium">Total Denda</span>
            <span class="text-2xl font-bold text-primary-600">
              {{ formatCurrency(calculateFine) }}
            </span>
          </div>
          <p v-if="calculateFine > 0" class="text-sm text-gray-500 mt-2">
            Denda akan ditambahkan ke akun member.
          </p>
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end gap-3">
          <BaseButton variant="secondary" @click="isReturnModalOpen = false">Batal</BaseButton>
          <BaseButton :loading="isLoading" @click="handleReturn">
            <CheckCircle class="w-4 h-4" />
            Konfirmasi Pengembalian
          </BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- New Borrowing Modal -->
    <BaseModal
      :is-open="isNewBorrowingModalOpen"
      title="Peminjaman Baru"
      size="md"
      @close="isNewBorrowingModalOpen = false"
    >
      <div class="space-y-4">
        <BaseSelect
          v-model="newBorrowingMemberId"
          label="Pilih Member"
          :options="availableMembers"
          placeholder="Pilih member..."
          required
        />
        
        <BaseSelect
          v-model="newBorrowingBookId"
          label="Pilih Buku"
          :options="availableBooks"
          placeholder="Pilih buku..."
          required
        />

        <div class="p-4 bg-gray-50 rounded-xl">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">Durasi Peminjaman</span>
            <span class="font-medium">{{ settingsStore.settings.borrowDuration }} hari</span>
          </div>
          <div class="flex justify-between text-sm mt-2">
            <span class="text-gray-500">Jatuh Tempo</span>
            <span class="font-medium">
              {{ formatDate(format(new Date(Date.now() + settingsStore.settings.borrowDuration * 24 * 60 * 60 * 1000), 'yyyy-MM-dd')) }}
            </span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end gap-3">
          <BaseButton variant="secondary" @click="isNewBorrowingModalOpen = false">Batal</BaseButton>
          <BaseButton 
            :loading="isLoading" 
            :disabled="!newBorrowingMemberId || !newBorrowingBookId"
            @click="handleNewBorrowing"
          >
            <Plus class="w-4 h-4" />
            Buat Peminjaman
          </BaseButton>
        </div>
      </template>
    </BaseModal>
  </div>
</template>
