import type { NuxtConfig } from "nuxt/schema";
import { isStringEmpty } from "../src/utils/textFormatting";

/** Configuration for the sentry section of Nuxt config. */
export const sentry: NuxtConfig["sentry"] = {
  sourceMapsUploadOptions: {
    org: "digitalservice",
    project: "ris-search",
    authToken: process.env.SENTRY_AUTH_TOKEN,
    sourcemaps: {
      filesToDeleteAfterUpload: [".*/**/public/**/*.map"],
    },
  },
  enabled: !isStringEmpty(process.env.NUXT_PUBLIC_SENTRY_DSN),
};
