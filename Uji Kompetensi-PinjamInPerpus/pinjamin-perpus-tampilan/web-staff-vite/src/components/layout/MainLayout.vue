<script setup lang="ts">
import TheSidebar from './TheSidebar.vue'
import TheHeader from './TheHeader.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <TheSidebar />
    
    <div class="lg:ml-64 transition-all duration-300">
      <TheHeader />
      
      <main class="p-6">
        <router-view v-slot="{ Component }">
          <Transition
            mode="out-in"
            enter-active-class="transition-opacity duration-200"
            enter-from-class="opacity-0"
            enter-to-class="opacity-100"
            leave-active-class="transition-opacity duration-200"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0"
          >
            <component :is="Component" />
          </Transition>
        </router-view>
      </main>
    </div>
  </div>
</template>
