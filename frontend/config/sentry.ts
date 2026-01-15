import type { NuxtConfig } from "nuxt/schema";
import { isStringEmpty } from "../src/utils/textFormatting";

/** Whether Sentry is enabled based on the presence of DSN. */
export const sentryEnabled = !isStringEmpty(process.env.NUXT_PUBLIC_SENTRY_DSN);

/** Configuration for the sentry section of Nuxt config. */
export const sentry: NuxtConfig["sentry"] = {
  sourceMapsUploadOptions: {
    org: "digitalservice",
    project: "ris-search",
    authToken: process.env.SENTRY_AUTH_TOKEN,
    telemetry: sentryEnabled,
    sourcemaps: {
      filesToDeleteAfterUpload: [".*/**/public/**/*.map"],
    },
  },
  enabled: sentryEnabled,
};
