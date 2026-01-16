import type { ModuleOptions } from "nuxt-security";
import { isDevelopment, isProduction } from "./shared";

/** Configuration for the security section of Nuxt config. */
export const security: Partial<ModuleOptions> = {
  strict: isProduction,
  headers: {
    referrerPolicy: "same-origin",
    contentSecurityPolicy: {
      "default-src": "'self'",
      "style-src": ["'self'", "https:", "'unsafe-inline'"],
      "img-src": ["'self'", "data:", "'unsafe-inline'"],
      "script-src": isDevelopment
        ? ["'strict-dynamic'", "'nonce-{{nonce}}'"]
        : ["'strict-dynamic'", "'nonce-{{nonce}}'", "https://*.posthog.com"],
      "connect-src": isDevelopment
        ? ["'self'", "http:"]
        : ["'self'", "https://*.posthog.com"],
      "worker-src": ["'self'", "blob:", "data:"],
    },
  },
  rateLimiter: {
    whiteList: isDevelopment ? ["127.0.0.1", "192.168.0.1"] : [],
    tokensPerInterval: 600,
    interval: 60000,
  },
};
