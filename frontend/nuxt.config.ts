import tailwindcss from "@tailwindcss/vite";
import { defineNuxtConfig } from "nuxt/config";
import { FileSystemIconLoader } from "unplugin-icons/loaders";
import Icons from "unplugin-icons/vite";
import { appHead } from "./config/appHead";
import { routeRules } from "./config/routeRules";
import { runtimeConfig } from "./config/runtimeConfig";
import { security } from "./config/security";
import { sentry } from "./config/sentry";
import { sitemap } from "./config/sitemap";
import { getStringOrDefault } from "./src/utils/textFormatting";

export default defineNuxtConfig({
  // Nuxt core settings
  compatibilityDate: "2024-11-01",
  modules: [
    "@nuxt/eslint",
    "unplugin-icons/nuxt",
    "@nuxt/test-utils/module",
    "@sentry/nuxt/module",
    "nuxt-security",
    "@nuxt/scripts",
    "@nuxtjs/sitemap",
    "@nuxtjs/mdc",
  ],
  experimental: {
    renderJsonPayloads: true,
  },
  srcDir: "src/",
  dir: {
    public: "src/public",
  },
  serverDir: "src/server",

  // App & assets
  app: {
    head: appHead,
  },
  css: ["~/assets/main.css"],
  ignore: [
    "**/**/*.{spec,test}.{js,cts,mts,ts,jsx,tsx}",
    "**/**/*.{spec,test}.data.ts",
  ],

  // Routing & runtime
  routeRules,
  runtimeConfig,

  // Build
  vite: {
    plugins: [
      tailwindcss(),
      Icons({
        scale: 1.3333, // ~24px at the current default font size of 18px
        customCollections: {
          custom: FileSystemIconLoader("./src/assets/icons"),
        },
      }),
    ],
  },
  typescript: {
    typeCheck: process.env.CI !== "true",
    tsConfig: {
      compilerOptions: {
        types: ["node", "vitest", "vitest/globals", "unplugin-icons/types/vue"],
      },
    },
    nodeTsConfig: {
      include: ["../e2e/**/*.ts"],
    },
  },
  sourcemap: {
    server: "hidden",
    client: "hidden",
  },
  vue: {
    compilerOptions: {
      // Tell Vue to treat the native HTML search element as a custom element,
      // i.e. not try to resolve it as a component. Workaround to be able to
      // use this element while waiting for https://github.com/vuejs/core/pull/9249
      // to be released.
      isCustomElement: (tag) => ["search"].includes(tag),
    },
  },

  // Modules
  mdc: {
    headings: {
      anchorLinks: false,
    },
  },
  security,
  sentry,
  sitemap,

  // Development
  devServer: {
    port: parseInt(getStringOrDefault(process.env.PORT, "3000")),
  },
  devtools: {
    enabled: process.env.CI !== "true",
  },
});
