import { type H3Event, sendRedirect } from "h3";
import { authRedirectCookieName } from "~/server/auth";

export default defineEventHandler((event: H3Event) => {
  const redirectUrl = getCookie(event, authRedirectCookieName);
  deleteCookie(event, authRedirectCookieName);

  const isValidRedirectUrl = redirectUrl?.startsWith("/");
  if (!isValidRedirectUrl || !redirectUrl) {
    return sendRedirect(event, "/");
  }
  return sendRedirect(event, redirectUrl);
});
