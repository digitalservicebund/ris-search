FROM node:24.14.1-alpine3.22@sha256:4f33c7804fa7775b87875f512e25e515692210bfdeafd715eb8bc441c364e2f6 AS builder
RUN apk update && apk upgrade
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml env.d.ts tailwind.config.js ./
RUN pnpm ci
COPY .vitepress ./.vitepress
COPY docs ./docs
RUN pnpm build

FROM nginxinc/nginx-unprivileged:1.31.0-alpine3.23@sha256:3707417e3304492667a63c90ac0103465330437f9cdfaa38f8cd19f9975cbeed
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf
