<template>
  <div class="card animate-fade-in" style="max-width: 600px; margin: 0 auto;">
    <h2>Submit a Security Report</h2>
    <p style="color: var(--gray); margin-bottom: 2rem;">Please provide details about the incident.</p>

    <form @submit.prevent="submitReport">
      <div class="form-group">
        <label for="title">Incident Title</label>
        <input type="text" id="title" v-model="form.title" required placeholder="e.g., Suspicious Activity at Gate 2" />
      </div>

      <div class="form-group">
        <label for="category">Category</label>
        <select id="category" v-model="form.category" required>
          <option value="" disabled>Select a category</option>
          <option value="Theft">Theft / Vandalism</option>
          <option value="Suspicious Activity">Suspicious Activity</option>
          <option value="Harassment">Harassment / Bullying</option>
          <option value="Medical Emergency">Medical Emergency</option>
          <option value="Other">Other</option>
        </select>
      </div>

      <div class="form-group">
        <label for="location">Location on Campus</label>
        <input type="text" id="location" v-model="form.location" required placeholder="e.g., Main Library, 2nd Floor" />
      </div>

      <div class="form-group">
        <label for="priority">Priority Level</label>
        <select id="priority" v-model="form.priorityLevel">
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </div>

      <div class="form-group">
        <label for="description">Detailed Description</label>
        <textarea id="description" v-model="form.description" rows="4" required placeholder="Describe what you saw or experienced..."></textarea>
      </div>

      <div class="form-group" style="display: flex; align-items: center; gap: 0.5rem;">
        <input type="checkbox" id="anonymous" v-model="form.isAnonymous" style="width: auto; padding: 0;" />
        <label for="anonymous" style="margin: 0;">Submit Anonymously</label>
      </div>

      <div style="margin-top: 2rem;">
        <button type="submit" class="btn btn-primary" :disabled="isSubmitting" style="width: 100%;">
          {{ isSubmitting ? 'Submitting...' : 'Submit Report' }}
        </button>
      </div>
      
      <div v-if="successMessage" style="margin-top: 1rem; color: var(--secondary); font-weight: 500; text-align: center;">
        {{ successMessage }}
      </div>
      <div v-if="errorMessage" style="margin-top: 1rem; color: #DC2626; font-weight: 500; text-align: center;">
        {{ errorMessage }}
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

// Use environment variable for API base URL
// In development: proxied through Vite (localhost:8080)
// In production: points directly to Render backend
const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

export default {
  name: 'ReportForm',
  props: ['studentId'],
  data() {
    return {
      form: {
        title: '',
        category: '',
        location: '',
        priorityLevel: 'MEDIUM',
        description: '',
        isAnonymous: true,
        studentID: ''
      },
      isSubmitting: false,
      successMessage: '',
      errorMessage: ''
    };
  },
  methods: {
    async submitReport() {
      this.isSubmitting = true;
      this.successMessage = '';
      this.errorMessage = '';
      // Attach studentId if not anonymous
      if (!this.form.isAnonymous) {
        this.form.studentID = this.studentId || '';
      }
      try {
        const response = await axios.post(`${API_BASE}/api/reports`, this.form);
        if (response.status === 201) {
          this.successMessage = 'Report submitted successfully. Thank you for keeping AUCA safe.';
          // Reset form
          this.form = {
            title: '', category: '', location: '', priorityLevel: 'MEDIUM', description: '', isAnonymous: true, studentID: ''
          };
        }
      } catch (error) {
        console.error('Error submitting report:', error);
        this.errorMessage = 'Failed to submit report. Please try again.';
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>
