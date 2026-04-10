<template>
  <div class="app-frame">
    <header class="topbar">
      <a class="brand" href="/">
        <span class="dot"></span>
        GAH Facilities Web
      </a>

      <nav class="nav-links">
        <a href="/resident">Resident Portal</a>
        <a href="/staff">Staff Console</a>
        <a href="/login">Auth</a>
      </nav>

      <div class="session-box">
        <span v-if="auth">{{ auth.fullName }} · {{ auth.role }}</span>
        <span v-else>Guest Session</span>
        <button v-if="auth" @click="logout">Log out</button>
      </div>
    </header>

    <main id="page-content" class="stage">
      <slot />
    </main>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { clearAuth, getStoredAuth } from "./lib/api";

const auth = computed(() => getStoredAuth());

function logout() {
  clearAuth();
  window.location.href = "/login";
}
</script>

<style>
:root {
  --bg-0: #f3f9f8;
  --bg-1: #e7f2f7;
  --ink: #0f172a;
  --ink-soft: #475569;
  --line: #cbd5e1;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  color: var(--ink);
  font-family: "Space Grotesk", "Segoe UI", sans-serif;
  background:
    radial-gradient(circle at 15% 10%, #ffffff 0%, transparent 46%),
    radial-gradient(circle at 85% 20%, #d6f5ee 0%, transparent 38%),
    linear-gradient(180deg, var(--bg-0), var(--bg-1));
}

a {
  color: inherit;
  text-decoration: none;
}

#page-content {
  opacity: 1;
  transition: opacity 0.25s ease;
}

body.page-transition #page-content {
  opacity: 0;
}
</style>

<style scoped>
.app-frame {
  min-height: 100vh;
}

.topbar {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 1rem;
  align-items: center;
  padding: 0.85rem 1.2rem;
  border-bottom: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.dot {
  width: 0.7rem;
  height: 0.7rem;
  border-radius: 50%;
  background: linear-gradient(135deg, #0284c7, #0f766e);
}

.nav-links {
  display: flex;
  gap: 0.9rem;
  justify-content: center;
}

.nav-links a {
  padding: 0.45rem 0.8rem;
  border: 1px solid transparent;
  border-radius: 999px;
}

.nav-links a:hover {
  border-color: var(--line);
  background: #fff;
}

.session-box {
  display: inline-flex;
  gap: 0.6rem;
  align-items: center;
  color: var(--ink-soft);
  font-size: 0.9rem;
}

.session-box button {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 10px;
  padding: 0.35rem 0.6rem;
}

.stage {
  max-width: 1180px;
  margin: 0 auto;
  padding: 1.2rem;
}

@media (max-width: 980px) {
  .topbar {
    grid-template-columns: 1fr;
  }

  .nav-links {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
