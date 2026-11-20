import { defineConfig } from "vitepress";

export default defineConfig({
  outDir: "./dist",
  cleanUrls: true,
  lastUpdated: true,
  title: "API Documentation of the Federal Legal Data Portal",
  description:
    "Technical documentation and API reference for accessing federal legal data.",
  head: [
    ["link", { rel: "icon", href: "/favicon.svg", type: "image/svg+xml" }],
    ["link", { rel: "icon", href: "/favicon.png", type: "image/png" }],
    ["meta", { property: "og:type", content: "website" }],
    [
      "meta",
      {
        property: "og:title",
        content: "API Documentation of the Federal Legal Data Portal",
      },
    ],
    [
      "meta",
      {
        property: "og:url",
        content: "https://docs.rechtsinformationen.bund.de",
      },
    ],
  ],
  themeConfig: {
    siteTitle: "German Legal Data API Docs",
    nav: [
      {
        component: "SwaggerButton",
      },
    ],
    sidebar: [
      {
        text: "Get Started",
        link: "/get-started/",
      },
      {
        text: "Standards",
        link: "/standards/",
      },
      {
        text: "Use Cases",
        link: "/use-cases/",
        items: [
          { text: "Find legislation", link: "/use-cases/find-legislation/" },
          {
            text: "Get a specific version",
            link: "/use-cases/legislation-version/",
          },
          {
            text: "Get a single article (§)",
            link: "/use-cases/legislation-article/",
          },
          {
            text: "Find a court decision",
            link: "/use-cases/find-court-decision/",
          },
          { text: "Choose a format", link: "/use-cases/formats/" },
          { text: "Download everything", link: "/use-cases/bulk-download/" },
          { text: "Stay up to date", link: "/use-cases/stay-up-to-date/" },
        ],
      },
      {
        text: "Guides",
        link: "/guides/",
        items: [
          { text: "Formats", link: "/guides/formats/" },
          { text: "Pagination", link: "/guides/pagination/" },
          { text: "Filters", link: "/guides/filters/" },
          { text: "Rate Limiting", link: "/guides/rate-limiting/" },
          { text: "Error Codes", link: "/guides/error-codes/" },
        ],
      },
      { text: "Feedback", link: "/feedback/" },
    ],
    socialLinks: [
      {
        icon: "github",
        link: "https://github.com/digitalservicebund/ris-search",
      },
    ],
  },
  markdown: {
    theme: {
      light: "github-light",
      dark: "github-dark",
    },
  },
  sitemap: {
    hostname: "https://docs.rechtsinformationen.bund.de/",
  },
});
