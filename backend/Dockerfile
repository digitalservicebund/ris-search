FROM gradle:9.4.1-jdk21@sha256:2d34fc215310891039e1b2b7797632de8d59eab54496c134bc2da711ba651c50 AS builder
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
