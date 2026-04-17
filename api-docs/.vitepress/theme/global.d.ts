import type { PostHog } from "posthog-js";

declare global {
  interface Window {
    posthog: PostHog;
  }

  interface ImportMetaEnv {
    readonly SSR: boolean;
  }

  interface ImportMeta {
    readonly env: ImportMetaEnv;
  }
}
