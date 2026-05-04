FROM gradle:9.5.0-jdk21@sha256:f2796e317ff13e2d0e10b83bb4aed71721d51570b55829972a5d9d89a6e96381
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
