FROM gradle:9.5.0-jdk21@sha256:41cd88d5934d5880ea7e3ebd53d711155e7e1c989390f0f9fa3dfd7d6b742a28
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
