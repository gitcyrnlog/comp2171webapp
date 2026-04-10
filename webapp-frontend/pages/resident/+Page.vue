<template>
  <section class="surface wrapper">
    <div class="tabbar">
      <button :class="{ active: tab === 'appointments' }" @click="tab = 'appointments'">Appointments</button>
      <button :class="{ active: tab === 'issues' }" @click="tab = 'issues'">Report Issues</button>
    </div>

    <article v-if="tab === 'appointments'" class="panel">
      <h2>Book Laundry Appointment</h2>
      <p class="muted">Sequence: check availability -> create booking -> notification.</p>
      <form class="stack" @submit.prevent="bookSlot">
        <label>Date <input v-model="bookingDate" type="date" required /></label>
        <div class="row">
          <label>Start <input v-model="startTime" type="time" required /></label>
          <label>End <input v-model="endTime" type="time" required /></label>
        </div>
        <label>Machine <input v-model="machineNo" placeholder="Machine-01" required /></label>
        <div class="row actions">
          <button type="button" @click="check">Check</button>
          <button class="primary" type="submit">Book</button>
        </div>
      </form>
      <p v-if="availability !== null" :class="availability ? 'ok' : 'bad'">
        {{ availability ? "Available slot" : "Slot is already taken" }}
      </p>
      <p v-if="laundryMessage" class="ok">{{ laundryMessage }}</p>
      <p v-if="laundryError" class="bad">{{ laundryError }}</p>
    </article>

    <article v-else class="panel">
      <h2>Report Facility Issue</h2>
      <p class="muted">Sequence: submit issue -> assign worker -> status update.</p>
      <form class="stack" @submit.prevent="submitIssue">
        <label>Location <input v-model="location" placeholder="Block J Laundry Room" required /></label>
        <label>Description <textarea v-model="description" required rows="5" /></label>
        <button class="primary" type="submit">Submit Issue</button>
      </form>
      <p v-if="issueMessage" class="ok">{{ issueMessage }}</p>
      <p v-if="issueError" class="bad">{{ issueError }}</p>
    </article>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { checkLaundryAvailability, createLaundryBooking, me, reportFacilityIssue } from "../lib/api";

const bookingDate = ref("");
const startTime = ref("07:00");
const endTime = ref("09:00");
const machineNo = ref("Machine-01");
const tab = ref<"appointments" | "issues">("appointments");
const availability = ref<boolean | null>(null);
const laundryMessage = ref("");
const laundryError = ref("");

const location = ref("");
const description = ref("");
const issueMessage = ref("");
const issueError = ref("");

onMounted(async () => {
  try {
    await me();
  } catch {
    window.location.href = "/login";
  }
});

async function check() {
  try {
    const result = await checkLaundryAvailability({
      bookingDate: bookingDate.value,
      startTime: startTime.value,
      endTime: endTime.value,
      machineNo: machineNo.value,
    });
    availability.value = result.available;
  } catch {
    availability.value = null;
  }
}

async function bookSlot() {
  laundryMessage.value = "";
  laundryError.value = "";
  try {
    const booking = await createLaundryBooking({
      bookingDate: bookingDate.value,
      startTime: startTime.value,
      endTime: endTime.value,
      machineNo: machineNo.value,
    });
    laundryMessage.value = `Booking confirmed. Reference #${booking.id ?? "N/A"}`;
  } catch (e: any) {
    laundryError.value = e.message || "Unable to create booking";
  }
}

async function submitIssue() {
  issueMessage.value = "";
  issueError.value = "";
  try {
    const issue = await reportFacilityIssue({ location: location.value, description: description.value });
    issueMessage.value = `Issue #${issue.id} submitted. Status: ${issue.status}`;
  } catch (e: any) {
    issueError.value = e.message || "Unable to submit issue";
  }
}
</script>

<style scoped>
.wrapper {
  max-width: 860px;
  margin: 0 auto;
  display: grid;
  gap: 1rem;
  padding: 1.4rem;
  border: 1px solid var(--line);
  border-radius: 16px;
  background: #fff;
}
.tabbar {
  display: inline-grid;
  grid-template-columns: 1fr 1fr;
  border: 1px solid var(--line);
  border-radius: 999px;
  overflow: hidden;
  width: 320px;
}
.tabbar button {
  border: 0;
  padding: 0.55rem 1rem;
  background: transparent;
  color: var(--ink-soft);
}
.tabbar button.active {
  background: #0f766e;
  color: #fff;
}
.panel {
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 1.2rem;
}
.muted {
  color: var(--ink-soft);
}
.stack {
  display: grid;
  gap: 0.8rem;
}
.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.7rem;
}
.actions {
  grid-template-columns: 1fr 1fr;
}
input,
textarea,
button {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 0.72rem;
}
.primary {
  background: linear-gradient(135deg, #0f766e, #0891b2);
  color: #fff;
  border: none;
}
.ok {
  color: #047857;
}
.bad {
  color: #b91c1c;
}
</style>
