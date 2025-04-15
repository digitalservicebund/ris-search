# Authentication logic

The RIS portal uses Keycloak for authentication for the internal portal, as well as for the
internal portal during the evaluation period.

## Login flow

The browser is expected to navigate to the `/auth` route, which is handled by
`_shared/server/routes/auth/index.get.ts`.
A redirectTo query string parameter may be used to trigger setting of a cookie that will
ensure the original page is opened after successful completion of the sign-in flow.

The `/auth` endpoint will issue a redirect to `/auth/keycloak`. Here, most of the interaction
with keycloak is handled by `defineOAuthKeycloakEventHandler`, provided by Nuxt plugin
`nuxt-auth-utils`.

`nuxt-auth-utils` supports sealed sessions, meaning that the JWTs used to communicate
with the APIs are not available directly to the client. Instead, the Nuxt API will
decrypt the JWT value and call the API endpoints on the user agent's behalf.

### Additional endpoints

The handler `index.delete.ts` provides functionality to invalidate Keycloak session, meaning
that the user will need to sign in again to obtain new tokens.

The handler `refresh.get.ts` allows clients to obtain new access tokens `refreshToken`s.
