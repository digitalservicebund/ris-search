#!/bin/bash

SCRIPT_DIR="$(dirname "$0")"

op run -- env BACKEND_AUTH_TOKEN="$(op read 'op://gqcg6tcubz42fqwr3cx23k3a24/portal-staging-basic-auth-header-value/basic-auth-header-value')" \
docker run --rm \
  --name local-staging-backend-proxy \
  -p 8090:8090 \
  -e BACKEND_AUTH_TOKEN \
  -v $SCRIPT_DIR/templates:/etc/nginx/templates \
  nginx:latest