<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMockStore } from '@/stores/mockData'
import { StatsCard, BaseCard, BaseBadge, BaseButton } from '@/components/common'
import { LineChart, BarChart, DoughnutChart } from '@/components/charts'
import { BookOpen, Users, ArrowLeftRight, AlertTriangle, Plus, RefreshCw, Eye } from 'lucide-vue-next'
import { format } from 'date-fns'
import { id } from 'date-fns/locale'

const router = useRouter()
const mockStore = useMockStore()

const stats = computed(() => mockStore.dashboardStats)
const recentTransactions = computed(() => mockStore.recentTransactions)
const borrowingTrends = computed(() => mockStore.borrowingTrends)
const categoryDistribution = computed(() => mockStore.categoryDistribution)
const topBooks = computed(() => mockStore.topBorrowedBooks)

// Chart data
const trendLabels = computed(() => 
  borrowingTrends.value.map(t => format(new Date(t.date), 'dd MMM', { locale: id }))
)
const trendDatasets = computed(() => [
  {
    label: 'Peminjaman',
    data: borrowingTrends.value.map(t => t.borrowed),
    borderColor: '#10b981',
    backgroundColor: 'rgba(16, 185, 129, 0.1)'
  },
  {
    label: 'Pengembalian',
    data: borrowingTrends.value.map(t => t.returned),
    borderColor: '#3b82f6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)'
  }
])

const categoryLabels = computed(() => categoryDistribution.value.map(c => c.label))
const categoryData = computed(() => categoryDistribution.value.map(c => c.value))

const topBookLabels = computed(() => topBooks.value.map(b => b.label.length > 15 ? b.label.substring(0, 15) + '...' : b.label))
const topBookData = computed(() => topBooks.value.map(b => b.value))

const getStatusVariant = (status: string) => {
  switch (status) {
    case 'active': return 'info'
    case 'returned': return 'success'
    case 'overdue': return 'danger'
    default: return 'default'
  }
}

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'active': return 'Aktif'
    case 'returned': return 'Dikembalikan'
    case 'overdue': return 'Telat'
    default: return status
  }
}

const formatDate = (date: string) => format(new Date(date), 'dd MMM yyyy', { locale: id })
const formatCurrency = (value: number) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(value)
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p class="text-gray-500">Selamat datang kembali! Berikut ringkasan perpustakaan hari ini.</p>
      </div>
      <div class="flex items-center gap-3">
        <BaseButton variant="secondary" @click="router.push('/transactions')">
          <RefreshCw class="w-4 h-4" />
          Proses Pengembalian
        </BaseButton>
        <BaseButton @click="router.push('/books')">
          <Plus class="w-4 h-4" />
          Tambah Buku
        </BaseButton>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <StatsCard
        title="Total Buku"
        :value="stats.totalBooks"
        :change="stats.booksChange"
        :icon="BookOpen"
        icon-bg-color="bg-primary-100"
      />
      <StatsCard
        title="Member Aktif"
        :value="stats.activeMembers"
        :change="stats.membersChange"
        :icon="Users"
        icon-bg-color="bg-blue-100"
      />
      <StatsCard
        title="Buku Dipinjam"
        :value="stats.borrowedBooks"
        :change="stats.borrowedChange"
        :icon="ArrowLeftRight"
        icon-bg-color="bg-amber-100"
      />
      <StatsCard
        title="Total Tunggakan"
        :value="formatCurrency(stats.totalFines)"
        :change="stats.finesChange"
        :icon="AlertTriangle"
        icon-bg-color="bg-red-100"
      />
    </div>

    <!-- Charts Row -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Trend Chart -->
      <BaseCard title="Tren Peminjaman (7 Hari)" class="lg:col-span-2">
        <LineChart
          :labels="trendLabels"
          :datasets="trendDatasets"
          :height="280"
        />
      </BaseCard>

      <!-- Category Distribution -->
      <BaseCard title="Distribusi Kategori">
        <DoughnutChart
          :labels="categoryLabels"
          :data="categoryData"
          :height="280"
        />
      </BaseCard>
    </div>

    <!-- Bottom Row -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Top Books -->
      <BaseCard title="Buku Terpopuler">
        <BarChart
          :labels="topBookLabels"
          :data="topBookData"
          label="Dipinjam"
          :height="280"
          :horizontal="true"
        />
      </BaseCard>

      <!-- Recent Activity -->
      <BaseCard title="Aktivitas Terbaru" :no-padding="true" class="lg:col-span-2">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Member</th>
                <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Buku</th>
                <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Tanggal</th>
                <th class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Status</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="tx in recentTransactions" :key="tx.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ tx.memberName }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {{ tx.bookTitle.length > 25 ? tx.bookTitle.substring(0, 25) + '...' : tx.bookTitle }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(tx.borrowDate) }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <BaseBadge :variant="getStatusVariant(tx.status)">
                    {{ getStatusLabel(tx.status) }}
                  </BaseBadge>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <template #footer>
          <div class="flex justify-end">
            <BaseButton variant="ghost" size="sm" @click="router.push('/transactions')">
              <Eye class="w-4 h-4" />
              Lihat Semua
            </BaseButton>
          </div>
        </template>
      </BaseCard>
    </div>
  </div>
</template>
