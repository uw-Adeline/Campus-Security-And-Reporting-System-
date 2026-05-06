<template>
  <div>
    <!-- Navigation Bar -->
    <nav class="navbar">
      <div class="brand">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
        </svg>
        AUCA CSRS
      </div>
      <div style="display: flex; gap: 1rem;">
        <button 
          @click="currentView = 'student'" 
          :class="['btn', currentView === 'student' ? 'btn-primary' : 'btn-secondary']">
          Student Portal
        </button>
        <button 
          @click="currentView = 'admin'" 
          :class="['btn', currentView === 'admin' ? 'btn-primary' : 'btn-secondary']">
          Admin Dashboard
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="container">
      <div style="text-align: center; margin-bottom: 3rem; margin-top: 2rem;">
        <h1>Campus Security & Reporting System</h1>
        <p style="color: var(--gray); font-size: 1.125rem;">
          {{ currentView === 'student' ? 'Report incidents quickly and anonymously.' : 'Manage and track security incidents across the campus.' }}
        </p>
      </div>

      <!-- Views -->
      <ReportForm v-if="currentView === 'student'" />
      <AdminDashboard v-if="currentView === 'admin'" />
    </main>
  </div>
</template>

<script>
import ReportForm from './components/ReportForm.vue'
import AdminDashboard from './components/AdminDashboard.vue'

export default {
  name: 'App',
  components: {
    ReportForm,
    AdminDashboard
  },
  data() {
    return {
      currentView: 'student' // 'student' or 'admin'
    }
  }
}
</script>
