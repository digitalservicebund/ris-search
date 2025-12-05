FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /backend
RUN apt-get purge git-lfs -y
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
