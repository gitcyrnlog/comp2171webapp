<template>
  <section class="hero surface">
    <p class="eyebrow">DIGITIZED OPERATIONS PLATFORM</p>
    <h1>GAH Facilities Management</h1>
    <p class="lede">
      Web application preserving your architecture: Auth, Laundry, Facilities, SecurityOps, and Notifications.
    </p>
    <div class="hero-actions">
      <a href="/login">Start Session</a>
      <a href="/resident">Resident Workflows</a>
      <a href="/staff">Staff Workflows</a>
    </div>
  </section>

  <section class="module-grid">
    <article class="surface card">
      <h3>AuthService</h3>
      <p>JWT issue/validation with role claims and ownership-safe request handling.</p>
    </article>
    <article class="surface card">
      <h3>LaundryService</h3>
      <p>Availability checks, booking lifecycle, and resident-facing confirmations.</p>
    </article>
    <article class="surface card">
      <h3>FacilityService</h3>
      <p>Issue submission, assignment transition, and status tracking by issue ID.</p>
    </article>
    <article class="surface card">
      <h3>Notification Layer</h3>
      <p>Service hook for email/push integration while retaining modular boundaries.</p>
    </article>
  </section>
</template>

<style scoped>
.surface {
  border: 1px solid var(--line);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

.hero {
  padding: 2rem;
  margin-bottom: 1rem;
}

.eyebrow {
  letter-spacing: 0.08em;
  font-size: 0.78rem;
  color: #0f766e;
  font-weight: 700;
}

.hero h1 {
  margin: 0.2rem 0 0.6rem;
  font-size: clamp(1.8rem, 3vw, 3rem);
}

.lede {
  max-width: 70ch;
  color: var(--ink-soft);
}

.hero-actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.7rem;
  flex-wrap: wrap;
}

.hero-actions a {
  border: 1px solid var(--line);
  border-radius: 999px;
  padding: 0.55rem 0.9rem;
  background: #fff;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.card {
  padding: 1rem 1.1rem;
}

.card p {
  color: var(--ink-soft);
}

@media (max-width: 900px) {
  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
