#!/usr/bin/env bash
set -euo pipefail

FRONTEND_PORT="${FRONTEND_PORT:-5173}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$REPO_ROOT/webapp-backend"
FRONTEND_DIR="$REPO_ROOT/webapp-frontend"

if [ ! -f "$BACKEND_DIR/mvnw" ]; then
  echo "Maven Wrapper not found at webapp-backend/mvnw"
  exit 1
fi

if ! command -v node >/dev/null 2>&1; then
  echo "Node.js 20+ is required"
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java 17+ is required"
  exit 1
fi

if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
  echo "Installing frontend dependencies..."
  (cd "$FRONTEND_DIR" && npm install)
fi

echo "Starting backend in background..."
(
  cd "$BACKEND_DIR"
  ./mvnw spring-boot:run
) &

BACKEND_PID=$!
trap 'kill $BACKEND_PID 2>/dev/null || true' EXIT

echo "Starting frontend on port $FRONTEND_PORT..."
cd "$FRONTEND_DIR"
npm run dev -- --host 0.0.0.0 --port "$FRONTEND_PORT"
