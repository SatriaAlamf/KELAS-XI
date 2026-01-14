<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-vue-next'

interface Column {
  key: string
  label: string
  sortable?: boolean
  width?: string
}

interface Props {
  columns: Column[]
  data: any[]
  loading?: boolean
  emptyMessage?: string
  perPage?: number
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  emptyMessage: 'Tidak ada data',
  perPage: 10
})

const emit = defineEmits<{
  rowClick: [row: any]
}>()

const currentPage = ref(1)
const sortKey = ref<string | null>(null)
const sortOrder = ref<'asc' | 'desc'>('asc')

const sortedData = computed(() => {
  if (!sortKey.value) return props.data
  
  return [...props.data].sort((a, b) => {
    const aVal = a[sortKey.value!]
    const bVal = b[sortKey.value!]
    
    if (aVal < bVal) return sortOrder.value === 'asc' ? -1 : 1
    if (aVal > bVal) return sortOrder.value === 'asc' ? 1 : -1
    return 0
  })
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * props.perPage
  const end = start + props.perPage
  return sortedData.value.slice(start, end)
})

const totalPages = computed(() => Math.ceil(props.data.length / props.perPage))

const handleSort = (key: string, sortable?: boolean) => {
  if (!sortable) return
  
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortOrder.value = 'asc'
  }
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

watch(() => props.data, () => {
  currentPage.value = 1
})
</script>

<template>
  <div class="overflow-hidden">
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th
              v-for="column in columns"
              :key="column.key"
              :style="{ width: column.width }"
              :class="[
                'px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider',
                column.sortable ? 'cursor-pointer hover:bg-gray-100 select-none' : ''
              ]"
              @click="handleSort(column.key, column.sortable)"
            >
              <div class="flex items-center gap-1">
                {{ column.label }}
                <template v-if="column.sortable">
                  <ArrowUp v-if="sortKey === column.key && sortOrder === 'asc'" class="w-4 h-4" />
                  <ArrowDown v-else-if="sortKey === column.key && sortOrder === 'desc'" class="w-4 h-4" />
                  <ArrowUpDown v-else class="w-4 h-4 text-gray-400" />
                </template>
              </div>
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <!-- Loading skeleton -->
          <template v-if="loading">
            <tr v-for="i in perPage" :key="i">
              <td v-for="col in columns" :key="col.key" class="px-6 py-4">
                <div class="h-4 bg-gray-200 rounded animate-pulse" :class="col.key === 'actions' ? 'w-20' : 'w-full'"></div>
              </td>
            </tr>
          </template>
          
          <!-- Empty state -->
          <template v-else-if="data.length === 0">
            <tr>
              <td :colspan="columns.length" class="px-6 py-16 text-center">
                <div class="flex flex-col items-center">
                  <svg class="w-16 h-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <p class="mt-4 text-gray-500">{{ emptyMessage }}</p>
                </div>
              </td>
            </tr>
          </template>
          
          <!-- Data rows -->
          <template v-else>
            <tr
              v-for="(row, index) in paginatedData"
              :key="index"
              class="hover:bg-gray-50 transition-colors"
              @click="emit('rowClick', row)"
            >
              <td v-for="column in columns" :key="column.key" class="px-6 py-4 whitespace-nowrap">
                <slot :name="column.key" :row="row" :value="row[column.key]">
                  {{ row[column.key] }}
                </slot>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    
    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-between px-6 py-4 border-t border-gray-200 bg-gray-50">
      <div class="text-sm text-gray-500">
        Menampilkan {{ (currentPage - 1) * perPage + 1 }} - {{ Math.min(currentPage * perPage, data.length) }} dari {{ data.length }} data
      </div>
      <div class="flex items-center gap-1">
        <button
          @click="goToPage(1)"
          :disabled="currentPage === 1"
          class="p-2 rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronsLeft class="w-4 h-4" />
        </button>
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="p-2 rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronLeft class="w-4 h-4" />
        </button>
        
        <template v-for="page in totalPages" :key="page">
          <button
            v-if="page === 1 || page === totalPages || (page >= currentPage - 1 && page <= currentPage + 1)"
            @click="goToPage(page)"
            :class="[
              'px-3 py-1 rounded-lg text-sm font-medium transition-colors',
              currentPage === page
                ? 'bg-primary-500 text-white'
                : 'hover:bg-gray-200 text-gray-700'
            ]"
          >
            {{ page }}
          </button>
          <span
            v-else-if="page === currentPage - 2 || page === currentPage + 2"
            class="px-2 text-gray-400"
          >
            ...
          </span>
        </template>
        
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="p-2 rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronRight class="w-4 h-4" />
        </button>
        <button
          @click="goToPage(totalPages)"
          :disabled="currentPage === totalPages"
          class="p-2 rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronsRight class="w-4 h-4" />
        </button>
      </div>
    </div>
  </div>
</template>
