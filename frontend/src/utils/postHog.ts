/**
 * Name of the cookie in which PostHog persists the user's tracking consent.
 * PostHog writes it when calling `opt_in_capturing` or `opt_out_capturing`.
 */
export const CONSENT_COOKIE_NAME = "consent_given";

export type ConsentStatus = "granted" | "denied" | "pending";

const grantedValues = new Set<unknown>([true, 1, "true", "1", "yes"]);
const deniedValues = new Set<unknown>([false, 0, "false", "0", "no"]);

/**
 * Reads the consent status from the value of the consent cookie. Accepts the
 * same values as PostHog does, so the result matches PostHog's own reading of
 * the cookie.
 *
 * @param value - Value of the consent cookie
 * @returns The consent status
 */
export function parseConsentCookie(value: unknown): ConsentStatus {
  if (grantedValues.has(value)) return "granted";
  if (deniedValues.has(value)) return "denied";
  return "pending";
}

/**
 * How long the consent cookie is kept, in seconds. Matches PostHog's default
 * `cookie_expiration` of 365 days.
 */
export const CONSENT_COOKIE_MAX_AGE = 365 * 24 * 60 * 60;

/**
 * Returns the value PostHog writes to the consent cookie, so a cookie written
 * by the server is read the same way as one written by PostHog.
 *
 * @param isGranted - Whether the user accepted tracking
 * @returns The cookie value
 */
export function formatConsentCookie(isGranted: boolean): string {
  return isGranted ? "1" : "0";
}
