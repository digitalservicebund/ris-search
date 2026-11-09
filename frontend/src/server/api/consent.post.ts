import {
  createError,
  defineEventHandler,
  getHeader,
  getRequestURL,
  readBody,
  sendRedirect,
  setCookie,
} from "h3";
import { getSameOriginUrl } from "~/server/utils/sameOriginUrl";
import {
  CONSENT_COOKIE_MAX_AGE,
  CONSENT_COOKIE_NAME,
  formatConsentCookie,
} from "~/utils/postHog";

interface ConsentBody {
  consent?: string;
  // Repeated form fields arrive as an array
  redirect?: unknown;
}

/**
 * Returns the redirect target if it points to the same origin, or the home page
 * otherwise. Prevents the endpoint from being used as an open redirect.
 */
function getSafeRedirectPath(redirect: unknown, origin: string) {
  if (typeof redirect !== "string") return "/";
  if (!redirect.startsWith("/")) return "/";

  const url = getSameOriginUrl(redirect, origin);
  if (!url) return "/";
  return `${url.pathname}${url.search}${url.hash}`;
}

/**
 * Sets the consent cookie when JavaScript is disabled. Writes the same cookie
 * PostHog writes on the client, so both stay interchangeable.
 */
export default defineEventHandler(async (event) => {
  // Without this, other sites could submit a form that grants consent on the
  // user's behalf. Browsers that don't send the header are allowed.
  const fetchSite = getHeader(event, "sec-fetch-site");
  if (fetchSite && fetchSite !== "same-origin") {
    throw createError({ statusCode: 403 });
  }

  const body = await readBody<ConsentBody>(event);
  const isGranted = body?.consent === "granted";

  setCookie(event, CONSENT_COOKIE_NAME, formatConsentCookie(isGranted), {
    maxAge: CONSENT_COOKIE_MAX_AGE,
    path: "/",
    sameSite: "lax",
    secure: !import.meta.dev,
  });

  const { origin } = getRequestURL(event);
  return sendRedirect(event, getSafeRedirectPath(body?.redirect, origin), 303);
});
