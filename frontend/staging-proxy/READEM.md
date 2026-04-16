# Connect dev frontend to the staging backend

Sometimes you want to test new frontend features with backend data that you don't have locally.

The following command starts a nginx reverse proxy as a docker container which will forward calls to `localhost:8090` to the actual staging backend.
It reads the basic auth credentials from 1password.
First cd into `/staging-proxy` then run:

```bash
op run -- env BACKEND_AUTH_TOKEN="$(op read 'op://gqcg6tcubz42fqwr3cx23k3a24/portal-staging-basic-auth-header-value/basic-auth-header-value')" \
docker run -d \
  --name local-staging-backend-proxy \
  -p 8090:8090 \
  -e BACKEND_AUTH_TOKEN \
  -v ./templates:/etc/nginx/templates \
  nginx:latest
```
