#!/bin/sh
set -euo
set -x

read_secret() {
  file="$1"
  [ -f "$file" ] && (cat "$file" || true)
}

# Exporting only if not already set, see https://stackoverflow.com/a/11686912
export NUXT_OAUTH_KEYCLOAK_CLIENT_ID="${NUXT_OAUTH_KEYCLOAK_CLIENT_ID:-$(read_secret /etc/secrets/keycloakclientid/clientid)}"
export NUXT_OAUTH_KEYCLOAK_CLIENT_SECRET="${NUXT_OAUTH_KEYCLOAK_CLIENT_SECRET:-$(read_secret /etc/secrets/keycloakclientsecret/clientsecret)}"
export NUXT_RIS_BACKEND_URL="${NUXT_RIS_BACKEND_URL:-$(read_secret /etc/secrets/ris-backend-url/url)}"
export NUXT_SSR_BACKEND_URL="${NUXT_SSR_BACKEND_URL:-$(read_secret /etc/secrets/ssr-backend-url/url)}"
export NUXT_SESSION_PASSWORD="${NUXT_SESSION_PASSWORD:-$(read_secret /etc/secrets/session-password/secret)}"
export SENTRY_AUTH_TOKEN="${SENTRY_AUTH_TOKEN:-$(read_secret /etc/secrets/sentry-auth/token)}"
export NUXT_PUBLIC_SENTRY_DSN="${NUXT_PUBLIC_SENTRY_DSN:-$(read_secret /etc/secrets/sentry-dsn/url)}"

SENTRY_IMPORT="/app/server/sentry.server.config.mjs"

NODE_ARGS=""
if [ -f "$SENTRY_IMPORT" ] && [ -n "${NUXT_PUBLIC_SENTRY_DSN:-}" ]; then
  NODE_ARGS="--import $SENTRY_IMPORT"
else
  echo "Skipping Sentry import (file missing or DSN empty)"
fi

node "$NODE_ARGS" /app/server/index.mjs
