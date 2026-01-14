<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  LayoutDashboard,
  BookOpen,
  Users,
  ArrowLeftRight,
  BarChart3,
  Settings,
  LogOut,
  ChevronLeft,
  Menu,
  Library
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)
const isMobileOpen = ref(false)

const menuItems = [
  { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { path: '/books', label: 'Manajemen Buku', icon: BookOpen },
  { path: '/members', label: 'Manajemen Member', icon: Users },
  { path: '/transactions', label: 'Transaksi', icon: ArrowLeftRight },
  { path: '/reports', label: 'Laporan & Statistik', icon: BarChart3 },
  { path: '/settings', label: 'Pengaturan', icon: Settings },
]

const isActive = (path: string) => route.path.startsWith(path)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleMobile = () => {
  isMobileOpen.value = !isMobileOpen.value
}
</script>

<template>
  <!-- Mobile Menu Button -->
  <button
    @click="toggleMobile"
    class="lg:hidden fixed top-4 left-4 z-50 p-2 bg-white rounded-lg shadow-md"
  >
    <Menu class="w-6 h-6 text-gray-600" />
  </button>

  <!-- Overlay -->
  <div
    v-if="isMobileOpen"
    class="lg:hidden fixed inset-0 bg-black/50 z-40"
    @click="toggleMobile"
  />

  <!-- Sidebar -->
  <aside
    :class="[
      'fixed top-0 left-0 h-screen bg-white border-r border-gray-200 z-40 transition-all duration-300 flex flex-col',
      isCollapsed ? 'w-20' : 'w-64',
      isMobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
    ]"
  >
    <!-- Logo -->
    <div class="flex items-center gap-3 px-6 py-5 border-b border-gray-200">
      <div class="p-2 bg-primary-100 rounded-xl flex-shrink-0">
        <Library class="w-6 h-6 text-primary-600" />
      </div>
      <div v-if="!isCollapsed" class="overflow-hidden">
        <h1 class="font-bold text-lg text-gray-900 whitespace-nowrap">PinjamIn Perpus</h1>
        <p class="text-xs text-gray-500">Staff Dashboard</p>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 p-4 space-y-1 overflow-y-auto">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        :class="[
          'flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200',
          isActive(item.path)
            ? 'bg-primary-50 text-primary-700 font-medium'
            : 'text-gray-600 hover:bg-gray-100'
        ]"
        @click="isMobileOpen = false"
      >
        <component
          :is="item.icon"
          :class="['w-5 h-5 flex-shrink-0', isActive(item.path) ? 'text-primary-600' : 'text-gray-500']"
        />
        <span v-if="!isCollapsed" class="whitespace-nowrap">{{ item.label }}</span>
      </router-link>
    </nav>

    <!-- Footer -->
    <div class="p-4 border-t border-gray-200">
      <button
        @click="handleLogout"
        :class="[
          'flex items-center gap-3 w-full px-4 py-3 rounded-xl text-gray-600 hover:bg-red-50 hover:text-red-600 transition-all duration-200',
          isCollapsed ? 'justify-center' : ''
        ]"
      >
        <LogOut class="w-5 h-5 flex-shrink-0" />
        <span v-if="!isCollapsed">Keluar</span>
      </button>
    </div>

    <!-- Collapse Button (Desktop) -->
    <button
      @click="toggleSidebar"
      class="hidden lg:flex absolute -right-3 top-1/2 transform -translate-y-1/2 w-6 h-6 bg-white border border-gray-200 rounded-full items-center justify-center shadow-sm hover:bg-gray-50 transition-colors"
    >
      <ChevronLeft :class="['w-4 h-4 text-gray-600 transition-transform', isCollapsed ? 'rotate-180' : '']" />
    </button>
  </aside>
</template>
