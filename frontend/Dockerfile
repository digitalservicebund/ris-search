FROM node:22.17.0-alpine3.22 AS build

RUN apk update && apk upgrade

WORKDIR /app
COPY .yarn/releases/yarn-4.9.2.cjs .yarn/releases/yarn-4.9.2.cjs
COPY package.json yarn.lock .yarnrc.yml ./
RUN yarn install --immutable
COPY playwright.config.ts nuxt.config.ts nuxt.config.d.ts libraries.d.ts tsconfig.json \
    eslint.config.mjs sentry.server.config.ts ./
COPY src ./src
RUN yarn run prepare && yarn build


FROM node:22.17.0-alpine3.22
RUN apk update && apk upgrade && apk add dumb-init && apk cache clean && \
    rm -r /usr/local/lib/node_modules/npm/node_modules/cross-spawn
USER node
WORKDIR /app
COPY --chown=node:node --from=build /app/.output/server ./server
COPY --chown=node:node --from=build /app/.output/public ./public
COPY --chown=node:node --from=build /app/.output/nitro.json ./nitro.json
COPY start.sh ./
EXPOSE 3000
ENV HOST=0.0.0.0 PORT=3000 NODE_ENV=production
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["sh", "./start.sh"]
