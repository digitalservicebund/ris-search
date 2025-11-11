/// <reference types="vitest" />
/// <reference types="vite" />

import { defineVitestConfig } from "@nuxt/test-utils/config";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import { configDefaults } from "vitest/config";

export default defineVitestConfig({
  plugins: [
    Icons(),
    Components({
      resolvers: [IconsResolver()],
    }),
  ],
  test: {
    environment: "nuxt",
    globals: true,
    exclude: [...configDefaults.exclude, "e2e/**"],
    setupFiles: ["tests/setup.ts"],
    environmentOptions: {
      nuxt: {
        domEnvironment: "jsdom",
      },
    },
    slowTestThreshold: 100,
    coverage: {
      provider: "v8",
      reporter: [
        "text",
        ["json-summary", { file: "coverage-summary.json" }],
        ["json", { file: "coverage.json" }],
        "lcov",
      ],
      reportsDirectory: "./coverage",
      include: ["*/**/*.ts", "*/**/*.vue"],
      exclude: [
        "**/**/*.generated.ts",
        "**/**/generated/**",
        "src/layouts/default.vue",
        "src/utils/config.ts",
        "src/tests/setup.ts",
        "src/**/*.spec.ts",
        "src/pages/docs/index.vue",
      ],
    },
  },
});
