import type { SessionConfig } from "h3";

// schema extension to fix type errors in runtimeConfig.session of nuxt.config.ts
declare module "nuxt/schema" {
  interface PublicRuntimeConfig {
    session: SessionConfig;
  }
}
