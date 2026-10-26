FROM node:26.12.1-alpine3.24@sha256:40cd011b55415bc894e45b02fdd5a5dfde056921b7d23a64c74f80497af48797 AS builder
# Needed for vitepress to calulcate the last updated date for files
RUN apk add git
RUN npm install -g corepack
RUN corepack enable
WORKDIR /app
COPY . ./
RUN pnpm ci
RUN pnpm build

FROM cgr.dev/chainguard/nginx@sha256:9aa6c46648f17b2835d009fd4338b38591b2396670a07270ee19b532f83dbbb5
COPY --from=builder /app/dist /usr/share/nginx/html
# Replace the default server configuration of the base nginx image.
COPY nginx.conf /etc/nginx/conf.d/default.conf