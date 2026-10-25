#!/usr/bin/env bash
set -Eeuo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"
ENV_FILE="${ENV_FILE:-.env}"

[[ -f "$COMPOSE_FILE" ]] || { echo "ERROR: $COMPOSE_FILE not found"; exit 1; }
[[ -f "$ENV_FILE" ]] || { echo "ERROR: $ENV_FILE not found; configure it first"; exit 1; }

echo "==> Checking Docker"
docker info >/dev/null
echo "==> Validating Compose"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" config >/dev/null
echo "==> Building images"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" build
echo "==> Starting all services"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps
