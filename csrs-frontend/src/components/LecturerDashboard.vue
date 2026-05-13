<template>
  <div class="card animate-fade-in">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
      <div>
        <h2>Lecturer — Reports Dashboard</h2>
        <p style="color: var(--gray); font-size: 0.9rem;">View-only access to campus security reports.</p>
      </div>
      <button @click="fetchReports" class="btn btn-secondary">Refresh</button>
    </div>

    <!-- Stats summary -->
    <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 2rem;">
      <div style="background: #FEF3C7; padding: 1rem; border-radius: 12px; text-align: center;">
        <div style="font-size: 1.5rem; font-weight: 700; color: #D97706;">{{ countByStatus('PENDING') }}</div>
        <div style="font-size: 0.8rem; color: #92400E;">Pending</div>
      </div>
      <div style="background: #DBEAFE; padding: 1rem; border-radius: 12px; text-align: center;">
        <div style="font-size: 1.5rem; font-weight: 700; color: #2563EB;">{{ countByStatus('IN_PROGRESS') }}</div>
        <div style="font-size: 0.8rem; color: #1E40AF;">In Progress</div>
      </div>
      <div style="background: #D1FAE5; padding: 1rem; border-radius: 12px; text-align: center;">
        <div style="font-size: 1.5rem; font-weight: 700; color: #059669;">{{ countByStatus('RESOLVED') }}</div>
        <div style="font-size: 0.8rem; color: #065F46;">Resolved</div>
      </div>
      <div style="background: #F3F4F6; padding: 1rem; border-radius: 12px; text-align: center;">
        <div style="font-size: 1.5rem; font-weight: 700; color: #374151;">{{ reports.length }}</div>
        <div style="font-size: 0.8rem; color: #6B7280;">Total</div>
      </div>
    </div>

    <div v-if="loading" style="text-align: center; padding: 2rem; color: var(--gray);">
      Loading reports...
    </div>

    <div v-else-if="reports.length === 0" style="text-align: center; padding: 2rem; color: var(--gray);">
      No reports found in the system.
    </div>

    <div v-else style="display: grid; gap: 1rem;">
      <div v-for="report in sortedReports" :key="report.reportID" class="report-card">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
          <h3 style="margin: 0; font-size: 1.125rem;">{{ report.title }}</h3>
          <span :class="getBadgeClass(report.status)" class="badge">{{ report.status }}</span>
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem; font-size: 0.875rem; color: var(--gray); margin-bottom: 0.75rem;">
          <div><strong>Category:</strong> {{ report.category }}</div>
          <div><strong>Location:</strong> {{ report.location }}</div>
          <div><strong>Priority:</strong>
            <span :style="{ color: getPriorityColor(report.priorityLevel), fontWeight: 'bold' }">
              {{ report.priorityLevel }}
            </span>
          </div>
          <div><strong>Submitted:</strong> {{ formatDate(report.submittedAt) }}</div>
        </div>
        <p style="background: var(--light); padding: 0.75rem; border-radius: 8px; font-size: 0.875rem;">
          {{ report.description }}
        </p>
        <!-- VIEW ONLY notice -->
        <div style="margin-top: 0.75rem; padding: 0.5rem 0.75rem; background: #F0F9FF; border-radius: 6px; font-size: 0.8rem; color: #0369A1;">
          👁️ View Only — Contact security admin to update this report
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

export default {
  name: 'LecturerDashboard',
  data() {
    return {
      reports: [],
      loading: true
    }
  },
  computed: {
    sortedReports() {
      return [...this.reports].sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
    }
  },
  mounted() {
    this.fetchReports()
  },
  methods: {
    async fetchReports() {
      this.loading = true
      try {
        const response = await axios.get(`${API_BASE}/api/reports`)
        this.reports = response.data
      } catch (error) {
        console.error('Error fetching reports:', error)
      } finally {
        this.loading = false
      }
    },
    countByStatus(status) {
      return this.reports.filter(r => r.status === status).length
    },
    getBadgeClass(status) {
      if (status === 'PENDING') return 'badge-pending'
      if (status === 'IN_PROGRESS') return 'badge-progress'
      if (status === 'RESOLVED') return 'badge-resolved'
      return 'badge-secondary'
    },
    getPriorityColor(priority) {
      if (priority === 'CRITICAL') return '#DC2626'
      if (priority === 'HIGH') return '#EA580C'
      if (priority === 'MEDIUM') return '#D97706'
      return '#059669'
    },
    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      return new Date(dateString).toLocaleString()
    }
  }
}
</script>

<style scoped>
.report-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.5rem;
  background: white;
}
</style>
