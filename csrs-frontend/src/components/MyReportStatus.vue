<template>
  <div class="card animate-fade-in">
    <h2>My Report Status</h2>
    <p style="color: var(--gray); margin-bottom: 2rem;">
      Track the status of all reports you have submitted.
    </p>

    <div v-if="loading" style="text-align: center; padding: 2rem; color: var(--gray);">
      Loading your reports...
    </div>

    <div v-else-if="myReports.length === 0" style="text-align: center; padding: 3rem; color: var(--gray);">
      <div style="font-size: 3rem; margin-bottom: 1rem;">📋</div>
      <p style="font-weight: 600;">No reports found.</p>
      <p style="font-size: 0.875rem; margin-top: 0.5rem;">
        Go to <strong>Submit Report</strong> to report an incident.
      </p>
    </div>

    <div v-else style="display: grid; gap: 1rem;">
      <div v-for="report in myReports" :key="report.reportID" class="report-card">
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
        <!-- Anonymous badge -->
        <div v-if="report.isAnonymous" style="margin-top: 0.5rem; font-size: 0.75rem; color: var(--gray);">
          🔒 Submitted anonymously
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

export default {
  name: 'MyReportStatus',
  props: ['studentId'],
  data() {
    return {
      myReports: [],
      loading: true
    }
  },
  mounted() {
    this.fetchMyReports()
  },
  methods: {
    async fetchMyReports() {
      this.loading = true
      try {
        const response = await axios.get(`${API_BASE}/api/reports`)
        const all = response.data
        // Show only reports where studentID matches this logged-in student
        this.myReports = all.filter(r => r.studentID === this.studentId)
      } catch (error) {
        console.error('Error fetching reports:', error)
      } finally {
        this.loading = false
      }
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
