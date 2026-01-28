FROM node:24.13.0-alpine3.22 AS build
ARG NUXT_PUBLIC_SENTRY_DSN
ARG SENTRY_RELEASE
RUN apk update && apk upgrade
RUN npm install -g pnpm@10.28.1
WORKDIR /app
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile
COPY playwright.config.ts nuxt.config.ts nuxt.config.d.ts tsconfig.json \
  eslint.config.mjs sentry.server.config.ts sentry.client.config.ts ./
COPY config ./config
COPY src ./src
RUN --mount=type=secret,id=SENTRY_AUTH_TOKEN,env=SENTRY_AUTH_TOKEN \
  pnpm run prepare && NODE_OPTIONS="--max-old-space-size=4096" pnpm build

FROM node:24.13.0-alpine3.22
ARG NUXT_PUBLIC_SENTRY_DSN
ARG SENTRY_RELEASE
RUN apk update && apk upgrade && apk add dumb-init && apk cache clean && \
  rm -r /usr/local/lib/node_modules/npm/node_modules/cross-spawn
USER node
WORKDIR /app
COPY --chown=node:node --from=build /app/.output/server ./server
COPY --chown=node:node --from=build /app/.output/public ./public
COPY --chown=node:node --from=build /app/.output/nitro.json ./nitro.json

COPY start.sh ./
EXPOSE 3000
ENV HOST=0.0.0.0 \
  PORT=3000 \
  NUXT_PUBLIC_SENTRY_DSN=${NUXT_PUBLIC_SENTRY_DSN} \
  SENTRY_RELEASE=${SENTRY_RELEASE}
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["sh", "./start.sh"]
