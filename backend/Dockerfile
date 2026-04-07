# gradle:9.4.1-jdk21
FROM gradle@sha256:4a19481c230d8b5d41f17ea0c3cb9633f566be9ce498dccdb75aaa1282275f9f AS builder
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
