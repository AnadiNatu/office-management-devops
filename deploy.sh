#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"
ENV_FILE="${ENV_FILE:-.env}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "ERROR: $ENV_FILE not found."
  echo "Copy .env.example to .env on the deployment host and fill in the values."
  exit 1
fi

set -a
source "$ENV_FILE"
set +a

: "${DOMAIN:?DOMAIN is required}"
: "${LETSENCRYPT_DIR:?LETSENCRYPT_DIR is required}"
: "${CERTBOT_WEBROOT:?CERTBOT_WEBROOT is required}"

mkdir -p "$CERTBOT_WEBROOT/.well-known/acme-challenge"
mkdir -p "$LETSENCRYPT_DIR"

echo "==> Checking Docker..."
docker info >/dev/null

echo "==> Validating Compose..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" config >/dev/null

echo "==> Building services..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" build --pull

echo "==> Starting infrastructure and backend..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d \
  service-registry rabbitmq auth-service task-service comment-service \
  email-service notification-service message-service api-gateway

echo "==> Waiting briefly for the gateway..."
sleep 15

echo "==> Starting frontend..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d frontend

echo "==> Current containers:"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps

echo
echo "Deployment started."
echo "URL: https://${DOMAIN}"
