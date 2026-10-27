import type { ModuleOptions } from "nuxt-security";
import { isDevelopment, isProduction } from "./shared";

/**
 * PostHog is limited to its EU domains, so a misconfigured host can't send data
 * to the US cloud. (PostHog doesn't recommend listing specific domains, as they
 * may change, see https://posthog.com/docs/advanced/content-security-policy.)
 * The CSP only applies in the browser, which is fine as we don't track on the
 * server.
 */
const allowedExternalSrc = [
  "https://eu.i.posthog.com",
  "https://eu-assets.i.posthog.com",
  "https://*.sentry.io/",
];

/** Configuration for the security section of Nuxt config. */
export const security: Partial<ModuleOptions> = {
  strict: isProduction,
  headers: {
    referrerPolicy: "same-origin",
    contentSecurityPolicy: {
      "upgrade-insecure-requests": !isDevelopment,
      "default-src": "'self'",
      "style-src": ["'self'", "https:", "'unsafe-inline'"],
      "img-src": ["'self'", "data:", "'unsafe-inline'"],
      "script-src": [
        "'strict-dynamic'",
        "'nonce-{{nonce}}'",
        ...allowedExternalSrc,
      ],
      "connect-src": isDevelopment
        ? ["'self'", "http:", ...allowedExternalSrc]
        : ["'self'", ...allowedExternalSrc],
      "worker-src": ["'self'", "blob:", "data:"],
    },
  },
  rateLimiter: {
    whiteList: isDevelopment ? ["127.0.0.1", "192.168.0.1"] : [],
    tokensPerInterval: 600,
    interval: 60000,
  },
};
