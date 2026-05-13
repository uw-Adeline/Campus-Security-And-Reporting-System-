<template>
  <div class="card animate-fade-in">
    <h2>My Notifications</h2>
    <p style="color: var(--gray); margin-bottom: 2rem;">
      Updates on your submitted reports.
    </p>

    <div v-if="loading" style="text-align: center; padding: 2rem; color: var(--gray);">
      Loading notifications...
    </div>

    <div v-else style="display: grid; gap: 0.75rem;">

      <!-- General welcome alert always shown -->
      <div style="display: flex; gap: 1rem; padding: 1rem; border-radius: 12px; border: 1px solid #DDD6FE; background: #F5F3FF;">
        <div style="width: 40px; height: 40px; border-radius: 50%; background: #EDE9FE; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 1.2rem;">
          🔔
        </div>
        <div>
          <p style="font-weight: 600; margin-bottom: 0.25rem;">Campus Security Alert</p>
          <p style="color: var(--gray); font-size: 0.875rem;">Please remain vigilant and report any suspicious activity immediately.</p>
          <p style="color: var(--gray); font-size: 0.75rem; margin-top: 0.25rem;">Today</p>
        </div>
      </div>

      <!-- Per-report notifications for THIS student only -->
      <div v-for="notif in notifications" :key="notif.id"
        :style="{ display: 'flex', gap: '1rem', padding: '1rem', borderRadius: '12px', border: '1px solid ' + notif.borderColor, background: notif.bgColor }">
        <div :style="{ width: '40px', height: '40px', borderRadius: '50%', background: notif.iconBg, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, fontSize: '1.2rem' }">
          {{ notif.icon }}
        </div>
        <div>
          <p style="font-weight: 600; margin-bottom: 0.25rem;">{{ notif.title }}</p>
          <p style="color: var(--gray); font-size: 0.875rem;">{{ notif.message }}</p>
          <p style="color: var(--gray); font-size: 0.75rem; margin-top: 0.25rem;">{{ notif.time }}</p>
        </div>
      </div>

      <!-- Empty state if no reports submitted yet -->
      <div v-if="notifications.length === 0" style="text-align: center; padding: 2rem; color: var(--gray);">
        <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📭</div>
        <p style="font-weight: 600;">No report notifications yet.</p>
        <p style="font-size: 0.875rem; margin-top: 0.5rem;">Submit a report to start receiving updates.</p>
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
      loading: true
    }
  },
  mounted() {
    this.loadNotifications()
  },
  methods: {
    async loadNotifications() {
      this.loading = true
      try {
        const response = await axios.get(`${API_BASE}/api/reports`)
        const all = response.data

        // Filter only THIS student's reports
        const myReports = all.filter(r => r.studentID === this.studentId)

        const notifs = []
        myReports.forEach(report => {
          if (report.status === 'RESOLVED') {
            notifs.push({
              id: report.reportID + '-resolved',
              icon: '✅',
              iconBg: '#D1FAE5',
              bgColor: '#F0FDF4',
              borderColor: '#BBF7D0',
              title: 'Report Resolved',
              message: `Your report "${report.title}" has been resolved by the security team.`,
              time: this.formatDate(report.submittedAt)
            })
          } else if (report.status === 'IN_PROGRESS') {
            notifs.push({
              id: report.reportID + '-progress',
              icon: '🔄',
              iconBg: '#DBEAFE',
              bgColor: '#EFF6FF',
              borderColor: '#BFDBFE',
              title: 'Report In Progress',
              message: `Your report "${report.title}" is being handled by the security team.`,
              time: this.formatDate(report.submittedAt)
            })
          } else if (report.status === 'PENDING') {
            notifs.push({
              id: report.reportID + '-pending',
              icon: '📋',
              iconBg: '#FEF3C7',
              bgColor: '#FFFBEB',
              borderColor: '#FDE68A',
              title: 'Report Received',
              message: `Your report "${report.title}" has been received and is pending review.`,
              time: this.formatDate(report.submittedAt)
            })
          } else if (report.status === 'DISMISSED') {
            notifs.push({
              id: report.reportID + '-dismissed',
              icon: '❌',
              iconBg: '#FEE2E2',
              bgColor: '#FFF5F5',
              borderColor: '#FECACA',
              title: 'Report Dismissed',
              message: `Your report "${report.title}" was reviewed and dismissed.`,
              time: this.formatDate(report.submittedAt)
            })
          }
        })

        this.notifications = notifs
      } catch (error) {
        console.error('Error loading notifications:', error)
      } finally {
        this.loading = false
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      return new Date(dateString).toLocaleString()
    }
  }
}
</script>
