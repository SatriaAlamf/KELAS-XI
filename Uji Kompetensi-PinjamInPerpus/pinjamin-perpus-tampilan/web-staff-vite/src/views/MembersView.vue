<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMockStore } from '@/stores/mockData'
import type { Member, MemberFormData, MemberType } from '@/types'
import { DataTable, BaseButton, BaseCard, BaseBadge, BaseModal, BaseInput, BaseSelect, SearchInput, ConfirmDialog } from '@/components/common'
import { Plus, Edit, Trash2, QrCode, Eye, Download, Ban, CheckCircle } from 'lucide-vue-next'
import QrcodeVue from 'qrcode.vue'
import { format } from 'date-fns'
import { id as localeId } from 'date-fns/locale'

const mockStore = useMockStore()

// State
const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const isFormModalOpen = ref(false)
const isDetailModalOpen = ref(false)
const isQRModalOpen = ref(false)
const isDeleteDialogOpen = ref(false)
const isBlockDialogOpen = ref(false)
const editingMember = ref<Member | null>(null)
const selectedMember = ref<Member | null>(null)
const isLoading = ref(false)

// Form state
const formData = ref<MemberFormData>({
  name: '',
  email: '',
  phone: '',
  address: '',
  type: 'Mahasiswa',
  photo: ''
})

// Computed
const filteredMembers = computed(() => {
  let members = mockStore.members
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    members = members.filter(m => 
      m.name.toLowerCase().includes(query) ||
      m.email.toLowerCase().includes(query) ||
      m.memberID.toLowerCase().includes(query)
    )
  }
  
  if (statusFilter.value) {
    members = members.filter(m => m.status === statusFilter.value)
  }
  
  if (typeFilter.value) {
    members = members.filter(m => m.type === typeFilter.value)
  }
  
  return members
})

const types: MemberType[] = ['Siswa', 'Mahasiswa', 'Dosen', 'Umum']
const typeOptions = types.map(t => ({ value: t, label: t }))
const statusOptions = [
  { value: '', label: 'Semua Status' },
  { value: 'active', label: 'Aktif' },
  { value: 'blocked', label: 'Diblokir' }
]

const columns = [
  { key: 'photo', label: 'Foto', width: '60px' },
  { key: 'name', label: 'Nama', sortable: true },
  { key: 'email', label: 'Email', sortable: true },
  { key: 'memberID', label: 'ID Member' },
  { key: 'type', label: 'Tipe', sortable: true },
  { key: 'status', label: 'Status', sortable: true },
  { key: 'fines', label: 'Tunggakan', sortable: true },
  { key: 'qr', label: 'QR' },
  { key: 'actions', label: 'Aksi', width: '150px' }
]

// Methods
const openAddModal = () => {
  editingMember.value = null
  formData.value = {
    name: '',
    email: '',
    phone: '',
    address: '',
    type: 'Mahasiswa',
    photo: ''
  }
  isFormModalOpen.value = true
}

const openEditModal = (member: Member) => {
  editingMember.value = member
  formData.value = {
    name: member.name,
    email: member.email,
    phone: member.phone,
    address: member.address,
    type: member.type,
    photo: member.photo
  }
  isFormModalOpen.value = true
}

const openDetailModal = (member: Member) => {
  selectedMember.value = member
  isDetailModalOpen.value = true
}

const openQRModal = (member: Member) => {
  selectedMember.value = member
  isQRModalOpen.value = true
}

const confirmDelete = (member: Member) => {
  selectedMember.value = member
  isDeleteDialogOpen.value = true
}

const confirmToggleBlock = (member: Member) => {
  selectedMember.value = member
  isBlockDialogOpen.value = true
}

const handleSave = async () => {
  isLoading.value = true
  await new Promise(resolve => setTimeout(resolve, 500))
  
  if (editingMember.value) {
    mockStore.updateMember(editingMember.value.id, formData.value)
  } else {
    mockStore.addMember(formData.value)
  }
  
  isLoading.value = false
  isFormModalOpen.value = false
}

const handleDelete = () => {
  if (selectedMember.value) {
    mockStore.deleteMember(selectedMember.value.id)
  }
  isDeleteDialogOpen.value = false
}

const handleToggleBlock = () => {
  if (selectedMember.value) {
    mockStore.toggleMemberStatus(selectedMember.value.id)
  }
  isBlockDialogOpen.value = false
}

const getQRData = (member: Member) => {
  return JSON.stringify({
    type: 'member',
    id: member.id,
    memberID: member.memberID
  })
}

const downloadQR = () => {
  const canvas = document.querySelector('#qr-canvas canvas') as HTMLCanvasElement
  if (canvas && selectedMember.value) {
    const link = document.createElement('a')
    link.download = `QR-${selectedMember.value.memberID}.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
  }
}

const formatCurrency = (value: number) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(value)
const formatDate = (date: string) => format(new Date(date), 'dd MMM yyyy', { locale: localeId })

const getMemberBorrowings = (memberId: number) => {
  return mockStore.borrowings.filter(b => b.memberId === memberId)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Manajemen Member</h1>
        <p class="text-gray-500">Kelola anggota perpustakaan</p>
      </div>
      <BaseButton @click="openAddModal">
        <Plus class="w-4 h-4" />
        Tambah Member
      </BaseButton>
    </div>

    <!-- Filters -->
    <BaseCard>
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="flex-1">
          <SearchInput v-model="searchQuery" placeholder="Cari nama, email, atau ID member..." />
        </div>
        <div class="sm:w-40">
          <BaseSelect v-model="statusFilter" :options="statusOptions" />
        </div>
        <div class="sm:w-40">
          <BaseSelect v-model="typeFilter" :options="[{ value: '', label: 'Semua Tipe' }, ...typeOptions]" />
        </div>
      </div>
    </BaseCard>

    <!-- Data Table -->
    <BaseCard :no-padding="true">
      <DataTable :columns="columns" :data="filteredMembers" :loading="isLoading">
        <template #photo="{ row }">
          <img :src="row.photo" :alt="row.name" class="w-10 h-10 rounded-full object-cover" />
        </template>
        
        <template #name="{ row }">
          <div class="font-medium text-gray-900">{{ row.name }}</div>
          <div class="text-sm text-gray-500">{{ row.phone }}</div>
        </template>
        
        <template #status="{ row }">
          <BaseBadge :variant="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? 'Aktif' : 'Diblokir' }}
          </BaseBadge>
        </template>
        
        <template #fines="{ row }">
          <span :class="row.fines > 0 ? 'text-red-600 font-medium' : 'text-gray-500'">
            {{ row.fines > 0 ? formatCurrency(row.fines) : '-' }}
          </span>
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
            <BaseButton variant="ghost" size="sm" @click.stop="confirmToggleBlock(row)">
              <Ban v-if="row.status === 'active'" class="w-4 h-4 text-amber-500" />
              <CheckCircle v-else class="w-4 h-4 text-green-500" />
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
      :title="editingMember ? 'Edit Member' : 'Tambah Member Baru'"
      size="lg"
      @close="isFormModalOpen = false"
    >
      <form @submit.prevent="handleSave" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <BaseInput v-model="formData.name" label="Nama Lengkap" placeholder="Masukkan nama" required />
          <BaseInput v-model="formData.email" label="Email" type="email" placeholder="email@example.com" required />
          <BaseInput v-model="formData.phone" label="No. HP" type="tel" placeholder="08xxxxxxxxxx" />
          <BaseSelect v-model="formData.type" label="Tipe Member" :options="typeOptions" required />
        </div>
        <BaseInput v-model="formData.address" label="Alamat" placeholder="Masukkan alamat lengkap" />
        <BaseInput v-model="formData.photo" label="URL Foto" placeholder="https://..." />
      </form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <BaseButton variant="secondary" @click="isFormModalOpen = false">Batal</BaseButton>
          <BaseButton :loading="isLoading" @click="handleSave">
            {{ editingMember ? 'Simpan Perubahan' : 'Tambah Member' }}
          </BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- Detail Modal -->
    <BaseModal
      :is-open="isDetailModalOpen"
      :title="selectedMember?.name || 'Detail Member'"
      size="lg"
      @close="isDetailModalOpen = false"
    >
      <div v-if="selectedMember" class="space-y-6">
        <div class="flex gap-6">
          <img :src="selectedMember.photo" :alt="selectedMember.name" class="w-24 h-24 rounded-full object-cover shadow-md" />
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <span class="text-xl font-bold text-gray-900">{{ selectedMember.name }}</span>
              <BaseBadge :variant="selectedMember.status === 'active' ? 'success' : 'danger'">
                {{ selectedMember.status === 'active' ? 'Aktif' : 'Diblokir' }}
              </BaseBadge>
            </div>
            <p class="text-gray-500">{{ selectedMember.email }}</p>
            <p class="text-gray-500">{{ selectedMember.phone }}</p>
            <BaseBadge>{{ selectedMember.type }}</BaseBadge>
          </div>
        </div>
        
        <div class="grid grid-cols-3 gap-4 p-4 bg-gray-50 rounded-xl">
          <div class="text-center">
            <p class="text-2xl font-bold text-primary-600">{{ selectedMember.memberID }}</p>
            <p class="text-sm text-gray-500">ID Member</p>
          </div>
          <div class="text-center">
            <p class="text-2xl font-bold text-blue-600">{{ getMemberBorrowings(selectedMember.id).length }}</p>
            <p class="text-sm text-gray-500">Total Peminjaman</p>
          </div>
          <div class="text-center">
            <p :class="['text-2xl font-bold', selectedMember.fines > 0 ? 'text-red-600' : 'text-green-600']">
              {{ formatCurrency(selectedMember.fines) }}
            </p>
            <p class="text-sm text-gray-500">Tunggakan</p>
          </div>
        </div>
        
        <div>
          <p class="text-sm text-gray-500 mb-1">Alamat</p>
          <p class="text-gray-700">{{ selectedMember.address }}</p>
        </div>
        
        <div>
          <p class="text-sm text-gray-500 mb-1">Terdaftar Sejak</p>
          <p class="text-gray-700">{{ formatDate(selectedMember.createdAt) }}</p>
        </div>
      </div>
    </BaseModal>

    <!-- QR Modal -->
    <BaseModal
      :is-open="isQRModalOpen"
      title="QR Code Member"
      size="sm"
      @close="isQRModalOpen = false"
    >
      <div v-if="selectedMember" class="text-center space-y-4">
        <p class="font-medium text-gray-900">{{ selectedMember.name }}</p>
        <p class="text-sm text-gray-500">ID: {{ selectedMember.memberID }}</p>
        <div id="qr-canvas" class="flex justify-center py-4">
          <QrcodeVue :value="getQRData(selectedMember)" :size="200" level="H" />
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
      title="Hapus Member"
      :message="`Apakah Anda yakin ingin menghapus member '${selectedMember?.name}'? Tindakan ini tidak dapat dibatalkan.`"
      type="danger"
      confirm-text="Hapus"
      @confirm="handleDelete"
      @cancel="isDeleteDialogOpen = false"
    />

    <!-- Block/Unblock Confirmation -->
    <ConfirmDialog
      :is-open="isBlockDialogOpen"
      :title="selectedMember?.status === 'active' ? 'Blokir Member' : 'Aktifkan Member'"
      :message="selectedMember?.status === 'active' 
        ? `Apakah Anda yakin ingin memblokir member '${selectedMember?.name}'?` 
        : `Apakah Anda yakin ingin mengaktifkan kembali member '${selectedMember?.name}'?`"
      :type="selectedMember?.status === 'active' ? 'warning' : 'success'"
      :confirm-text="selectedMember?.status === 'active' ? 'Blokir' : 'Aktifkan'"
      @confirm="handleToggleBlock"
      @cancel="isBlockDialogOpen = false"
    />
  </div>
</template>
