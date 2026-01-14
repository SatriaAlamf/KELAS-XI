<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMockStore } from '@/stores/mockData'
import { Bell, Search, ChevronDown, User, LogOut, Settings } from 'lucide-vue-next'

const route = useRoute()
const authStore = useAuthStore()
const mockStore = useMockStore()

const isProfileOpen = ref(false)
const searchQuery = ref('')

const breadcrumbs = computed(() => {
  const paths = route.path.split('/').filter(Boolean)
  const items = paths.map((path, index) => {
    const fullPath = '/' + paths.slice(0, index + 1).join('/')
    const labels: Record<string, string> = {
      dashboard: 'Dashboard',
      books: 'Manajemen Buku',
      members: 'Manajemen Member',
      transactions: 'Transaksi',
      reports: 'Laporan',
      settings: 'Pengaturan'
    }
    return {
      path: fullPath,
      label: labels[path] || path.charAt(0).toUpperCase() + path.slice(1)
    }
  })
  return items
})

const overdueCount = computed(() => mockStore.overdueBorrowings.length)
</script>

<template>
  <header class="bg-white border-b border-gray-200 sticky top-0 z-30">
    <div class="flex items-center justify-between px-6 py-4">
      <!-- Left: Breadcrumbs -->
      <div class="flex items-center gap-2 ml-12 lg:ml-0">
        <nav class="flex items-center text-sm">
          <router-link to="/dashboard" class="text-gray-500 hover:text-primary-600 transition-colors">
            Beranda
          </router-link>
          <template v-for="(crumb, index) in breadcrumbs" :key="crumb.path">
            <span class="mx-2 text-gray-400">/</span>
            <router-link
              v-if="index < breadcrumbs.length - 1"
              :to="crumb.path"
              class="text-gray-500 hover:text-primary-600 transition-colors"
            >
              {{ crumb.label }}
            </router-link>
            <span v-else class="text-gray-900 font-medium">{{ crumb.label }}</span>
          </template>
        </nav>
      </div>

      <!-- Right: Search, Notifications, Profile -->
      <div class="flex items-center gap-4">
        <!-- Search -->
        <div class="relative hidden md:block">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Cari cepat... (Ctrl+K)"
            class="w-64 pl-10 pr-4 py-2 rounded-lg border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 text-sm transition-all"
          />
        </div>

        <!-- Notifications -->
        <button class="relative p-2 rounded-lg hover:bg-gray-100 transition-colors">
          <Bell class="w-5 h-5 text-gray-600" />
          <span
            v-if="overdueCount > 0"
            class="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center"
          >
            {{ overdueCount }}
          </span>
        </button>

        <!-- Profile Dropdown -->
        <div class="relative">
          <button
            @click="isProfileOpen = !isProfileOpen"
            class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <img
              :src="authStore.staff?.photo || 'https://ui-avatars.com/api/?name=User'"
              alt="Profile"
              class="w-8 h-8 rounded-full object-cover"
            />
            <div class="hidden sm:block text-left">
              <p class="text-sm font-medium text-gray-900">{{ authStore.staff?.name || 'Staff' }}</p>
              <p class="text-xs text-gray-500">{{ authStore.staff?.role || 'Staff' }}</p>
            </div>
            <ChevronDown :class="['w-4 h-4 text-gray-500 transition-transform', isProfileOpen ? 'rotate-180' : '']" />
          </button>

          <!-- Dropdown Menu -->
          <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="opacity-0 scale-95"
            enter-to-class="opacity-100 scale-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="opacity-100 scale-100"
            leave-to-class="opacity-0 scale-95"
          >
            <div
              v-if="isProfileOpen"
              class="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-50"
            >
              <div class="px-4 py-3 border-b border-gray-100">
                <p class="font-medium text-gray-900">{{ authStore.staff?.name }}</p>
                <p class="text-sm text-gray-500">{{ authStore.staff?.email }}</p>
              </div>
              <router-link
                to="/settings"
                class="flex items-center gap-3 px-4 py-2 text-gray-700 hover:bg-gray-50 transition-colors"
                @click="isProfileOpen = false"
              >
                <Settings class="w-4 h-4" />
                Pengaturan
              </router-link>
              <button
                @click="authStore.logout(); $router.push('/login')"
                class="flex items-center gap-3 w-full px-4 py-2 text-red-600 hover:bg-red-50 transition-colors"
              >
                <LogOut class="w-4 h-4" />
                Keluar
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </header>

  <!-- Click outside handler -->
  <div
    v-if="isProfileOpen"
    class="fixed inset-0 z-20"
    @click="isProfileOpen = false"
  />
</template>
