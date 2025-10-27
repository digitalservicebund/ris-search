export default defineEventHandler(async (event) => {
  const body = await readBody(event);
  const consent = body.consent === "true" || body.consent === true;

  const isDevMode = process.env.NODE_ENV === "development";

  setCookie(event, "consent_given", consent.toString(), {
    maxAge: 365 * 24 * 60 * 60,
    path: "/",
    sameSite: "lax",
    secure: !isDevMode,
  });

  const referer = getHeader(event, "referer");
  let redirectPath = "/";

  if (referer) {
    try {
      const refererUrl = new URL(referer);
      const requestUrl = getRequestURL(event);

      if (refererUrl.origin === requestUrl.origin) {
        redirectPath = refererUrl.pathname + refererUrl.search;
      }
    } catch {
      redirectPath = "/";
    }
  }

  return sendRedirect(event, redirectPath);
});
