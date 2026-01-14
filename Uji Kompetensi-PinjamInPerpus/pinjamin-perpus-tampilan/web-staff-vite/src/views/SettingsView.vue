<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore, useSettingsStore } from '@/stores/auth'
import { BaseButton, BaseCard, BaseInput, BaseSelect } from '@/components/common'
import { User, Settings as SettingsIcon, Save, Bell, Shield, Clock, AlertTriangle } from 'lucide-vue-next'

const authStore = useAuthStore()
const settingsStore = useSettingsStore()

// State
const activeTab = ref<'profile' | 'system'>('profile')
const isLoading = ref(false)
const profileSaved = ref(false)
const settingsSaved = ref(false)

// Profile form
const profileForm = ref({
  name: authStore.staff?.name || '',
  email: authStore.staff?.email || '',
  phone: authStore.staff?.phone || '',
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// System settings
const systemSettings = ref({
  borrowDuration: settingsStore.settings.borrowDuration,
  finePerDay: settingsStore.settings.finePerDay,
  maxBooksPerMember: settingsStore.settings.maxBooksPerMember,
  emailNotifications: settingsStore.settings.emailNotifications,
  maintenanceMode: settingsStore.settings.maintenanceMode
})

const durationOptions = [
  { value: 3, label: '3 Hari' },
  { value: 7, label: '7 Hari' },
  { value: 14, label: '14 Hari' },
  { value: 30, label: '30 Hari' }
]

const maxBooksOptions = [
  { value: 1, label: '1 Buku' },
  { value: 2, label: '2 Buku' },
  { value: 3, label: '3 Buku' },
  { value: 5, label: '5 Buku' },
  { value: 10, label: '10 Buku' }
]

const fineOptions = [
  { value: 500, label: 'Rp 500 / hari' },
  { value: 1000, label: 'Rp 1.000 / hari' },
  { value: 2000, label: 'Rp 2.000 / hari' },
  { value: 5000, label: 'Rp 5.000 / hari' }
]

// Methods
const saveProfile = async () => {
  isLoading.value = true
  profileSaved.value = false
  
  await new Promise(resolve => setTimeout(resolve, 800))
  
  // Update auth store
  if (authStore.staff) {
    authStore.staff.name = profileForm.value.name
    authStore.staff.email = profileForm.value.email
    authStore.staff.phone = profileForm.value.phone
  }
  
  isLoading.value = false
  profileSaved.value = true
  
  setTimeout(() => {
    profileSaved.value = false
  }, 3000)
}

const saveSettings = async () => {
  isLoading.value = true
  settingsSaved.value = false
  
  await new Promise(resolve => setTimeout(resolve, 800))
  
  settingsStore.updateSettings({
    borrowDuration: systemSettings.value.borrowDuration,
    finePerDay: systemSettings.value.finePerDay,
    maxBooksPerMember: systemSettings.value.maxBooksPerMember,
    emailNotifications: systemSettings.value.emailNotifications,
    maintenanceMode: systemSettings.value.maintenanceMode
  })
  
  isLoading.value = false
  settingsSaved.value = true
  
  setTimeout(() => {
    settingsSaved.value = false
  }, 3000)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div>
      <h1 class="text-2xl font-bold text-gray-900">Pengaturan</h1>
      <p class="text-gray-500">Kelola profil dan konfigurasi sistem</p>
    </div>

    <!-- Tabs -->
    <div class="flex gap-4 border-b border-gray-200">
      <button
        @click="activeTab = 'profile'"
        :class="[
          'pb-4 px-2 font-medium text-sm transition-colors relative',
          activeTab === 'profile'
            ? 'text-primary-600'
            : 'text-gray-500 hover:text-gray-700'
        ]"
      >
        <div class="flex items-center gap-2">
          <User class="w-4 h-4" />
          Profil Staff
        </div>
        <div
          v-if="activeTab === 'profile'"
          class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-500"
        />
      </button>
      <button
        @click="activeTab = 'system'"
        :class="[
          'pb-4 px-2 font-medium text-sm transition-colors relative',
          activeTab === 'system'
            ? 'text-primary-600'
            : 'text-gray-500 hover:text-gray-700'
        ]"
      >
        <div class="flex items-center gap-2">
          <SettingsIcon class="w-4 h-4" />
          Pengaturan Sistem
        </div>
        <div
          v-if="activeTab === 'system'"
          class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-500"
        />
      </button>
    </div>

    <!-- Profile Tab -->
    <div v-if="activeTab === 'profile'" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Profile Card -->
      <BaseCard>
        <div class="text-center">
          <img
            :src="authStore.staff?.photo"
            :alt="authStore.staff?.name"
            class="w-24 h-24 rounded-full mx-auto mb-4 object-cover ring-4 ring-primary-100"
          />
          <h3 class="text-lg font-semibold text-gray-900">{{ authStore.staff?.name }}</h3>
          <p class="text-gray-500">{{ authStore.staff?.role }}</p>
          <p class="text-sm text-gray-400 mt-1">{{ authStore.staff?.email }}</p>
          
          <div class="mt-6 pt-6 border-t border-gray-200">
            <BaseButton variant="secondary" class="w-full">
              Ganti Foto
            </BaseButton>
          </div>
        </div>
      </BaseCard>

      <!-- Profile Form -->
      <BaseCard title="Informasi Profil" class="lg:col-span-2">
        <form @submit.prevent="saveProfile" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <BaseInput
              v-model="profileForm.name"
              label="Nama Lengkap"
              placeholder="Masukkan nama"
              required
            />
            <BaseInput
              v-model="profileForm.email"
              label="Email"
              type="email"
              placeholder="email@example.com"
              required
            />
            <BaseInput
              v-model="profileForm.phone"
              label="No. HP"
              type="tel"
              placeholder="08xxxxxxxxxx"
            />
          </div>

          <!-- Change Password Section -->
          <div class="pt-6 border-t border-gray-200">
            <h4 class="font-medium text-gray-900 mb-4">Ubah Password</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <BaseInput
                v-model="profileForm.currentPassword"
                label="Password Saat Ini"
                type="password"
                placeholder="••••••••"
              />
              <BaseInput
                v-model="profileForm.newPassword"
                label="Password Baru"
                type="password"
                placeholder="••••••••"
              />
              <BaseInput
                v-model="profileForm.confirmPassword"
                label="Konfirmasi Password"
                type="password"
                placeholder="••••••••"
              />
            </div>
          </div>

          <div class="flex items-center justify-end gap-3 pt-4">
            <Transition
              enter-active-class="transition-opacity duration-200"
              enter-from-class="opacity-0"
              enter-to-class="opacity-100"
              leave-active-class="transition-opacity duration-200"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
            >
              <span v-if="profileSaved" class="text-sm text-green-600 flex items-center gap-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                </svg>
                Perubahan tersimpan!
              </span>
            </Transition>
            <BaseButton type="submit" :loading="isLoading">
              <Save class="w-4 h-4" />
              Simpan Perubahan
            </BaseButton>
          </div>
        </form>
      </BaseCard>
    </div>

    <!-- System Settings Tab -->
    <div v-else class="space-y-6">
      <!-- Borrowing Settings -->
      <BaseCard>
        <template #header>
          <div class="flex items-center gap-2">
            <Clock class="w-5 h-5 text-primary-500" />
            <h3 class="font-semibold text-gray-900">Pengaturan Peminjaman</h3>
          </div>
        </template>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <BaseSelect
              v-model="systemSettings.borrowDuration"
              label="Durasi Peminjaman Default"
              :options="durationOptions"
            />
            <p class="text-xs text-gray-500 mt-1">Lama peminjaman standar untuk setiap buku</p>
          </div>
          
          <div>
            <BaseSelect
              v-model="systemSettings.finePerDay"
              label="Denda per Hari"
              :options="fineOptions"
            />
            <p class="text-xs text-gray-500 mt-1">Denda keterlambatan per hari</p>
          </div>
          
          <div>
            <BaseSelect
              v-model="systemSettings.maxBooksPerMember"
              label="Maksimal Buku per Member"
              :options="maxBooksOptions"
            />
            <p class="text-xs text-gray-500 mt-1">Jumlah maksimal buku yang bisa dipinjam</p>
          </div>
        </div>
      </BaseCard>

      <!-- Notification Settings -->
      <BaseCard>
        <template #header>
          <div class="flex items-center gap-2">
            <Bell class="w-5 h-5 text-blue-500" />
            <h3 class="font-semibold text-gray-900">Pengaturan Notifikasi</h3>
          </div>
        </template>
        
        <div class="space-y-4">
          <label class="flex items-center justify-between p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100 transition-colors">
            <div>
              <p class="font-medium text-gray-900">Notifikasi Email</p>
              <p class="text-sm text-gray-500">Kirim email pengingat kepada member sebelum jatuh tempo</p>
            </div>
            <div class="relative">
              <input
                type="checkbox"
                v-model="systemSettings.emailNotifications"
                class="sr-only peer"
              />
              <div class="w-11 h-6 bg-gray-300 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-500"></div>
            </div>
          </label>
        </div>
      </BaseCard>

      <!-- System Settings -->
      <BaseCard>
        <template #header>
          <div class="flex items-center gap-2">
            <Shield class="w-5 h-5 text-amber-500" />
            <h3 class="font-semibold text-gray-900">Pengaturan Sistem</h3>
          </div>
        </template>
        
        <div class="space-y-4">
          <label class="flex items-center justify-between p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100 transition-colors">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-amber-100 rounded-lg">
                <AlertTriangle class="w-5 h-5 text-amber-600" />
              </div>
              <div>
                <p class="font-medium text-gray-900">Mode Maintenance</p>
                <p class="text-sm text-gray-500">Nonaktifkan akses member sementara untuk pemeliharaan sistem</p>
              </div>
            </div>
            <div class="relative">
              <input
                type="checkbox"
                v-model="systemSettings.maintenanceMode"
                class="sr-only peer"
              />
              <div class="w-11 h-6 bg-gray-300 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-amber-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-amber-500"></div>
            </div>
          </label>
        </div>
      </BaseCard>

      <!-- Save Button -->
      <div class="flex items-center justify-end gap-3">
        <Transition
          enter-active-class="transition-opacity duration-200"
          enter-from-class="opacity-0"
          enter-to-class="opacity-100"
          leave-active-class="transition-opacity duration-200"
          leave-from-class="opacity-100"
          leave-to-class="opacity-0"
        >
          <span v-if="settingsSaved" class="text-sm text-green-600 flex items-center gap-1">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
            </svg>
            Pengaturan tersimpan!
          </span>
        </Transition>
        <BaseButton :loading="isLoading" @click="saveSettings">
          <Save class="w-4 h-4" />
          Simpan Pengaturan
        </BaseButton>
      </div>
    </div>
  </div>
</template>
