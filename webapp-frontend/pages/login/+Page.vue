<template>
  <section class="surface auth-shell">
    <div class="mode-switch">
      <button :class="{ active: mode === 'login' }" @click="mode = 'login'">Login</button>
      <button :class="{ active: mode === 'register' }" @click="mode = 'register'">Register</button>
    </div>

    <h1>{{ mode === "login" ? "Welcome Back" : "Create Resident Access" }}</h1>
    <p class="subtitle">Diagram-aligned AuthService flow with secure token session.</p>

    <form @submit.prevent="submit" class="stack">
      <label v-if="mode === 'register'">
        Full name
        <input v-model="fullName" required placeholder="e.g. Harry Son" />
      </label>

      <label>
        Email
        <input v-model="email" type="email" required placeholder="your@email.com" />
      </label>

      <label>
        Password
        <input v-model="password" type="password" required minlength="8" />
      </label>

      <template v-if="mode === 'register'">
        <label>
          Role
          <select v-model="role">
            <option value="RESIDENT">Resident</option>
            <option value="BLOCK_REPRESENTATIVE">Block Representative</option>
          </select>
        </label>

        <label>
          Block / Room code
          <input v-model="blockCode" placeholder="J1204" />
        </label>
      </template>

      <button class="primary" :disabled="loading">{{ loading ? "Processing..." : mode === "login" ? "Sign In" : "Create Account" }}</button>
    </form>

    <p v-if="error" class="feedback error">{{ error }}</p>
    <p v-if="success" class="feedback success">{{ success }}</p>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { login, register, saveAuth } from "../lib/api";

const mode = ref<"login" | "register">("login");
const fullName = ref("");
const email = ref("");
const password = ref("");
const role = ref<"RESIDENT" | "BLOCK_REPRESENTATIVE">("RESIDENT");
const blockCode = ref("");
const loading = ref(false);
const error = ref("");
const success = ref("");

async function submit() {
  loading.value = true;
  error.value = "";
  success.value = "";

  try {
    if (mode.value === "login") {
      const auth = await login(email.value, password.value);
      saveAuth(auth);
      success.value = `Logged in as ${auth.fullName} (${auth.role}). Redirecting...`;
    } else {
      const auth = await register({
        fullName: fullName.value,
        email: email.value,
        password: password.value,
        role: role.value,
        blockCode: blockCode.value || undefined,
      });
      saveAuth(auth);
      success.value = "Account created and signed in.";
    }

    setTimeout(() => {
      window.location.href = "/";
    }, 800);
  } catch (e: any) {
    error.value = e.message || "Authentication failed";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-shell {
  max-width: 560px;
  margin: 2rem auto;
  padding: 2rem;
}
.mode-switch {
  display: inline-grid;
  grid-template-columns: 1fr 1fr;
  border: 1px solid var(--line);
  border-radius: 999px;
  overflow: hidden;
  margin-bottom: 1rem;
}
.mode-switch button {
  border: 0;
  background: transparent;
  color: var(--ink-soft);
  padding: 0.5rem 1rem;
}
.mode-switch button.active {
  background: var(--ink);
  color: #fff;
}
.subtitle {
  color: var(--ink-soft);
  margin-top: 0;
}
.stack {
  display: grid;
  gap: 0.85rem;
}
label {
  display: grid;
  gap: 0.3rem;
  font-size: 0.95rem;
}
input,
select {
  width: 100%;
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 0.72rem 0.8rem;
  background: #fff;
}
.primary {
  border: 0;
  border-radius: 12px;
  padding: 0.8rem 1rem;
  background: linear-gradient(135deg, #0f766e, #0369a1);
  color: #fff;
  font-weight: 600;
}
.feedback {
  margin-top: 1rem;
}
.error {
  color: #b91c1c;
}
.success {
  color: #047857;
}
</style>
