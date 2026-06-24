FROM gradle:9.6.0-jdk21@sha256:6b71d4d40e45200709c178ca0ab10125e1d99e6e44dec6bcbabd0a3371f8e650
WORKDIR /backend
COPY --chown=gradle:gradle . /app/backend
WORKDIR /app/backend
EXPOSE 8090
CMD ["gradle", "bootRun"]
