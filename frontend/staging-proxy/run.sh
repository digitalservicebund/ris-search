#!/bin/bash

SCRIPT_DIR="$(dirname "$0")"

op run -- env BACKEND_AUTH_TOKEN="$(op read 'op://gqcg6tcubz42fqwr3cx23k3a24/portal-staging-basic-auth-header-value/basic-auth-header-value')" \
docker run --rm \
  --name local-staging-backend-proxy \
  -p 127.0.0.1:8080:8080 \
  -e BACKEND_AUTH_TOKEN \
  -v $SCRIPT_DIR/templates:/etc/nginx/templates \
  nginx@sha256:08f354a3b44c422021a5c0f5efb3e27f8f90236b5447d3bac4053f5f50ab6760 # v. 1.31.9
