FROM node:26.3.0-alpine3.22@sha256:c7932b9e5e337b0e733d6e16abc1b0e104759e8b05e59ed56586cce967d26dfe AS builder
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN npm install -g corepack
RUN corepack enable
WORKDIR /app
COPY . ./
RUN pnpm ci
RUN pnpm build

FROM cgr.dev/chainguard/nginx@sha256:f801aa4ac908f6c3847efe81b577e4f34e1f237b5556dfbe698127ad464db269
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf