import type { H3Event } from "h3";
import { buildSessionAttributes } from "~/server/utils/keycloak";

export default defineOAuthKeycloakEventHandler({
  async onSuccess(event, { user, tokens }) {
    await setUserSession(event, {
      user: {
        name: user.name || user.preferred_username,
        email: user.email,
      },
      ...buildSessionAttributes(tokens),
    });

    return sendRedirect(event, "/auth/success");
  },
  async onError(event: H3Event, error: Error) {
    const query = getQuery(event);
    // see https://www.keycloak.org/securing-apps/oidc-layers#_oidc-errors
    if (query["error_description"] === "authentication_expired") {
      console.log("Keycloak authentication_expired");
      return sendRedirect(event, "/auth/keycloak");
    } else {
      console.error("keycloak error", error);
      return sendError(event, error, false);
    }
  },
});
