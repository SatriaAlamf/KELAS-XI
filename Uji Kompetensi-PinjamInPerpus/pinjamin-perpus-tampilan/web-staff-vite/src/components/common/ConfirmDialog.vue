<script setup lang="ts">
import { ref, Transition } from 'vue'
import BaseModal from './BaseModal.vue'
import BaseButton from './BaseButton.vue'
import { AlertTriangle, CheckCircle, XCircle, Info } from 'lucide-vue-next'

interface Props {
  isOpen: boolean
  title: string
  message: string
  type?: 'danger' | 'warning' | 'success' | 'info'
  confirmText?: string
  cancelText?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'danger',
  confirmText: 'Konfirmasi',
  cancelText: 'Batal'
})

const emit = defineEmits<{
  confirm: []
  cancel: []
}>()

const icons = {
  danger: XCircle,
  warning: AlertTriangle,
  success: CheckCircle,
  info: Info
}

const iconColors = {
  danger: 'text-red-500',
  warning: 'text-amber-500',
  success: 'text-green-500',
  info: 'text-blue-500'
}

const buttonVariants = {
  danger: 'danger',
  warning: 'warning',
  success: 'primary',
  info: 'primary'
} as const
</script>

<template>
  <BaseModal :is-open="isOpen" :title="title" size="sm" @close="emit('cancel')">
    <div class="flex flex-col items-center text-center py-4">
      <div :class="['p-3 rounded-full mb-4', type === 'danger' ? 'bg-red-100' : type === 'warning' ? 'bg-amber-100' : type === 'success' ? 'bg-green-100' : 'bg-blue-100']">
        <component :is="icons[type]" :class="['w-8 h-8', iconColors[type]]" />
      </div>
      <p class="text-gray-600">{{ message }}</p>
    </div>
    
    <template #footer>
      <div class="flex justify-end gap-3">
        <BaseButton variant="secondary" @click="emit('cancel')">
          {{ cancelText }}
        </BaseButton>
        <BaseButton :variant="buttonVariants[type]" @click="emit('confirm')">
          {{ confirmText }}
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>
