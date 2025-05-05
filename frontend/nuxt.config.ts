import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import IconsResolver from "unplugin-icons/resolver";
import { defineNuxtConfig } from "nuxt/config";
import { PrimeVueResolver } from "unplugin-vue-components/resolvers";
import { isStringEmpty, getStringOrDefault } from "./src/utils/textFormatting";
import tailwindcss from "@tailwindcss/vite";

const config = {
  devMode: process.env.NODE_ENV == "development",
  production: process.env.NODE_ENV == "production",
};

const sentryEnabled = !isStringEmpty(process.env.NUXT_PUBLIC_SENTRY_DSN);

const secureCookie = !config.devMode;

const backendBaseUrl = process.env.NUXT_RIS_BACKEND_URL;

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
      ],
      htmlAttrs: {
        lang: "de",
      },
      charset: "utf-8",
      viewport: "width=device-width, initial-scale=1",
    },
  },
  srcDir: "src/",
  css: ["@/assets/main.css"],
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
  ],
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
    /* Backend URL to talk to the backend, used by the /api and /proxy routes */
    risBackendUrl: "http://localhost:8090",
    /* Backend URL to use when performing authenticated server-side rendering
     (SSR), which might go to the Nuxt middleware at /api, or against the
      backend directly. */
    ssrBackendUrl: "/api",
    session: {
      cookie: {
        secure: secureCookie, // workaround needed for Safari on localhost
      },
      password: "", // needs override in env
    },
    public: {
      /*
       * For backendUrl, either
       * 1. use /api to use the Nuxt middleware defined in server/api/[...].ts
       *    using sealed session cookies, or
       * 2. use http://localhost:8090 to connect to the backend directly
       * 3. in public production, leave empty to route requests directly
       *    to e.g. /v1
       */
      backendURL: "/api",
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
        feedbackSurveyId: "", // needs override in env
      },
    },
  },
  security: {
    strict: true,
    headers: {
      contentSecurityPolicy: {
        "style-src": ["'self'", "https:", "'unsafe-inline'"],
        "img-src": ["'self'", "data:"],
        "script-src": ["'strict-dynamic'", "'nonce-{{nonce}}'"],
      },
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
  nitro: {
    rollupConfig: {
      output: {
        sourcemap: "hidden",
      },
    },
  },
  routeRules: {
    "/api/docs/**": {
      proxy: `${backendBaseUrl}/swagger-ui/**`,
    },
    "v3/api-docs/**": {
      proxy: `${backendBaseUrl}/**`,
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
  components: [
    {
      path: "@/components",
    },
    // PrimeVue components are registered implicitly
  ],
  build: {
    transpile: ["hyperdx/lucene"],
  },
  ignore: ["**/**/*.{spec,test}.{js,cts,mts,ts,jsx,tsx}"],
  compatibilityDate: "2024-11-01",
});
