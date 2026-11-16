FROM node:26.13.0-alpine3.24@sha256:8996bb5821d62a8fd9bb27c1cb07462f3fdce6e47d640c6a881ead0b064ce216 AS build
ARG SENTRY_RELEASE
ARG SENTRY_UPLOAD_SOURCEMAPS=false
RUN npm install -g corepack
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
  SENTRY_UPLOAD_SOURCEMAPS=${SENTRY_UPLOAD_SOURCEMAPS} \
  pnpm run prepare && NODE_OPTIONS="--max-old-space-size=4096" pnpm build

FROM cgr.dev/chainguard/node@sha256:562b1ed174d7b89f053c3dc7dea8c03f03466aa2769e83ccac62aa3f727f23df
ARG SENTRY_RELEASE
USER node
WORKDIR /app
COPY --chown=node:node --from=build /app/.output/server ./server
COPY --chown=node:node --from=build /app/.output/public ./public
COPY --chown=node:node --from=build /app/.output/nitro.json ./nitro.json
COPY --chown=node:node start.sh ./start.sh

EXPOSE 3000
ENV HOST=0.0.0.0 \
  PORT=3000 \
  SENTRY_RELEASE=${SENTRY_RELEASE}
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["sh", "start.sh"]
