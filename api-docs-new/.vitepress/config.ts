import { defineConfig } from "vitepress";

export default defineConfig({
  outDir: "./dist",
  cleanUrls: true,
  lastUpdated: true,
  title: "API-Dokumentation des Rechtsinformationsportals des Bundes",
  description:
    "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
  head: [
    ["link", { rel: "icon", href: "/favicon.svg", type: "image/svg+xml" }],
    ["link", { rel: "icon", href: "/favicon.png", type: "image/png" }],
    [
      "meta",
      {
        name: "description",
        content:
          "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
      },
    ],
    ["meta", { property: "og:type", content: "website" }],
    [
      "meta",
      {
        property: "og:title",
        content: "API-Dokumentation des Rechtsinformationsportals des Bundes",
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
    siteTitle: "RIS API Docs",
    nav: [
      { text: "Get Started", link: "/get-started/" },
      { text: "Standards", link: "/standards/" },
      {
        text: "Guides",
        items: [
          { text: "Formats", link: "/guides/formats/" },
          { text: "Pagination", link: "/guides/pagination/" },
          { text: "Filters", link: "/guides/filters/" },
          { text: "Rate Limiting", link: "/guides/rate-limiting/" },
          { text: "Error Codes", link: "/guides/error-codes/" },
        ],
      },
      { text: "Changelog", link: "/changelog/" },
      { text: "Feedback", link: "/feedback/" },
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
      { text: "Changelog", link: "/changelog/" },
      { text: "Feedback", link: "/feedback/" },
      { text: "Contact", link: "/contact/" },
    ],
    socialLinks: [
      {
        icon: "github",
        link: "https://github.com/digitalservicebund/ris-search",
      },
    ],
    footer: {
      message:
        '<a href="https://digitalservice.bund.de/en/legal-notice">Legal Notice</a> · <a href="/contact/">Contact</a> · <a href="https://digitalservice.bund.de/en/data-privacy">Data Privacy</a>',
    },
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
