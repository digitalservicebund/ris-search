import type { ModuleOptions } from "nuxt-security";
import { usePrivateFeaturesFlag } from "../src/composables/usePrivateFeaturesFlag";
import { isDevelopment, isProduction } from "./shared";

/** Configuration for the security section of Nuxt config. */
const hasPosthog = !usePrivateFeaturesFlag();

export const security: Partial<ModuleOptions> = {
  strict: isProduction,
  headers: {
    referrerPolicy: "same-origin",
    contentSecurityPolicy: {
      "style-src": ["'self'", "https:", "'unsafe-inline'"],
      "img-src": ["'self'", "data:"],
      "script-src": [
        "'strict-dynamic'",
        "'nonce-{{nonce}}'",
        ...(hasPosthog ? ["https://*.posthog.com"] : []),
      ],
      "connect-src": [
        "'self'",
        ...(hasPosthog ? ["https://*.posthog.com"] : []),
        ...(isDevelopment ? ["http:"] : []),
      ],
      "worker-src": ["'self'", "blob:", "data:"],
    },
  },
  rateLimiter: {
    whiteList: isDevelopment ? ["127.0.0.1", "192.168.0.1"] : [],
    tokensPerInterval: 600,
    interval: 60000,
  },
};
