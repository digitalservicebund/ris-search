import type { NuxtConfig } from "nuxt/schema";

/** Configuration for the routeRules section of Nuxt config. */
export const routeRules: NuxtConfig["routeRules"] = {
  // Temporarily disabled due to data issue with Form bricks
  "/nutzungstests": {
    redirect: "/",
  },
  "/sitemaps/administrative-directives/**": {
    proxy: {
      to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/administrative-directives/**`,
      headers: {
        Accept: "application/xml",
      },
    },
  },
  "/sitemaps/case-law/**": {
    proxy: {
      to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/case-law/**`,
      headers: {
        Accept: "application/xml",
      },
    },
  },
  "/sitemaps/literature/**": {
    proxy: {
      to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/literature/**`,
      headers: {
        Accept: "application/xml",
      },
    },
  },
  "/sitemaps/norms/**": {
    proxy: {
      to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/norms/**`,
      headers: {
        Accept: "application/xml",
      },
    },
  },
};
