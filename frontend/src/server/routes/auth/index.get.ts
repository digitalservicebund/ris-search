import { sendRedirect } from "h3";
import { authRedirectCookieName } from "~/server/auth";

export default defineEventHandler((event) => {
  const query = getQuery(event);
  if (query.redirectTo) {
    setCookie(event, authRedirectCookieName, query.redirectTo.toString());
  }
  return sendRedirect(event, "/auth/keycloak");
});
