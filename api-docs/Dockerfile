FROM node:24.14.1-alpine3.22@sha256:4f33c7804fa7775b87875f512e25e515692210bfdeafd715eb8bc441c364e2f6 AS builder
RUN apk update && apk upgrade
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN npm install -g pnpm@10.28.2
WORKDIR /app
COPY package.json pnpm-lock.yaml env.d.ts tailwind.config.js ./
RUN pnpm install --frozen-lockfile
COPY .vitepress ./.vitepress
COPY docs ./docs
RUN pnpm build

FROM nginxinc/nginx-unprivileged:1.29.8-alpine3.23@sha256:f35982400455b4f359083ba9d1ef573fbbe4221a6a8a194dc7a14beb4e2cd3dc
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf