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

The frontend comes in different variants,

- public: the version to be released to the public end-of-year 2025
- prototype: a test phase version of the public prototype with fewer data fields
- internal: an advanced version showing additional metadata, to be used internally by public administration officials

The default one is public. Set `NUXT_PUBLIC_PROFILE=internal`
to see the internal variant (the NUXT_PUBLIC prefix only means that the value will be accessible in the browser).

To use the prototype profile, you may use the prototype env file by running

```shell
yarn dev --dotenv .env.prototype
```

## Logging in

When asked for a username/password combination, you may use sample data from
[the keycloak config](../local/keycloak/realm.json),
values `users[0].username` and `users[0].credentials[0].value`.
