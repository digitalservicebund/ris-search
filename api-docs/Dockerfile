FROM node:26.13.0-alpine3.24@sha256:c6ad9e9c1d80f6eb607c26b4b0a132ff7a2344fad1c78ea3baa2a7d4c8de6d94 AS builder
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN npm install -g corepack
RUN corepack enable
WORKDIR /app
COPY . ./
RUN pnpm ci
RUN pnpm build

FROM cgr.dev/chainguard/nginx@sha256:16d0e55ffe62f55744d9b903d8eb57e533d0f660332a9fbb967d78b4e77c8bdb
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf