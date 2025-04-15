import type { H3Event } from "h3";
import { buildSessionAttributes } from "./keycloakUtils";
import { authRedirectCookieName } from "@/server/auth";

const getRedirectUrl = (event: H3Event) => {
  const redirectUrl = getCookie(event, authRedirectCookieName) || "/";
  deleteCookie(event, authRedirectCookieName);

  if (redirectUrl.startsWith("/")) {
    return redirectUrl;
  }
  return "/";
};

export default defineOAuthKeycloakEventHandler({
  async onSuccess(event, { user, tokens }) {
    await setUserSession(event, {
      user: {
        name: user.name || user.preferred_username,
        email: user.email,
      },
      ...buildSessionAttributes(tokens),
    });

    return sendRedirect(event, getRedirectUrl(event));
  },
  async onError(event: H3Event) {
    const query = getQuery(event);
    // see https://www.keycloak.org/securing-apps/oidc-layers#_oidc-errors
    if (query["error_description"] === "authentication_expired") {
      console.log("Keycloak authentication_expired");
      return sendRedirect(event, "/auth/keycloak");
    }
  },
});
