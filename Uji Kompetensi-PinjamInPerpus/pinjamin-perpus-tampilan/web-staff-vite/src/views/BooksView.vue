<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMockStore } from '@/stores/mockData'
import type { Book, BookFormData, BookCategory } from '@/types'
import { DataTable, BaseButton, BaseCard, BaseBadge, BaseModal, BaseInput, BaseSelect, SearchInput, ConfirmDialog } from '@/components/common'
import { Plus, Edit, Trash2, QrCode, Eye, Download } from 'lucide-vue-next'
import QrcodeVue from 'qrcode.vue'

const mockStore = useMockStore()

// State
const searchQuery = ref('')
const categoryFilter = ref('')
const isFormModalOpen = ref(false)
const isDetailModalOpen = ref(false)
const isQRModalOpen = ref(false)
const isDeleteDialogOpen = ref(false)
const editingBook = ref<Book | null>(null)
const selectedBook = ref<Book | null>(null)
const isLoading = ref(false)

// Form state
const formData = ref<BookFormData>({
  title: '',
  author: '',
  publisher: '',
  publishYear: new Date().getFullYear(),
  isbn: '',
  category: 'Fiksi',
  stock: 1,
  description: '',
  cover: ''
})

// Computed
const filteredBooks = computed(() => {
  let books = mockStore.books
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    books = books.filter(b => 
      b.title.toLowerCase().includes(query) ||
      b.author.toLowerCase().includes(query) ||
      b.isbn.toLowerCase().includes(query)
    )
  }
  
  if (categoryFilter.value) {
    books = books.filter(b => b.category === categoryFilter.value)
  }
  
  return books
})

const categories: BookCategory[] = ['Fiksi', 'Non-Fiksi', 'Referensi', 'Sains', 'Sejarah', 'Teknologi', 'Sastra', 'Pendidikan']

const categoryOptions = categories.map(c => ({ value: c, label: c }))

const columns = [
  { key: 'cover', label: 'Cover', width: '80px' },
  { key: 'title', label: 'Judul', sortable: true },
  { key: 'author', label: 'Penulis', sortable: true },
  { key: 'category', label: 'Kategori', sortable: true },
  { key: 'isbn', label: 'ISBN' },
  { key: 'stock', label: 'Stok', sortable: true },
  { key: 'status', label: 'Status' },
  { key: 'qr', label: 'QR' },
  { key: 'actions', label: 'Aksi', width: '120px' }
]

// Methods
const openAddModal = () => {
  editingBook.value = null
  formData.value = {
    title: '',
    author: '',
    publisher: '',
    publishYear: new Date().getFullYear(),
    isbn: '',
    category: 'Fiksi',
    stock: 1,
    description: '',
    cover: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=200&h=300&fit=crop'
  }
  isFormModalOpen.value = true
}

const openEditModal = (book: Book) => {
  editingBook.value = book
  formData.value = {
    title: book.title,
    author: book.author,
    publisher: book.publisher,
    publishYear: book.publishYear,
    isbn: book.isbn,
    category: book.category,
    stock: book.stock,
    description: book.description,
    cover: book.cover
  }
  isFormModalOpen.value = true
}

const openDetailModal = (book: Book) => {
  selectedBook.value = book
  isDetailModalOpen.value = true
}

const openQRModal = (book: Book) => {
  selectedBook.value = book
  isQRModalOpen.value = true
}

const confirmDelete = (book: Book) => {
  selectedBook.value = book
  isDeleteDialogOpen.value = true
}

const handleSave = async () => {
  isLoading.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  if (editingBook.value) {
    mockStore.updateBook(editingBook.value.id, formData.value)
  } else {
    mockStore.addBook(formData.value)
  }
  
  isLoading.value = false
  isFormModalOpen.value = false
}

const handleDelete = () => {
  if (selectedBook.value) {
    mockStore.deleteBook(selectedBook.value.id)
  }
  isDeleteDialogOpen.value = false
}

const getQRData = (book: Book) => {
  return JSON.stringify({
    type: 'book',
    id: book.id,
    isbn: book.isbn
  })
}

const downloadQR = () => {
  const canvas = document.querySelector('#qr-canvas canvas') as HTMLCanvasElement
  if (canvas && selectedBook.value) {
    const link = document.createElement('a')
    link.download = `QR-${selectedBook.value.isbn}.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Manajemen Buku</h1>
        <p class="text-gray-500">Kelola koleksi buku perpustakaan</p>
      </div>
      <BaseButton @click="openAddModal">
        <Plus class="w-4 h-4" />
        Tambah Buku
      </BaseButton>
    </div>

    <!-- Filters -->
    <BaseCard>
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="flex-1">
          <SearchInput v-model="searchQuery" placeholder="Cari judul, penulis, atau ISBN..." />
        </div>
        <div class="sm:w-48">
          <BaseSelect
            v-model="categoryFilter"
            :options="[{ value: '', label: 'Semua Kategori' }, ...categoryOptions]"
          />
        </div>
      </div>
    </BaseCard>

    <!-- Data Table -->
    <BaseCard :no-padding="true">
      <DataTable :columns="columns" :data="filteredBooks" :loading="isLoading">
        <template #cover="{ row }">
          <img :src="row.cover" :alt="row.title" class="w-10 h-14 object-cover rounded" />
        </template>
        
        <template #title="{ row }">
          <div class="font-medium text-gray-900">{{ row.title }}</div>
        </template>
        
        <template #stock="{ row }">
          <span class="font-medium">{{ row.stock - row.borrowed }}</span>
          <span class="text-gray-400">/{{ row.stock }}</span>
        </template>
        
        <template #status="{ row }">
          <BaseBadge :variant="row.stock - row.borrowed > 0 ? 'success' : 'warning'">
            {{ row.stock - row.borrowed > 0 ? 'Tersedia' : 'Dipinjam Semua' }}
          </BaseBadge>
        </template>
        
        <template #qr="{ row }">
          <BaseButton variant="ghost" size="sm" @click.stop="openQRModal(row)">
            <QrCode class="w-4 h-4" />
          </BaseButton>
        </template>
        
        <template #actions="{ row }">
          <div class="flex items-center gap-1">
            <BaseButton variant="ghost" size="sm" @click.stop="openDetailModal(row)">
              <Eye class="w-4 h-4" />
            </BaseButton>
            <BaseButton variant="ghost" size="sm" @click.stop="openEditModal(row)">
              <Edit class="w-4 h-4" />
            </BaseButton>
            <BaseButton variant="ghost" size="sm" @click.stop="confirmDelete(row)">
              <Trash2 class="w-4 h-4 text-red-500" />
            </BaseButton>
          </div>
        </template>
      </DataTable>
    </BaseCard>

    <!-- Add/Edit Modal -->
    <BaseModal
      :is-open="isFormModalOpen"
      :title="editingBook ? 'Edit Buku' : 'Tambah Buku Baru'"
      size="lg"
      @close="isFormModalOpen = false"
    >
      <form @submit.prevent="handleSave" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <BaseInput v-model="formData.title" label="Judul Buku" placeholder="Masukkan judul" required />
          <BaseInput v-model="formData.author" label="Penulis" placeholder="Masukkan nama penulis" required />
          <BaseInput v-model="formData.publisher" label="Penerbit" placeholder="Masukkan penerbit" />
          <BaseInput v-model="formData.publishYear" label="Tahun Terbit" type="number" />
          <BaseInput v-model="formData.isbn" label="ISBN" placeholder="978-xxx-xxx" required />
          <BaseSelect v-model="formData.category" label="Kategori" :options="categoryOptions" required />
          <BaseInput v-model="formData.stock" label="Jumlah Stok" type="number" required />
          <BaseInput v-model="formData.cover" label="URL Cover" placeholder="https://..." />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Deskripsi</label>
          <textarea
            v-model="formData.description"
            rows="3"
            class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="Deskripsi singkat buku..."
          ></textarea>
        </div>
      </form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <BaseButton variant="secondary" @click="isFormModalOpen = false">Batal</BaseButton>
          <BaseButton :loading="isLoading" @click="handleSave">
            {{ editingBook ? 'Simpan Perubahan' : 'Tambah Buku' }}
          </BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- Detail Modal -->
    <BaseModal
      :is-open="isDetailModalOpen"
      :title="selectedBook?.title || 'Detail Buku'"
      size="lg"
      @close="isDetailModalOpen = false"
    >
      <div v-if="selectedBook" class="space-y-6">
        <div class="flex gap-6">
          <img :src="selectedBook.cover" :alt="selectedBook.title" class="w-32 h-48 object-cover rounded-lg shadow-md" />
          <div class="space-y-3">
            <div>
              <p class="text-sm text-gray-500">Penulis</p>
              <p class="font-medium">{{ selectedBook.author }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Penerbit</p>
              <p class="font-medium">{{ selectedBook.publisher }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Tahun Terbit</p>
              <p class="font-medium">{{ selectedBook.publishYear }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">ISBN</p>
              <p class="font-medium font-mono">{{ selectedBook.isbn }}</p>
            </div>
          </div>
        </div>
        <div>
          <p class="text-sm text-gray-500 mb-1">Deskripsi</p>
          <p class="text-gray-700">{{ selectedBook.description }}</p>
        </div>
        <div class="grid grid-cols-3 gap-4 p-4 bg-gray-50 rounded-xl">
          <div class="text-center">
            <p class="text-2xl font-bold text-primary-600">{{ selectedBook.stock }}</p>
            <p class="text-sm text-gray-500">Total Stok</p>
          </div>
          <div class="text-center">
            <p class="text-2xl font-bold text-amber-600">{{ selectedBook.borrowed }}</p>
            <p class="text-sm text-gray-500">Dipinjam</p>
          </div>
          <div class="text-center">
            <p class="text-2xl font-bold text-green-600">{{ selectedBook.stock - selectedBook.borrowed }}</p>
            <p class="text-sm text-gray-500">Tersedia</p>
          </div>
        </div>
      </div>
    </BaseModal>

    <!-- QR Modal -->
    <BaseModal
      :is-open="isQRModalOpen"
      title="QR Code Buku"
      size="sm"
      @close="isQRModalOpen = false"
    >
      <div v-if="selectedBook" class="text-center space-y-4">
        <p class="font-medium text-gray-900">{{ selectedBook.title }}</p>
        <p class="text-sm text-gray-500">ISBN: {{ selectedBook.isbn }}</p>
        <div id="qr-canvas" class="flex justify-center py-4">
          <QrcodeVue :value="getQRData(selectedBook)" :size="200" level="H" />
        </div>
        <BaseButton @click="downloadQR" class="w-full">
          <Download class="w-4 h-4" />
          Download QR Code
        </BaseButton>
      </div>
    </BaseModal>

    <!-- Delete Confirmation -->
    <ConfirmDialog
      :is-open="isDeleteDialogOpen"
      title="Hapus Buku"
      :message="`Apakah Anda yakin ingin menghapus buku '${selectedBook?.title}'? Tindakan ini tidak dapat dibatalkan.`"
      type="danger"
      confirm-text="Hapus"
      @confirm="handleDelete"
      @cancel="isDeleteDialogOpen = false"
    />
  </div>
</template>
