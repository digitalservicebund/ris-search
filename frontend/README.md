# Portal Frontend

## Setup

To run the portal frontend, run

```bash
yarn install
cp .env.example .env
yarn dev
```

For authentication, you will also need to have a local Keycloak server running. The easiest way would be to start
it using docker-compose:

```bash
docker compose up -d keycloak
```

In order to use the search and to view files, the backend and backend dependencies will need to be running. Refer to
the [main README](../README.md) for further instructions.

The frontend will be available at [localhost:3000](http://localhost:3000).

The frontend has a feature flag that enables or disables private features distinguishing internal from public features,

- public: the features are allowed to be seen by the public and are accessible online without authentication
- private: the features require authentication and are nto allowed to be shown yet to the public

The default one is that private features are disabled. Set `NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED=false`
to see the feature flag variant (the NUXT_PUBLIC prefix only means that the value will be accessible in the browser).

To use the public profile (e.g. for prototype), you may use the public env file by running

```shell
yarn dev --dotenv .env.public
```

## Logging in

When asked for a username/password combination, you may use sample data from
[the keycloak config](../local/keycloak/realm.json),
values `users[0].username` and `users[0].credentials[0].value`.
