import type { ModuleOptions } from "@nuxtjs/sitemap";

/** Configuration for the sitemap section of Nuxt config. */
export const sitemap: Partial<ModuleOptions> = {
  sitemapsPathPrefix: "/sitemaps/",
  sitemaps: {
    static: {
      includeAppSources: true,
      exclude: [
        "/administrative-directives/**",
        "/case‑law/**",
        "/literature/**",
        "/norms/eli/**",
      ],
      defaults: { priority: 0.7 },
    },
  },
  appendSitemaps: [
    "/v1/sitemaps/administrative-directives/index.xml",
    "/v1/sitemaps/case-law/index.xml",
    "/v1/sitemaps/literature/index.xml",
    "/v1/sitemaps/norms/index.xml",
  ],
};
