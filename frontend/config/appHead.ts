import type { NuxtAppConfig } from "nuxt/schema";

/** Configuration for the app.head section of Nuxt config. */
export const appHead: NuxtAppConfig["head"] = {
  title: undefined, // set dynamically in app.vue using useHead
  meta: [
    {
      name: "description",
      content:
        "Gesetze & Verordnungen, Gerichtsentscheidungen und Verwaltungsvorschriften",
    },
    { property: "og:image", content: "/og_image.png" },
    { name: "twitter:image", content: "/og_image.png" },
    { name: "twitter:card", content: "summary_large_image" },
    { name: "theme-color", content: "#ffffff" },
    { name: "msapplication-TileColor", content: "#ffffff" },
    {
      name: "apple-mobile-web-app-title",
      content: "Rechtsinformationen des Bundes",
    },
  ],
  htmlAttrs: {
    lang: "de",
  },
  charset: "utf-8",
  viewport: "width=device-width, initial-scale=1",
  link: [
    { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
    { rel: "icon", type: "image/svg+xml", href: "/favicon.svg" },
    {
      rel: "icon",
      type: "image/png",
      sizes: "16x16",
      href: "/favicon-16x16.png",
    },
    {
      rel: "icon",
      type: "image/png",
      sizes: "32x32",
      href: "/favicon-32x32.png",
    },
    {
      rel: "icon",
      type: "image/png",
      sizes: "96x96",
      href: "/favicon-96x96.png",
    },
    {
      rel: "apple-touch-icon",
      sizes: "180x180",
      href: "/apple-touch-icon.png",
    },
    {
      rel: "icon",
      type: "image/png",
      sizes: "192x192",
      href: "/android-chrome-192x192.png",
    },
    {
      rel: "icon",
      type: "image/png",
      sizes: "512x512",
      href: "/android-chrome-512x512.png",
    },
    { rel: "mask-icon", href: "/favicon.svg", color: "#0b3d91" },
    { rel: "manifest", href: "/site.webmanifest" },
  ],
};
