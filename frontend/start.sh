#!/bin/sh
set -euo

read_secret() {
  file="$1"
  [ -f "$file" ] && (cat "$file" || true)
}

# Exporting only if not already set, see https://stackoverflow.com/a/11686912
export NUXT_BASIC_AUTH="${NUXT_BASIC_AUTH:-$(read_secret /etc/secrets/basic-auth/secret)}"

exec node --import /app/server/sentry.server.config.mjs /app/server/index.mjs