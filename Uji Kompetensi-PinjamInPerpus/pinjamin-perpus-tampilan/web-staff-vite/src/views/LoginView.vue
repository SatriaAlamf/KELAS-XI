<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { BaseButton, BaseInput } from '@/components/common'
import { Library, Eye, EyeOff } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('admin@perpus.com')
const password = ref('password')
const showPassword = ref(false)
const isLoading = ref(false)

const handleLogin = async () => {
  isLoading.value = true
  
  // Simulate API call
  await new Promise(resolve => setTimeout(resolve, 800))
  
  authStore.login(email.value, password.value)
  router.push('/dashboard')
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-50 via-white to-primary-100 flex items-center justify-center p-4">
    <!-- Background Decoration -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-primary-200 rounded-full opacity-30 blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-primary-300 rounded-full opacity-30 blur-3xl"></div>
    </div>

    <div class="w-full max-w-md relative">
      <!-- Login Card -->
      <div class="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
        <!-- Logo & Title -->
        <div class="text-center mb-8">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-2xl mb-4">
            <Library class="w-8 h-8 text-primary-600" />
          </div>
          <h1 class="text-2xl font-bold text-gray-900">PinjamIn Perpus</h1>
          <p class="text-gray-500 mt-1">Staff Dashboard</p>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin" class="space-y-5">
          <BaseInput
            v-model="email"
            label="Email"
            type="email"
            placeholder="Masukkan email"
            required
          />

          <div class="relative">
            <BaseInput
              v-model="password"
              label="Password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Masukkan password"
              required
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-9 text-gray-400 hover:text-gray-600"
            >
              <Eye v-if="!showPassword" class="w-5 h-5" />
              <EyeOff v-else class="w-5 h-5" />
            </button>
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" class="w-4 h-4 rounded border-gray-300 text-primary-500 focus:ring-primary-500" />
              <span class="text-gray-600">Ingat saya</span>
            </label>
            <a href="#" class="text-primary-600 hover:text-primary-700 font-medium">Lupa password?</a>
          </div>

          <BaseButton
            type="submit"
            class="w-full"
            size="lg"
            :loading="isLoading"
          >
            Masuk
          </BaseButton>
        </form>

        <!-- Demo Credentials -->
        <div class="mt-6 p-4 bg-gray-50 rounded-xl">
          <p class="text-xs text-gray-500 text-center mb-2">Demo credentials:</p>
          <p class="text-sm text-gray-600 text-center">
            Email: <span class="font-mono text-primary-600">admin@perpus.com</span>
          </p>
          <p class="text-sm text-gray-600 text-center">
            Password: <span class="font-mono text-primary-600">password</span>
          </p>
        </div>
      </div>

      <!-- Footer -->
      <p class="text-center text-sm text-gray-500 mt-6">
        &copy; 2026 PinjamIn Perpus. All rights reserved.
      </p>
    </div>
  </div>
</template>
