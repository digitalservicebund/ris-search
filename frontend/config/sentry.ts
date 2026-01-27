import type { NuxtConfig } from "nuxt/schema";
import { isProduction } from "./shared";

/** Configuration for the sentry section of Nuxt config. */
export const sentry: NuxtConfig["sentry"] = {
  enabled: isProduction,
  org: "digitalservice",
  project: "ris-search",
  authToken: process.env.SENTRY_AUTH_TOKEN,
  sourcemaps: { filesToDeleteAfterUpload: [".*/**/public/**/*.map"] },
  telemetry: false,
};
