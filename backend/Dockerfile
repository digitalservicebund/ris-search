FROM gradle:9.5.1-jdk21@sha256:4a011ed0edfe2feb7dace27d1fedf22caf217918b59932a95605cb7beec142c4
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
