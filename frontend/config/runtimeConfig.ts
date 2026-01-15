import type { NuxtConfig } from "nuxt/schema";
import { isDevelopment } from "./shared";

const secureCookie = !isDevelopment;

/** Configuration for the runtimeConfig section of Nuxt config. */
export const runtimeConfig: NuxtConfig["runtimeConfig"] = {
  basicAuth: "",
  auth: {
    webAuth: false,
  },
  session: {
    cookie: {
      secure: secureCookie, // workaround needed for Safari on localhost
    },
    password: "", // needs override in env
  },
  public: {
    risBackendUrl: "",
    /*
     * A feature flag that controls whether the private annotated features should,
     * be displayed or not, such features are for example: metadata, fassungen ...etc
     */
    privateFeaturesEnabled: false,
    sentryDSN: "",
    analytics: {
      posthogKey: "", // needs override in env
      posthogHost: "", // needs override in env
    },
  },
};
