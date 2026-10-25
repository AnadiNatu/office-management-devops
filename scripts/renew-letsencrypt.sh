#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

set -a
source .env
set +a

docker run --rm \
  -v "${LETSENCRYPT_DIR}:/etc/letsencrypt" \
  -v "${CERTBOT_WEBROOT}:/var/www/certbot" \
  certbot/certbot:latest renew

docker compose --env-file .env -f docker-compose.prod.yml exec -T frontend nginx -s reload
