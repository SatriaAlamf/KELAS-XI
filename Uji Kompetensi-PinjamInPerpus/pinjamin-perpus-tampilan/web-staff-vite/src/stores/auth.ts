import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Staff, AuthState, SystemSettings } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const staff = ref<Staff | null>(null)

  const login = (email: string, _password: string) => {
    // Mock login - always succeeds
    staff.value = {
      id: 1,
      name: 'Admin Perpustakaan',
      email: email,
      phone: '081234567890',
      photo: 'https://ui-avatars.com/api/?name=Admin+Perpus&background=10b981&color=fff',
      role: 'Staff'
    }
    isAuthenticated.value = true
    localStorage.setItem('auth', JSON.stringify({ isAuthenticated: true, staff: staff.value }))
  }

  const logout = () => {
    staff.value = null
    isAuthenticated.value = false
    localStorage.removeItem('auth')
  }

  const initAuth = () => {
    const saved = localStorage.getItem('auth')
    if (saved) {
      const data = JSON.parse(saved) as AuthState
      isAuthenticated.value = data.isAuthenticated
      staff.value = data.staff
    }
  }

  return { isAuthenticated, staff, login, logout, initAuth }
})

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<SystemSettings>({
    borrowDuration: 7,
    finePerDay: 1000,
    maxBooksPerMember: 3,
    emailNotifications: true,
    maintenanceMode: false
  })

  const updateSettings = (newSettings: Partial<SystemSettings>) => {
    settings.value = { ...settings.value, ...newSettings }
    localStorage.setItem('settings', JSON.stringify(settings.value))
  }

  const initSettings = () => {
    const saved = localStorage.getItem('settings')
    if (saved) {
      settings.value = JSON.parse(saved)
    }
  }

  return { settings, updateSettings, initSettings }
})
