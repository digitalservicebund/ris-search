FROM gradle:9.4.1-jdk21@sha256:5d5fa6d13c61373b8ae5ac11a3a5c9877d0fff04d6f54d24dabb808823527ce0 AS builder
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
