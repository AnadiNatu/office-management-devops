#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

if [[ ! -f .env ]]; then
  echo "Copy .env.example to .env and configure DOMAIN first."
  exit 1
fi

set -a
source .env
set +a

: "${DOMAIN:?DOMAIN is required}"
: "${LETSENCRYPT_DIR:?LETSENCRYPT_DIR is required}"
: "${CERTBOT_WEBROOT:?CERTBOT_WEBROOT is required}"

mkdir -p "$CERTBOT_WEBROOT/.well-known/acme-challenge"
mkdir -p "$LETSENCRYPT_DIR"

echo "==> Starting the stack with HTTP enabled for ACME..."
docker compose --env-file .env -f docker-compose.prod.yml up -d \
  service-registry rabbitmq auth-service task-service comment-service \
  email-service notification-service message-service api-gateway frontend

echo "==> Waiting for port 80..."
sleep 10

echo "==> Requesting Let's Encrypt certificate for ${DOMAIN}..."

docker run --rm \
  -v "${LETSENCRYPT_DIR}:/etc/letsencrypt" \
  -v "${CERTBOT_WEBROOT}:/var/www/certbot" \
  certbot/certbot:latest certonly \
  --webroot \
  --webroot-path /var/www/certbot \
  --email "${CERTBOT_EMAIL:-admin@${DOMAIN}}" \
  --agree-tos \
  --no-eff-email \
  -d "${DOMAIN}"

echo "==> Restarting frontend so HTTPS configuration is loaded..."
docker compose --env-file .env -f docker-compose.prod.yml up -d --force-recreate frontend

echo "Certificate created and HTTPS frontend started."
echo "Open: https://${DOMAIN}"
