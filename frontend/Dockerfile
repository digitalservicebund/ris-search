FROM node:26.12.1-alpine3.24@sha256:40cd011b55415bc894e45b02fdd5a5dfde056921b7d23a64c74f80497af48797 AS build
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

FROM cgr.dev/chainguard/node@sha256:939413e9b81609d6d7188037f76faf627566de86fa786f9a25baf6764a51b618
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
