FROM node:26.3.0-alpine3.22@sha256:c7932b9e5e337b0e733d6e16abc1b0e104759e8b05e59ed56586cce967d26dfe AS build
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

FROM cgr.dev/chainguard/node@sha256:3c2afa83342798a7354cacf2641c006180df8eb57d639cfc068c403454f2e624
ARG SENTRY_RELEASE
USER node
WORKDIR /app
COPY --chown=node:node --from=build /app/.output/server ./server
COPY --chown=node:node --from=build /app/.output/public ./public
COPY --chown=node:node --from=build /app/.output/nitro.json ./nitro.json

EXPOSE 3000
ENV HOST=0.0.0.0 \
  PORT=3000 \
  SENTRY_RELEASE=${SENTRY_RELEASE}
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["node", "--import", "/app/server/sentry.server.config.mjs", "/app/server/index.mjs"]
