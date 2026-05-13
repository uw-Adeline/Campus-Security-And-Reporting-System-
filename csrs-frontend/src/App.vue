<template>
  <div>
    <!-- LOGGED OUT: Show Login/Register -->
    <div v-if="!currentUser">
      <LoginRegister @login="handleLogin" />
    </div>

    <!-- LOGGED IN -->
    <div v-else>
      <!-- Navigation Bar -->
      <nav class="navbar">
        <div class="brand">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          AUCA CSRS
        </div>

        <div style="display: flex; align-items: center; gap: 1rem;">
          <!-- STUDENT nav -->
          <template v-if="currentUser.role === 'student'">
            <button @click="currentView = 'submit'"
              :class="['btn', currentView === 'submit' ? 'btn-primary' : 'btn-secondary']">
              Submit Report
            </button>
            <button @click="currentView = 'mystatus'"
              :class="['btn', currentView === 'mystatus' ? 'btn-primary' : 'btn-secondary']">
              My Report Status
            </button>
            <button @click="currentView = 'notifications'"
              :class="['btn', currentView === 'notifications' ? 'btn-primary' : 'btn-secondary']">
              Notifications
            </button>
          </template>

          <!-- LECTURER nav -->
          <template v-if="currentUser.role === 'lecturer'">
            <button @click="currentView = 'lecturerdash'"
              :class="['btn', currentView === 'lecturerdash' ? 'btn-primary' : 'btn-secondary']">
              View Reports
            </button>
          </template>

          <!-- ADMIN nav -->
          <template v-if="currentUser.role === 'admin'">
            <button @click="currentView = 'admin'"
              :class="['btn', currentView === 'admin' ? 'btn-primary' : 'btn-secondary']">
              Admin Dashboard
            </button>
          </template>

          <!-- User info + Logout -->
          <span style="color: var(--gray); font-size: 0.875rem;">
            👤 {{ currentUser.name }} ({{ currentUser.role }})
          </span>
          <button @click="handleLogout" class="btn btn-secondary" style="padding: 0.5rem 1rem;">
            Logout
          </button>
        </div>
      </nav>

      <!-- Main Content -->
      <main class="container">
        <div style="text-align: center; margin-bottom: 2rem; margin-top: 2rem;">
          <h1>Campus Security & Reporting System</h1>
          <p style="color: var(--gray); font-size: 1.125rem;">
            Welcome, {{ currentUser.name }}!
          </p>
        </div>

        <!-- STUDENT VIEWS -->
        <ReportForm
          v-if="currentUser.role === 'student' && currentView === 'submit'"
          :studentId="currentUser.id" />

        <MyReportStatus
          v-if="currentUser.role === 'student' && currentView === 'mystatus'"
          :studentId="currentUser.id" />

        <NotificationsView
          v-if="currentUser.role === 'student' && currentView === 'notifications'"
          :studentId="currentUser.id" />

        <!-- LECTURER VIEW -->
        <LecturerDashboard
          v-if="currentUser.role === 'lecturer' && currentView === 'lecturerdash'" />

        <!-- ADMIN VIEW -->
        <AdminDashboard
          v-if="currentUser.role === 'admin' && currentView === 'admin'" />
      </main>
    </div>
  </div>
</template>

<script>
import LoginRegister from './components/LoginRegister.vue'
import ReportForm from './components/ReportForm.vue'
import MyReportStatus from './components/MyReportStatus.vue'
import NotificationsView from './components/NotificationsView.vue'
import LecturerDashboard from './components/LecturerDashboard.vue'
import AdminDashboard from './components/AdminDashboard.vue'

export default {
  name: 'App',
  components: {
    LoginRegister,
    ReportForm,
    MyReportStatus,
    NotificationsView,
    LecturerDashboard,
    AdminDashboard
  },
  data() {
    return {
      currentUser: null, // null = not logged in
      currentView: ''
    }
  },
  methods: {
    handleLogin(user) {
      this.currentUser = user
      // Set default view based on role
      if (user.role === 'student') this.currentView = 'submit'
      if (user.role === 'lecturer') this.currentView = 'lecturerdash'
      if (user.role === 'admin') this.currentView = 'admin'
    },
    handleLogout() {
      this.currentUser = null
      this.currentView = ''
    }
  }
}
</script>
