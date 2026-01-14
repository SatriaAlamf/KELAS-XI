<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore, useSettingsStore } from '@/stores/auth'

const authStore = useAuthStore()
const settingsStore = useSettingsStore()

onMounted(() => {
  // Initialize stores from localStorage
  authStore.initAuth()
  settingsStore.initSettings()
})
</script>

<template>
  <router-view />
</template>
