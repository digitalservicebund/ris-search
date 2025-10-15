#!/bin/sh
set -euf

# Exporting only if not already set, see https://stackoverflow.com/a/11686912
export NUXT_OAUTH_KEYCLOAK_CLIENT_ID="${NUXT_OAUTH_KEYCLOAK_CLIENT_ID:=$(cat /etc/secrets/keycloakclientid/clientid)}"
export NUXT_OAUTH_KEYCLOAK_CLIENT_SECRET="${NUXT_OAUTH_KEYCLOAK_CLIENT_SECRET:=$(cat /etc/secrets/keycloakclientsecret/clientsecret)}"
export NUXT_RIS_BACKEND_URL="${NUXT_RIS_BACKEND_URL:=$(cat /etc/secrets/ris-backend-url/url)}"
export NUXT_SSR_BACKEND_URL="${NUXT_SSR_BACKEND_URL=$(cat /etc/secrets/ssr-backend-url/url)}"
export NUXT_SESSION_PASSWORD="${NUXT_SESSION_PASSWORD:=$(cat /etc/secrets/session-password/secret)}"
export SENTRY_AUTH_TOKEN="${SENTRY_AUTH_TOKEN:=$(cat /etc/secrets/sentry-auth/token)}"
export NUXT_PUBLIC_SENTRY_DSN="${NUXT_PUBLIC_SENTRY_DSN:=$(cat /etc/secrets/sentry-dsn/url)}"

node --import /app/server/sentry.server.config.mjs /app/server/index.mjs
