#!/usr/bin/env bash
# Creates an ADMIN user directly in the Sonora PostgreSQL database.
# Usage: ./scripts/register_admin.sh [email] [password]
# Defaults: admin@sonora.com / Admin1234!
#
# The password is BCrypt-encoded at runtime via the backend DataInitializer,
# so this script only needs to set the env vars and (re)start the backend.
# Run this if you want to override the defaults without rebuilding the image.

set -euo pipefail

ADMIN_EMAIL="${1:-admin@sonora.com}"
ADMIN_PASSWORD="${2:-Admin1234!}"

echo "Setting admin credentials: $ADMIN_EMAIL"

# Export so docker-compose picks them up
export ADMIN_EMAIL
export ADMIN_PASSWORD

# If running inside the docker-compose stack, restart the backend so the
# DataInitializer runs with the new credentials.
if command -v docker &>/dev/null && docker compose version &>/dev/null 2>&1; then
  COMPOSE_FILE="$(dirname "$0")/../docker-compose.yml"
  docker compose -f "$COMPOSE_FILE" up -d --no-deps sonora-backend
  echo "Backend restarted — admin user will be created on startup."
else
  echo ""
  echo "Docker Compose not available. Start the backend manually with:"
  echo "  ADMIN_EMAIL=$ADMIN_EMAIL ADMIN_PASSWORD=$ADMIN_PASSWORD ./mvnw spring-boot:run"
fi
