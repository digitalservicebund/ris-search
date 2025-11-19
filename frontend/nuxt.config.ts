import tailwindcss from "@tailwindcss/vite";
import { defineNuxtConfig } from "nuxt/config";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import { PrimeVueResolver } from "unplugin-vue-components/resolvers";
import Components from "unplugin-vue-components/vite";
import { getStringOrDefault, isStringEmpty } from "./src/utils/textFormatting";

const config = {
  devMode: process.env.NODE_ENV == "development",
  production: process.env.NODE_ENV == "production",
  e2eTest: process.env.NUXT_PUBLIC_CI == "true",
};

const sentryEnabled = !isStringEmpty(process.env.NUXT_PUBLIC_SENTRY_DSN);

const secureCookie = !config.devMode;

export default defineNuxtConfig({
  app: {
    head: {
      title: undefined, // set dynamically in app.vue useHead
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
    },
  },
  srcDir: "src/",
  dir: {
    public: "src/public",
  },
  serverDir: "src/server",
  css: ["~/assets/main.css"],
  devServer: {
    port: parseInt(getStringOrDefault(process.env.PORT, "3000")),
  },
  extends: [],
  modules: [
    "@nuxt/image",
    "@nuxt/eslint",
    "unplugin-icons/nuxt",
    "@pinia/nuxt",
    "@nuxt/test-utils/module",
    "@sentry/nuxt/module",
    "nuxt-security",
    "@nuxt/scripts",
    "@nuxtjs/sitemap",
  ],
  devtools: {
    enabled: true,
  },
  experimental: {
    renderJsonPayloads: true,
  },
  runtimeConfig: {
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
      /*
       * This Url should only be different if the application is connecting from inside a container
       * and that the url is different from inside the container than from client side. Otherwise it should be
       * left empty
       */
      risBackendUrlSsr: "",
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
      ci: false,
    },
  },
  security: {
    strict: config.production,
    headers: {
      referrerPolicy: "same-origin",
      contentSecurityPolicy: {
        "style-src": ["'self'", "https:", "'unsafe-inline'"],
        "img-src": ["'self'", "data:", "'unsafe-inline'"],
        "script-src": ["'strict-dynamic'", "'nonce-{{nonce}}'"],
        "connect-src": ["'self'", "http:"],
        "upgrade-insecure-requests": false,
      },
    },
    rateLimiter: {
      whiteList: config.devMode ? ["127.0.0.1", "192.168.0.1"] : [],
      tokensPerInterval: 600,
      interval: 60000,
    },
  },
  sentry: {
    sourceMapsUploadOptions: {
      org: "digitalservice",
      project: "ris-search",
      authToken: process.env.SENTRY_AUTH_TOKEN,
      telemetry: sentryEnabled,
      sourcemaps: {
        filesToDeleteAfterUpload: [".*/**/public/**/*.map"],
      },
    },
    enabled: config.production,
  },
  sitemap: {
    sitemapsPathPrefix: "/sitemaps/",
    sitemaps: {
      static: {
        includeAppSources: true,
        exclude: ["/case‑law/**", "/norms/eli/**"],
        defaults: { priority: 0.7 },
      },
    },
    appendSitemaps: [
      "/sitemaps/norms/index.xml",
      "/sitemaps/caselaw/index.xml",
    ],
  },
  nitro: {
    rollupConfig: {
      output: {
        sourcemap: "hidden",
      },
    },
  },
  routeRules: {
    // Temporarily disabled due to data issue with Form bricks
    "/nutzungstests": {
      redirect: "/",
    },
    "/sitemaps/norms/**": {
      proxy: {
        to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/norms/**`,
        headers: {
          Accept: "application/xml",
        },
      },
    },
    "/sitemaps/caselaw/**": {
      proxy: {
        to: `${process.env.NUXT_PUBLIC_RIS_BACKEND_URL}/v1/sitemaps/caselaw/**`,
        headers: {
          Accept: "application/xml",
        },
      },
    },
  },
  vite: {
    optimizeDeps: {
      include: ["cookie"],
    },
    build: {
      sourcemap: "inline",
    },
    plugins: [
      tailwindcss(),
      Components({
        resolvers: [IconsResolver(), PrimeVueResolver()],
      }),
      Icons({
        scale: 1.3333, // ~24px at the current default font size of 18px
      }),
    ],
  },
  sourcemap: {
    server: true,
    client: sentryEnabled ? "hidden" : config.devMode,
  },
  typescript: {
    typeCheck: true,
    tsConfig: {
      compilerOptions: {
        types: ["node", "vitest", "vitest/globals", "unplugin-icons/types/vue"],
      },
    },
    nodeTsConfig: {
      include: ["../e2e/**/*.ts"],
    },
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
  ignore: [
    "**/**/*.{spec,test}.{js,cts,mts,ts,jsx,tsx}",
    "**/**/*.{spec,test}.data.ts",
  ],
  compatibilityDate: "2024-11-01",
});
