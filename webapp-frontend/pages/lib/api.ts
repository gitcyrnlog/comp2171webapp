export type UserRole =
  | "RESIDENT"
  | "BLOCK_REPRESENTATIVE"
  | "ADMIN"
  | "SECURITY_GUARD"
  | "MAINTENANCE_WORKER";

export interface AuthResponse {
  userId: number;
  fullName: string;
  email: string;
  role: UserRole;
  accessToken: string;
}

export interface CurrentUser {
  userId: number;
  fullName: string;
  email: string;
  role: UserRole;
  blockCode: string | null;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const AUTH_KEY = "gah.auth";

interface StoredAuth {
  token: string;
  role: UserRole;
  fullName: string;
  email: string;
  userId: number;
}

function buildHeaders(authenticated = true): HeadersInit {
  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (authenticated) {
    const session = getStoredAuth();
    if (session?.token) {
      headers.Authorization = `Bearer ${session.token}`;
    }
  }

  return headers;
}

async function parseJson<T>(response: Response): Promise<T> {
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    const msg = payload?.message || `Request failed with status ${response.status}`;
    throw new Error(msg);
  }
  return payload as T;
}

export function getStoredAuth(): StoredAuth | null {
  if (typeof window === "undefined") return null;
  const raw = window.localStorage.getItem(AUTH_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as StoredAuth;
  } catch {
    return null;
  }
}

export function clearAuth() {
  if (typeof window === "undefined") return;
  window.localStorage.removeItem(AUTH_KEY);
}

export function saveAuth(auth: AuthResponse) {
  if (typeof window === "undefined") return;
  const value: StoredAuth = {
    token: auth.accessToken,
    role: auth.role,
    fullName: auth.fullName,
    email: auth.email,
    userId: auth.userId,
  };
  window.localStorage.setItem(AUTH_KEY, JSON.stringify(value));
}

export async function login(email: string, password: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
    method: "POST",
    headers: buildHeaders(false),
    body: JSON.stringify({ email, password }),
  });
  return parseJson<AuthResponse>(response);
}

export async function register(payload: {
  fullName: string;
  email: string;
  password: string;
  role: "RESIDENT" | "BLOCK_REPRESENTATIVE";
  blockCode?: string;
}) {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/register`, {
    method: "POST",
    headers: buildHeaders(false),
    body: JSON.stringify(payload),
  });
  return parseJson<AuthResponse>(response);
}

export async function me() {
  const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
    method: "GET",
    headers: buildHeaders(true),
  });
  return parseJson<CurrentUser>(response);
}

export async function checkLaundryAvailability(payload: {
  bookingDate: string;
  startTime: string;
  endTime: string;
  machineNo: string;
}) {
  const params = new URLSearchParams(payload);
  const response = await fetch(`${API_BASE_URL}/api/v1/laundry/availability?${params.toString()}`, {
    method: "GET",
    headers: buildHeaders(true),
  });
  return parseJson<{ available: boolean }>(response);
}

export async function createLaundryBooking(payload: {
  bookingDate: string;
  startTime: string;
  endTime: string;
  machineNo: string;
}) {
  const response = await fetch(`${API_BASE_URL}/api/v1/laundry/bookings`, {
    method: "POST",
    headers: buildHeaders(true),
    body: JSON.stringify(payload),
  });
  return parseJson(response);
}

export async function reportFacilityIssue(payload: { location: string; description: string }) {
  const response = await fetch(`${API_BASE_URL}/api/v1/facilities/issues`, {
    method: "POST",
    headers: buildHeaders(true),
    body: JSON.stringify(payload),
  });
  return parseJson(response);
}

export async function getFacilityIssue(issueId: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/facilities/issues/${issueId}`, {
    method: "GET",
    headers: buildHeaders(true),
  });
  return parseJson(response);
}
