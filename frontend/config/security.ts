import type { ModuleOptions } from "nuxt-security";

const devMode = process.env.NODE_ENV === "development";
const production = process.env.NODE_ENV === "production";

/** Configuration for the security section of Nuxt config. */
export const security: Partial<ModuleOptions> = {
  strict: production,
  headers: {
    referrerPolicy: "same-origin",
    contentSecurityPolicy: {
      "style-src": ["'self'", "https:", "'unsafe-inline'"],
      "img-src": ["'self'", "data:", "'unsafe-inline'"],
      "script-src": ["'strict-dynamic'", "'nonce-{{nonce}}'"],
      "connect-src": devMode ? ["'self'", "http:"] : ["'self'"],
    },
  },
  rateLimiter: {
    whiteList: devMode ? ["127.0.0.1", "192.168.0.1"] : [],
    tokensPerInterval: 600,
    interval: 60000,
  },
};
