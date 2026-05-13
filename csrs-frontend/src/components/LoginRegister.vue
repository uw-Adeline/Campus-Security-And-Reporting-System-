<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #e0e7ff 0%, #dcfce7 100%);">
    <div class="card animate-fade-in" style="max-width: 480px; width: 100%; margin: 2rem;">

      <!-- Logo -->
      <div style="text-align: center; margin-bottom: 2rem;">
        <div style="display: inline-flex; align-items: center; justify-content: center; width: 64px; height: 64px; background: var(--primary); border-radius: 16px; margin-bottom: 1rem;">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24"
            fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <h2 style="margin-bottom: 0.25rem;">AUCA CSRS</h2>
        <p style="color: var(--gray); font-size: 0.9rem;">Campus Security & Reporting System</p>
      </div>

      <!-- Toggle Login / Register -->
      <div style="display: flex; background: var(--light); border-radius: 8px; padding: 4px; margin-bottom: 2rem;">
        <button
          @click="mode = 'login'"
          :style="{ flex: 1, padding: '0.5rem', borderRadius: '6px', border: 'none', cursor: 'pointer', fontWeight: '500', background: mode === 'login' ? 'white' : 'transparent', boxShadow: mode === 'login' ? '0 1px 3px rgba(0,0,0,0.1)' : 'none' }">
          Login
        </button>
        <button
          @click="mode = 'register'"
          :style="{ flex: 1, padding: '0.5rem', borderRadius: '6px', border: 'none', cursor: 'pointer', fontWeight: '500', background: mode === 'register' ? 'white' : 'transparent', boxShadow: mode === 'register' ? '0 1px 3px rgba(0,0,0,0.1)' : 'none' }">
          Register
        </button>
      </div>

      <!-- LOGIN FORM -->
      <form v-if="mode === 'login'" @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Email Address</label>
          <input type="email" v-model="loginForm.email" required placeholder="your.email@auca.ac.rw" />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input type="password" v-model="loginForm.password" required placeholder="Enter your password" />
        </div>
        <div class="form-group">
          <label>Login As</label>
          <select v-model="loginForm.role" required>
            <option value="" disabled>Select your role</option>
            <option value="student">Student</option>
            <option value="lecturer">Lecturer</option>
            <option value="admin">Admin</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
          Login
        </button>
        <div v-if="errorMsg" style="margin-top: 1rem; color: #DC2626; text-align: center; font-size: 0.9rem;">
          {{ errorMsg }}
        </div>
      </form>

      <!-- REGISTER FORM -->
      <form v-if="mode === 'register'" @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Full Name</label>
          <input type="text" v-model="registerForm.name" required placeholder="e.g., Jean Pierre Habimana" />
        </div>
        <div class="form-group">
          <label>Email Address</label>
          <input type="email" v-model="registerForm.email" required placeholder="your.email@auca.ac.rw" />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input type="password" v-model="registerForm.password" required placeholder="Create a password" />
        </div>
        <div class="form-group">
          <label>Confirm Password</label>
          <input type="password" v-model="registerForm.confirmPassword" required placeholder="Repeat your password" />
          <p v-if="passwordMismatch" style="color: #DC2626; font-size: 0.8rem; margin-top: 0.4rem;">
            ❌ Passwords do not match
          </p>
          <p v-if="registerForm.confirmPassword && !passwordMismatch" style="color: #059669; font-size: 0.8rem; margin-top: 0.4rem;">
            ✅ Passwords match
          </p>
        </div>
        <div class="form-group">
          <label>Register As</label>
          <select v-model="registerForm.role" required>
            <option value="" disabled>Select your role</option>
            <option value="student">Student</option>
            <option value="lecturer">Lecturer</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
          Create Account
        </button>
        <div v-if="successMsg" style="margin-top: 1rem; color: #059669; text-align: center; font-size: 0.9rem;">
          {{ successMsg }}
        </div>
      </form>

      <!-- Demo accounts hint - collapsible -->
      <div style="margin-top: 1.5rem;">
        <button
          @click="showDemo = !showDemo"
          style="width: 100%; padding: 0.6rem; background: #F0F9FF; border: 1px dashed #BAE6FD; border-radius: 8px; cursor: pointer; font-size: 0.85rem; color: #0369A1; font-weight: 500;">
          {{ showDemo ? '🔼 Hide Demo Accounts' : '🔽 Show Demo Accounts' }}
        </button>
        <div v-if="showDemo" style="margin-top: 0.5rem; padding: 1rem; background: #F0F9FF; border-radius: 8px; font-size: 0.8rem; color: var(--gray); border: 1px solid #BAE6FD;">
          <strong style="color: var(--dark);">Demo Accounts:</strong><br/><br/>
          🎓 <strong>Student:</strong> student@auca.ac.rw / password123<br/>
          📚 <strong>Lecturer:</strong> lecturer@auca.ac.rw / password123<br/>
          🔐 <strong>Admin:</strong> admin@auca.ac.rw / password123
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginRegister',
  emits: ['login'],
  data() {
    return {
      mode: 'login',
      showDemo: false,
      loginForm: { email: '', password: '', role: '' },
      registerForm: { name: '', email: '', password: '', confirmPassword: '', role: '' },
      errorMsg: '',
      successMsg: '',
      accounts: [
        { id: 'student-001', name: 'Alice Uwimana', email: 'student@auca.ac.rw', password: 'password123', role: 'student' },
        { id: 'lecturer-001', name: 'Dr. Jean Bosco', email: 'lecturer@auca.ac.rw', password: 'password123', role: 'lecturer' },
        { id: 'admin-001', name: 'Admin', email: 'admin@auca.ac.rw', password: 'password123', role: 'admin' }
      ]
    }
  },
  computed: {
    passwordMismatch() {
      return this.registerForm.confirmPassword &&
             this.registerForm.password !== this.registerForm.confirmPassword
    }
  },
  methods: {
    handleLogin() {
      this.errorMsg = ''
      const user = this.accounts.find(
        a => a.email === this.loginForm.email &&
             a.password === this.loginForm.password &&
             a.role === this.loginForm.role
      )
      if (user) {
        this.$emit('login', user)
      } else {
        this.errorMsg = 'Invalid email, password, or role. Please try again.'
      }
    },
    handleRegister() {
      this.successMsg = ''
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        return // blocked — UI already shows the mismatch error
      }
      const newUser = {
        id: 'user-' + Date.now(),
        name: this.registerForm.name,
        email: this.registerForm.email,
        password: this.registerForm.password,
        role: this.registerForm.role
      }
      this.accounts.push(newUser)
      this.successMsg = 'Account created! You can now login.'
      this.registerForm = { name: '', email: '', password: '', confirmPassword: '', role: '' }
      setTimeout(() => { this.mode = 'login' }, 1500)
    }
  }
}
</script>
