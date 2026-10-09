import type { NuxtConfig } from "nuxt/schema";
import { isProduction } from "./shared";

const uploadSourceMaps = process.env.SENTRY_UPLOAD_SOURCEMAPS === "true";

/** Configuration for the sentry section of Nuxt config. */
export const sentry: NuxtConfig["sentry"] = {
  enabled: isProduction,
  org: "digitalservice",
  project: "ris-search",
  authToken: process.env.SENTRY_AUTH_TOKEN,
  sourcemaps: {
    disable: uploadSourceMaps ? false : "disable-upload",
  },
  telemetry: false,
};
