<template>
  <div class="card animate-fade-in">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
      <h2>Security Reports Dashboard</h2>
      <button @click="fetchReports" class="btn btn-secondary">Refresh</button>
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
        
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem; font-size: 0.875rem; color: var(--gray); margin-bottom: 1rem;">
          <div><strong>Category:</strong> {{ report.category }}</div>
          <div><strong>Location:</strong> {{ report.location }}</div>
          <div><strong>Priority:</strong> <span :style="{ color: getPriorityColor(report.priorityLevel), fontWeight: 'bold' }">{{ report.priorityLevel }}</span></div>
          <div><strong>Submitted:</strong> {{ formatDate(report.submittedAt) }}</div>
        </div>
        
        <p style="background: var(--light); padding: 1rem; border-radius: 8px; font-size: 0.9rem; margin-bottom: 1rem;">
          {{ report.description }}
        </p>

        <div style="display: flex; gap: 1rem; align-items: center; border-top: 1px solid var(--border); padding-top: 1rem;">
          <strong style="font-size: 0.875rem;">Admin Action: Update Status</strong>
          <select v-model="report.status" @change="updateStatus(report.reportID, report.status)" style="width: auto; padding: 0.25rem 0.5rem;">
            <option value="PENDING">Pending</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="DISMISSED">Dismissed</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      reports: [],
      loading: true
    };
  },
  computed: {
    sortedReports() {
      // Sort newest first
      return [...this.reports].sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt));
    }
  },
  mounted() {
    this.fetchReports();
  },
  methods: {
    async fetchReports() {
      this.loading = true;
      try {
        const response = await axios.get('/api/reports');
        this.reports = response.data;
      } catch (error) {
        console.error('Error fetching reports:', error);
      } finally {
        this.loading = false;
      }
    },
    async updateStatus(id, newStatus) {
      try {
        await axios.patch(`/api/reports/${id}/status?status=${newStatus}`);
        // Optionally show a toast notification here
      } catch (error) {
        console.error('Error updating status:', error);
        alert('Failed to update status');
        this.fetchReports(); // Revert to old status
      }
    },
    getBadgeClass(status) {
      if (status === 'PENDING') return 'badge-pending';
      if (status === 'IN_PROGRESS') return 'badge-progress';
      if (status === 'RESOLVED') return 'badge-resolved';
      return 'badge-secondary';
    },
    getPriorityColor(priority) {
      if (priority === 'CRITICAL') return '#DC2626';
      if (priority === 'HIGH') return '#EA580C';
      if (priority === 'MEDIUM') return '#D97706';
      return '#059669';
    },
    formatDate(dateString) {
      if (!dateString) return 'Unknown';
      const date = new Date(dateString);
      return date.toLocaleString();
    }
  }
};
</script>

<style scoped>
.report-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.5rem;
  background: white;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.report-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
</style>
