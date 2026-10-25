#!/usr/bin/env bash
set -Eeuo pipefail
# Run locally; EC2 must already have a clone of this repository and a configured .env.
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"
: "${EC2_HOST:?Set EC2_HOST to the EC2 public DNS name or IP}"
EC2_USER="${EC2_USER:-ec2-user}"
REMOTE_DIR="${REMOTE_DIR:-~/office-task-devops}"
GIT_BRANCH="${GIT_BRANCH:-main}"
SSH_ARGS=()
if [[ -n "${EC2_KEY:-}" ]]; then
  [[ -f "$EC2_KEY" ]] || { echo "ERROR: EC2_KEY not found: $EC2_KEY"; exit 1; }
  SSH_ARGS+=(-i "$EC2_KEY")
fi
echo "==> Checking SSH"
ssh "${SSH_ARGS[@]}" "${EC2_USER}@${EC2_HOST}" "echo 'SSH connection OK'"
echo "==> Updating repository and deploying on EC2"
ssh "${SSH_ARGS[@]}" "${EC2_USER}@${EC2_HOST}" \
  "cd ${REMOTE_DIR} && git fetch origin && git checkout ${GIT_BRANCH} && git pull --ff-only origin ${GIT_BRANCH} && chmod +x deploy.sh scripts/*.sh && ./deploy.sh"
echo "Remote deploy command completed. Check container status and logs on EC2."
