FROM node:24.16.0-alpine3.22@sha256:191c9f0080fcbbc6547a85dc0ff7988072214a355aabdc1d2ec55a7dae5eea8a AS build
ARG SENTRY_RELEASE
RUN apk update && apk upgrade
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm ci
COPY playwright.config.ts nuxt.config.ts nuxt.config.d.ts tsconfig.json \
  .oxlintrc.json sentry.server.config.ts sentry.client.config.ts ./
COPY config ./config
COPY src ./src
RUN --mount=type=secret,id=SENTRY_AUTH_TOKEN,required=false \
  if [ -f /run/secrets/SENTRY_AUTH_TOKEN ]; then \
  export SENTRY_AUTH_TOKEN="$(cat /run/secrets/SENTRY_AUTH_TOKEN)"; \
  fi && \
  pnpm run prepare && NODE_OPTIONS="--max-old-space-size=4096" pnpm build

FROM node:24.16.0-alpine3.22@sha256:191c9f0080fcbbc6547a85dc0ff7988072214a355aabdc1d2ec55a7dae5eea8a
ARG SENTRY_RELEASE
RUN apk update && apk upgrade && apk add dumb-init && apk cache clean
USER node
WORKDIR /app
COPY --chown=node:node --from=build /app/.output/server ./server
COPY --chown=node:node --from=build /app/.output/public ./public
COPY --chown=node:node --from=build /app/.output/nitro.json ./nitro.json
COPY start.sh ./
EXPOSE 3000
ENV HOST=0.0.0.0 \
  PORT=3000 \
  SENTRY_RELEASE=${SENTRY_RELEASE}
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["sh", "./start.sh"]
