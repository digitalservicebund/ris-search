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
      ],
      htmlAttrs: {
        lang: "de",
      },
      charset: "utf-8",
      viewport: "width=device-width, initial-scale=1",
      link: [
        { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
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
        { rel: "manifest", href: "/site.webmanifest" },
      ],
    },
  },
  srcDir: "src/",
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
    "nuxt-auth-utils",
    "nuxt-security",
    "@nuxt/scripts",
    "@nuxtjs/sitemap",
  ],
  imports: {
    scan: false,
  },
  devtools: {
    enabled: true,
  },
  experimental: {
    renderJsonPayloads: true,
  },
  runtimeConfig: {
    auth: {
      webAuth: false,
    },
    /* Backend host to the spring backend, used by /proxy routes */
    risBackendUrl: "http://localhost:8090",
    session: {
      cookie: {
        secure: secureCookie, // workaround needed for Safari on localhost
      },
      password: "", // needs override in env
    },
    public: {
      /*
       * Host that is used by the browser to retrieve resources from the backend
       * 1. use http://localhost:8090 to connect to the spring backend directly
       * 2. leave empty to route requests through the nuxt backend
       */
      backendURL: "",
      /*
       * Controls whether the frontend should try to obtain an API token,
       * and whether to accept requests at /api (Nuxt middleware)
       */
      authEnabled: true,
      profile: "public",
      sentryDSN: "",
      analytics: {
        posthogKey: "", // needs override in env
        posthogHost: "", // needs override in env
      },
    },
  },
  security: {
    strict: config.production,
    headers: {
      contentSecurityPolicy: {
        "style-src": ["'self'", "https:", "'unsafe-inline'"],
        "img-src": ["'self'", "data:", "'unsafe-inline'"],
        "script-src": ["'strict-dynamic'", "'nonce-{{nonce}}'"],
        "connect-src": config.devMode ? ["'self'", "http:"] : ["'self'"],
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
    "/sitemaps/norms/**": {
      proxy: {
        to: "/v1/sitemaps/norms/**",
        headers: {
          Accept: "application/xml",
        },
      },
    },
    "/sitemaps/caselaw/**": {
      proxy: {
        to: "/v1/sitemaps/caselaw/**",
        headers: {
          Accept: "application/xml",
        },
      },
    },
  },
  vite: {
    optimizeDeps: {
      include: ["cookie", "hyperdx/lucene"],
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
        types: ["unplugin-icons/types/vue"],
      },
    },
  },
  build: {
    transpile: ["hyperdx/lucene"],
  },
  ignore: [
    "**/**/*.{spec,test}.{js,cts,mts,ts,jsx,tsx}",
    "**/**/*.{spec,test}.data.ts",
  ],
  compatibilityDate: "2024-11-01",
});
