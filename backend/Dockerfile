FROM gradle:8.5-jdk17 AS builder
ARG SENTRY_RELEASE
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
ENV SENTRY_RELEASE=${SENTRY_RELEASE}
CMD ["gradle", "bootRun"]
