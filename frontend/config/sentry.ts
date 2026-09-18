import type { NuxtConfig } from "nuxt/schema";
import { isProduction } from "./shared";

/** Configuration for the sentry section of Nuxt config. */
export const sentry: NuxtConfig["sentry"] = {
  enabled: isProduction,
  org: "digitalservice",
  project: "ris-search",
  authToken: process.env.SENTRY_AUTH_TOKEN,
  sourceMapsUploadOptions: {
    // Only upload the source maps when built on main (set via SENTRY_UPLOAD_SOURCEMAPS docker build arg)
    enabled: process.env.SENTRY_UPLOAD_SOURCEMAPS === "true",
  },
  telemetry: false,
};
