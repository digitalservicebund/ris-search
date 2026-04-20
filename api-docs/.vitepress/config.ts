import autoprefixer from "autoprefixer";
import tailwindcss from "tailwindcss";
import { defineConfigWithTheme, loadEnv } from "vitepress";
import type { ThemeConfig } from "./theme/types";
import { ensureStartingSlash, prependBase } from "./theme/utils";
import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import { PrimeVueResolver } from "unplugin-vue-components/resolvers";
import IconsResolver from "unplugin-icons/resolver";

const env = loadEnv("", process.cwd());
const base = ensureStartingSlash(env.VITE_BASE || "/");

export default defineConfigWithTheme<ThemeConfig>({
  srcDir: "./docs",
  outDir: "./dist",
  base,
  // Generate files as `/path/to/page.html` and URLs as `/path/to/page`
  cleanUrls: true,
  // Prevent builds when content has dead links
  ignoreDeadLinks: false,
  // Whether to get the last updated timestamp for each page using Git.
  lastUpdated: true,
  locales: {
    root: {
      label: "English",
      lang: "en",
      title: "API-Dokumentation des Rechtsinformationsportals des Bundes",
      description:
        "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
    },
  },
  head: [
    [
      "link",
      {
        rel: "icon",
        href: prependBase("/favicon.svg", base),
        type: "image/svg+xml",
      },
    ],
    [
      "link",
      {
        rel: "icon",
        href: prependBase("/favicon.png", base),
        type: "image/png",
      },
    ],
    [
      "link",
      {
        rel: "preload",
        href: prependBase("/icons.svg", base),
        as: "image",
        type: "image/svg+xml",
      },
    ],
    [
      "meta",
      {
        name: "title",
        content: "API-Dokumentation des Rechtsinformationsportals des Bundes",
      },
    ],
    [
      "meta",
      {
        name: "description",
        content:
          "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
      },
    ],
    [
      "meta",
      {
        property: "og:type",
        content: "website",
      },
    ],
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
        property: "og:description",
        content:
          "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
      },
    ],
    [
      "meta",
      {
        property: "og:url",
        content: "https://docs.rechtsinformationen.bund.de",
      },
    ],
    [
      "meta",
      {
        property: "og:image",
        content: "https://testphase.rechtsinformationen.bund.de/og_image.png",
      },
    ],
    [
      "meta",
      {
        name: "twitter:card",
        content: "summary_large_image",
      },
    ],
    [
      "meta",
      {
        name: "twitter:title",
        content: "API-Dokumentation des Rechtsinformationsportals des Bundes",
      },
    ],
    [
      "meta",
      {
        name: "twitter:description",
        content:
          "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
      },
    ],
    [
      "meta",
      {
        name: "twitter:image",
        content: "https://testphase.rechtsinformationen.bund.de/og_image.png",
      },
    ],
  ],
  themeConfig: {
    secondaryNav: [
      {
        text: "Legal Notice",
        link: "https://digitalservice.bund.de/en/legal-notice",
      },
      { text: "Contact", link: "/contact/" },
      {
        text: "Data Privacy",
        link: "https://digitalservice.bund.de/en/data-privacy",
      },
    ],
    contactEmail: "rechtsinformationsportal@digitalservice.bund.de",
    outline: [2, 3],
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

  vite: {
    plugins: [
      Components({
        resolvers: [IconsResolver(), PrimeVueResolver()],
      }),
      Icons({
        scale: 1.3333, // ~24px at the current default font size of 18px
        autoInstall: true,
      }),
    ],
    css: {
      postcss: {
        plugins: [tailwindcss(), autoprefixer()],
      },
    },
  },
});
