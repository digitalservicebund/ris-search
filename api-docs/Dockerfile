FROM node:24.14.1-alpine3.22@sha256:4f33c7804fa7775b87875f512e25e515692210bfdeafd715eb8bc441c364e2f6 AS builder
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN corepack enable
WORKDIR /app
COPY . ./
RUN pnpm ci
RUN pnpm build

FROM cgr.dev/chainguard/nginx@sha256:bfc999927013bd5d015c496342bd8fc1b43a94e740b61af1fa55734f66e94947
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf
