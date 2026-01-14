<script setup lang="ts">
interface Props {
  title?: string
  subtitle?: string
  noPadding?: boolean
}

withDefaults(defineProps<Props>(), {
  noPadding: false
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
    <div v-if="title || $slots.header" class="px-6 py-4 border-b border-gray-200">
      <slot name="header">
        <div>
          <h3 v-if="title" class="text-lg font-semibold text-gray-900">{{ title }}</h3>
          <p v-if="subtitle" class="text-sm text-gray-500 mt-0.5">{{ subtitle }}</p>
        </div>
      </slot>
    </div>
    <div :class="{ 'p-6': !noPadding }">
      <slot />
    </div>
    <div v-if="$slots.footer" class="px-6 py-4 border-t border-gray-200 bg-gray-50">
      <slot name="footer" />
    </div>
  </div>
</template>
