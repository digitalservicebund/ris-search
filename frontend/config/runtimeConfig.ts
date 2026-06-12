import type { NuxtConfig } from "nuxt/schema";
import { isDevelopment } from "./shared";

const secureCookie = !isDevelopment;

/**
 * Configuration for the runtimeConfig section of Nuxt config. These should be
 * set in the environment variables. Please refer to the README for more
 * information on configuration.
 */
export const runtimeConfig: NuxtConfig["runtimeConfig"] = {
  basicAuth: "",
  auth: {
    webAuth: false,
  },
  session: {
    cookie: {
      secure: secureCookie, // workaround needed for Safari on localhost
    },
  },
  public: {
    risBackendUrl: "",
    privateFeaturesEnabled: false,
    sentryDSN: "",
    sentryEnvironment: "",
    analytics: {
      posthogKey: "",
      posthogHost: "",
    },
  },
};
