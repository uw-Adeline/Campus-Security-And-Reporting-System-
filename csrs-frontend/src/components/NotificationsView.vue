<template>
  <div class="card animate-fade-in">
    <h2>Notifications</h2>
    <p style="color: var(--gray); margin-bottom: 2rem;">
      Updates on your submitted reports and campus security alerts.
    </p>

    <div style="display: grid; gap: 1rem;">
      <!-- Dynamic notifications based on report statuses -->
      <div v-if="loadingReports" style="text-align: center; padding: 2rem; color: var(--gray);">
        Loading notifications...
      </div>

      <div v-else>
        <!-- Show notifications for resolved/in-progress reports -->
        <div v-for="notif in notifications" :key="notif.id"
          style="display: flex; gap: 1rem; padding: 1rem; border-radius: 12px; border: 1px solid var(--border); background: white; margin-bottom: 0.75rem;">
          <div :style="{ width: '40px', height: '40px', borderRadius: '50%', background: notif.color, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }">
            <span style="font-size: 1.2rem;">{{ notif.icon }}</span>
          </div>
          <div>
            <p style="font-weight: 600; margin-bottom: 0.25rem;">{{ notif.title }}</p>
            <p style="color: var(--gray); font-size: 0.875rem;">{{ notif.message }}</p>
            <p style="color: var(--gray); font-size: 0.75rem; margin-top: 0.25rem;">{{ notif.time }}</p>
          </div>
        </div>

        <div v-if="notifications.length === 0" style="text-align: center; padding: 2rem; color: var(--gray);">
          No notifications yet. Submit a report to get started.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

export default {
  name: 'NotificationsView',
  props: ['studentId'],
  data() {
    return {
      notifications: [],
      loadingReports: true
    }
  },
  mounted() {
    this.loadNotifications()
  },
  methods: {
    async loadNotifications() {
      this.loadingReports = true
      try {
        const response = await axios.get(`${API_BASE}/api/reports`)
        const reports = response.data

        const notifs = []

        // Generate notifications based on report statuses
        reports.forEach(report => {
          if (report.status === 'RESOLVED') {
            notifs.push({
              id: report.reportID + '-resolved',
              icon: '✅',
              color: '#D1FAE5',
              title: 'Report Resolved',
              message: `Your report "${report.title}" has been resolved by the security team.`,
              time: this.formatDate(report.submittedAt)
            })
          } else if (report.status === 'IN_PROGRESS') {
            notifs.push({
              id: report.reportID + '-progress',
              icon: '🔄',
              color: '#DBEAFE',
              title: 'Report In Progress',
              message: `Your report "${report.title}" is being handled by the security team.`,
              time: this.formatDate(report.submittedAt)
            })
          } else if (report.status === 'PENDING') {
            notifs.push({
              id: report.reportID + '-pending',
              icon: '📋',
              color: '#FEF3C7',
              title: 'Report Received',
              message: `Your report "${report.title}" has been received and is pending review.`,
              time: this.formatDate(report.submittedAt)
            })
          }
        })

        // Add a general campus alert
        notifs.unshift({
          id: 'general-alert',
          icon: '🔔',
          color: '#EDE9FE',
          title: 'Campus Security Alert',
          message: 'Please remain vigilant and report any suspicious activity immediately.',
          time: 'Today'
        })

        this.notifications = notifs
      } catch (error) {
        console.error('Error loading notifications:', error)
        // Show default notification if API fails
        this.notifications = [{
          id: 'welcome',
          icon: '👋',
          color: '#EDE9FE',
          title: 'Welcome to CSRS',
          message: 'You can submit security reports and track their status here.',
          time: 'Now'
        }]
      } finally {
        this.loadingReports = false
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      return new Date(dateString).toLocaleString()
    }
  }
}
</script>
