# GAH Facilities Web App

Run the full web app from a fresh clone with minimal setup.

## Prerequisites
- Java 17+
- Node.js 20+

## Quick start (Windows)
1. Clone the repository.
2. Open PowerShell at the repo root.
3. Run:
   - `./run-dev.ps1`

This starts:
- Backend on `http://localhost:8080`
- Frontend on `http://localhost:5173`

## Quick start (macOS/Linux)
1. Clone the repository.
2. Open terminal at the repo root.
3. Make script executable once:
   - `chmod +x ./run-dev.sh`
4. Run:
   - `./run-dev.sh`

## Access from another device on same network
1. Start app with one of the scripts above on host machine.
2. Find host machine LAN IP (for example `192.168.1.25`).
3. Open from another device:
   - `http://<HOST_IP>:5173`

## Notes
- Backend uses Maven Wrapper (`webapp-backend/mvnw`), so Maven does not need to be installed separately.
- Frontend dependencies are installed automatically by scripts if `node_modules` is missing.
- If Windows asks for firewall access, allow private network access for ports `5173` and `8080`.
