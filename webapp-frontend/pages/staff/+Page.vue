<template>
  <section class="grid-2">
    <article class="surface">
      <h2>Session Profile</h2>
      <p class="muted">Verifies secured /users/me endpoint and role claims.</p>
      <dl v-if="profile" class="profile">
        <dt>Name</dt><dd>{{ profile.fullName }}</dd>
        <dt>Email</dt><dd>{{ profile.email }}</dd>
        <dt>Role</dt><dd>{{ profile.role }}</dd>
      </dl>
      <p v-if="profileError" class="bad">{{ profileError }}</p>
    </article>

    <article class="surface">
      <h2>Issue Lookup</h2>
      <p class="muted">Track facility issue status by ID.</p>
      <form class="stack" @submit.prevent="lookup">
        <input v-model="issueId" placeholder="Issue ID" required />
        <button class="primary" type="submit">Fetch Issue</button>
      </form>

      <div v-if="issue" class="card">
        <strong>#{{ issue.id }}</strong>
        <p>Status: {{ issue.status }}</p>
        <p>Location: {{ issue.location }}</p>
        <p>Assigned Worker: {{ issue.assignedWorkerId ?? "Unassigned" }}</p>
      </div>
      <p v-if="lookupError" class="bad">{{ lookupError }}</p>
    </article>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getFacilityIssue, me, type CurrentUser } from "../lib/api";

const profile = ref<CurrentUser | null>(null);
const profileError = ref("");
const issueId = ref("");
const issue = ref<any>(null);
const lookupError = ref("");

onMounted(async () => {
  try {
    profile.value = await me();
  } catch (e: any) {
    profileError.value = e.message || "Unable to verify session";
  }
});

async function lookup() {
  lookupError.value = "";
  issue.value = null;
  try {
    issue.value = await getFacilityIssue(issueId.value);
  } catch (e: any) {
    lookupError.value = e.message || "Issue lookup failed";
  }
}
</script>

<style scoped>
.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}
@media (max-width: 960px) {
  .grid-2 {
    grid-template-columns: 1fr;
  }
}
.surface {
  padding: 1.4rem;
  border: 1px solid var(--line);
  border-radius: 16px;
  background: #fff;
}
.muted {
  color: var(--ink-soft);
}
.stack {
  display: grid;
  gap: 0.8rem;
}
.profile {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.5rem 1rem;
}
.card {
  margin-top: 1rem;
  padding: 0.9rem;
  border-radius: 12px;
  background: #f5f9fb;
}
input,
button {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 0.72rem;
}
.primary {
  background: linear-gradient(135deg, #334155, #0f766e);
  color: #fff;
  border: none;
}
.bad {
  color: #b91c1c;
}
</style>
