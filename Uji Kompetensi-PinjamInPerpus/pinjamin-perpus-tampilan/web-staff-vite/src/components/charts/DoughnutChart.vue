<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

ChartJS.register(
  ArcElement,
  Title,
  Tooltip,
  Legend
)

interface Props {
  labels: string[]
  data: number[]
  backgroundColor?: string[]
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  backgroundColor: () => [
    '#10b981',
    '#3b82f6',
    '#f59e0b',
    '#ef4444',
    '#8b5cf6',
    '#ec4899',
    '#06b6d4',
    '#84cc16'
  ],
  height: 300
})

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      data: props.data,
      backgroundColor: props.backgroundColor,
      borderWidth: 0,
      hoverOffset: 8
    }
  ]
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '60%',
  plugins: {
    legend: {
      position: 'right' as const,
      labels: {
        usePointStyle: true,
        padding: 16,
        font: {
          size: 12
        }
      }
    }
  }
}
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <Doughnut :data="chartData" :options="chartOptions" />
  </div>
</template>
