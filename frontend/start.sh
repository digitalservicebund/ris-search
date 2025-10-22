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

if [ -f "/app/server/sentry.server.config.mjs" ] && [ -n "${NUXT_PUBLIC_SENTRY_DSN:-}" ]; then
  exec node --import /app/server/sentry.server.config.mjs /app/server/index.mjs
else
  exec node /app/server/index.mjs
fi
