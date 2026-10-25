#!/usr/bin/env bash
set -Eeuo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"
ENV_FILE="${ENV_FILE:-.env}"
[[ -f "$COMPOSE_FILE" ]] || { echo "ERROR: $COMPOSE_FILE not found"; exit 1; }
[[ -f "$ENV_FILE" ]] || { echo "ERROR: $ENV_FILE not found"; exit 1; }
compose() { docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" "$@"; }
usage() {
  cat <<'HELP'
Usage: ./scripts/manage.sh <command>
  up            Start all services
  down          Stop/remove containers and networks (keeps named volumes)
  restart       Restart all services
  rebuild       Build images and recreate services
  ps            Show container status
  logs [name]   Follow logs for all services or one service
  pull          Pull configured images
  health        Show container status/health
  config        Validate and print resolved Compose configuration
  help          Show this help
Optional: COMPOSE_FILE=docker-compose.prod.yml ENV_FILE=.env
HELP
}
case "${1:-help}" in
  up) compose up -d; compose ps ;;
  down) compose down ;;
  restart) compose restart; compose ps ;;
  rebuild) compose build; compose up -d --force-recreate; compose ps ;;
  ps|health) compose ps ;;
  logs) shift || true; if [[ $# -gt 0 ]]; then compose logs -f --tail=200 "$@"; else compose logs -f --tail=200; fi ;;
  pull) compose pull ;;
  config) compose config ;;
  help|-h|--help) usage ;;
  *) echo "Unknown command: $1"; usage; exit 2 ;;
esac
