import { sendRedirect } from "h3";
import { authRedirectCookieName } from "~/server/auth";

export default defineEventHandler<{ query: { redirectTo?: string } }>(
  (event) => {
    const query = getQuery(event);
    if (query.redirectTo) {
      setCookie(event, authRedirectCookieName, query.redirectTo);
    }
    return sendRedirect(event, "/auth/keycloak");
  },
);
