# GAH Facilities Web Frontend

Vue + Vike frontend for the facilities system.

## Prerequisites
- Node.js 20+
- Backend running on port 8080

## Local run
1. Install dependencies:
  - `npm install`
2. Start dev server:
  - `npm run dev -- --host 0.0.0.0 --port 5173`
3. Open in browser:
  - `http://localhost:5173`

## Run from another device (same Wi-Fi/LAN)
1. Start backend on your main machine (port 8080).
2. Start frontend on your main machine with host binding:
  - `npm run dev -- --host 0.0.0.0 --port 5173`
3. Find your main machine IP (for example `192.168.1.25`).
4. On the second device, open:
  - `http://<YOUR_MAIN_MACHINE_IP>:5173`

By default, the frontend sends API requests to `http://<current-hostname>:8080`, so when you open the frontend via LAN IP it will call the backend on the same machine automatically.

## Optional override
If backend is running on a different host, set a custom API URL before starting frontend:
- PowerShell: `$env:VITE_API_BASE_URL="http://192.168.1.50:8080"`
- Then run: `npm run dev -- --host 0.0.0.0 --port 5173`

