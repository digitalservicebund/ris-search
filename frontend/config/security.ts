import type { ModuleOptions } from "nuxt-security";
import { isDevelopment, isProduction } from "./shared";

const allowedExternalSrc = isDevelopment
  ? []
  : ["https://*.posthog.com", "https://*.ingest.us.sentry.io"];

/** Configuration for the security section of Nuxt config. */
export const security: Partial<ModuleOptions> = {
  strict: isProduction,
  headers: {
    referrerPolicy: "same-origin",
    contentSecurityPolicy: {
      "default-src": "'self'",
      "style-src": ["'self'", "https:", "'unsafe-inline'"],
      "img-src": ["'self'", "data:", "'unsafe-inline'"],
      "script-src": [
        "'strict-dynamic'",
        "'nonce-{{nonce}}'",
        ...allowedExternalSrc,
      ],
      "connect-src": isDevelopment
        ? ["'self'", "http:"]
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
