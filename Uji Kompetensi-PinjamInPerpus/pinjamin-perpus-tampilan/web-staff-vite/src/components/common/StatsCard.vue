<script setup lang="ts">
import { computed } from 'vue'
import { TrendingUp, TrendingDown } from 'lucide-vue-next'

interface Props {
  title: string
  value: string | number
  change?: number
  icon?: any
  iconBgColor?: string
  valuePrefix?: string
}

const props = withDefaults(defineProps<Props>(), {
  iconBgColor: 'bg-primary-100'
})

const isPositiveChange = computed(() => props.change && props.change > 0)
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow duration-200">
    <div class="flex items-center justify-between">
      <div class="flex-1">
        <p class="text-sm font-medium text-gray-500">{{ title }}</p>
        <p class="text-2xl font-bold text-gray-900 mt-1">
          {{ valuePrefix }}{{ typeof value === 'number' ? value.toLocaleString('id-ID') : value }}
        </p>
        <div v-if="change !== undefined" class="flex items-center mt-2 text-sm">
          <TrendingUp v-if="isPositiveChange" class="w-4 h-4 text-green-500 mr-1" />
          <TrendingDown v-else class="w-4 h-4 text-red-500 mr-1" />
          <span :class="isPositiveChange ? 'text-green-600' : 'text-red-600'">
            {{ isPositiveChange ? '+' : '' }}{{ change }}%
          </span>
          <span class="text-gray-400 ml-1">dari bulan lalu</span>
        </div>
      </div>
      <div v-if="icon" :class="['p-3 rounded-xl', iconBgColor]">
        <component :is="icon" class="w-6 h-6 text-primary-600" />
      </div>
    </div>
  </div>
</template>
