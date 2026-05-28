FROM node:24.14.1-alpine3.22@sha256:4f33c7804fa7775b87875f512e25e515692210bfdeafd715eb8bc441c364e2f6 AS builder
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN corepack enable
WORKDIR /app
COPY . ./
RUN pnpm ci
RUN pnpm build

FROM nginxinc/nginx-unprivileged:1.31.1-alpine3.23@sha256:8122337ed6c475bb486bc9340da453d4599f225e6b920ff0d92ca2267486b9b5
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf
