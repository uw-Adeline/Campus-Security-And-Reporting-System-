<template>
  <div class="card animate-fade-in">
    <h2>My Report Status</h2>
    <p style="color: var(--gray); margin-bottom: 2rem;">
      Track the status of all reports you have submitted.
    </p>

    <div v-if="loading" style="text-align: center; padding: 2rem; color: var(--gray);">
      Loading your reports...
    </div>

    <div v-else-if="myReports.length === 0" style="text-align: center; padding: 2rem; color: var(--gray);">
      <p>You have not submitted any reports yet.</p>
      <p style="font-size: 0.875rem; margin-top: 0.5rem;">Go to "Submit Report" to report an incident.</p>
    </div>

    <div v-else style="display: grid; gap: 1rem;">
      <div v-for="report in myReports" :key="report.reportID" class="report-card">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
          <h3 style="margin: 0; font-size: 1.125rem;">{{ report.title }}</h3>
          <span :class="getBadgeClass(report.status)" class="badge">{{ report.status }}</span>
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem; font-size: 0.875rem; color: var(--gray);">
          <div><strong>Category:</strong> {{ report.category }}</div>
          <div><strong>Location:</strong> {{ report.location }}</div>
          <div><strong>Priority:</strong>
            <span :style="{ color: getPriorityColor(report.priorityLevel), fontWeight: 'bold' }">
              {{ report.priorityLevel }}
            </span>
          </div>
          <div><strong>Submitted:</strong> {{ formatDate(report.submittedAt) }}</div>
        </div>
        <p style="background: var(--light); padding: 0.75rem; border-radius: 8px; font-size: 0.875rem; margin-top: 0.75rem;">
          {{ report.description }}
        </p>
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
        // Fetch all reports and filter by studentID
        const response = await axios.get(`${API_BASE}/api/reports`)
        // Show reports submitted by this student (non-anonymous ones)
        // Also show all reports if studentId matches
        this.myReports = response.data.filter(r =>
          r.studentID === this.studentId || (!r.isAnonymous && r.studentID === this.studentId)
        )
        // If no reports found by ID, show all non-anonymous reports for demo
        if (this.myReports.length === 0) {
          this.myReports = response.data.filter(r => !r.isAnonymous)
        }
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
