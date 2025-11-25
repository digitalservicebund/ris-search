/// <reference types="vitest" />
/// <reference types="vite" />

import { defineVitestConfig } from "@nuxt/test-utils/config";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import { PrimeVueResolver } from "unplugin-vue-components/resolvers";
import Components from "unplugin-vue-components/vite";
import { configDefaults } from "vitest/config";

export default defineVitestConfig({
  plugins: [
    Icons(),
    Components({
      resolvers: [IconsResolver(), PrimeVueResolver()],
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

      // This needs to be kept in sync with exclusions in sonar-project.properties
      // to ensure coverage is reported accurately in SonarCloud
      exclude: [
        "e2e/**/*",
        "src/**/*.spec.ts",
        "src/pages/**/*",
        "src/plugins/**/*",
        "src/server/plugins/**/*",
        "src/tests/**/*",
      ],
    },
  },
});
