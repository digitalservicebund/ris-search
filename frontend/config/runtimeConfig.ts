import fs from "node:fs";
import type { NuxtConfig } from "nuxt/schema";
import { isDevelopment } from "./shared";

const secureCookie = !isDevelopment;

/**
 * Configuration for the runtimeConfig section of Nuxt config. These should be
 * set in the environment variables. Please refer to the README for more
 * information on configuration.
 */

const getBasicAuth = () => {
  const path = "/etc/secrets/basic-auth/secret";
  if (fs.existsSync(path)) {
    return fs.readFileSync(path, "utf-8").trim();
  }
  return process.env.NUXT_BASIC_AUTH || "";
};

export const runtimeConfig: NuxtConfig["runtimeConfig"] = {
  basicAuth: getBasicAuth(),
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
    analytics: {
      posthogKey: "",
      posthogHost: "",
    },
  },
};
