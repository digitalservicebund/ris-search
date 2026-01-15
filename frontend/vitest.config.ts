/// <reference types="vitest" />
/// <reference types="vite" />

import { defineVitestConfig } from "@nuxt/test-utils/config";
import { configDefaults } from "vitest/config";

export default defineVitestConfig({
  test: {
    // General test environment
    environment: "nuxt",
    environmentOptions: {
      nuxt: {
        domEnvironment: "jsdom",
      },
    },
    globals: true,
    setupFiles: ["src/tests/setup.ts"],
    exclude: [...configDefaults.exclude, "e2e/**"],

    // Filtering test output
    onConsoleLog(log) {
      const suppressedWarnings = [
        "already provides property with key",
        "<Suspense> is an experimental feature",
      ];

      for (const warning of suppressedWarnings) {
        if (log.includes(warning)) return false;
      }
    },

    // Coverage reporting
    coverage: {
      provider: "v8",
      reportsDirectory: "src/coverage",
      include: ["*/**/*.ts", "*/**/*.vue"],
      reporter: [
        "text",
        ["json-summary", { file: "coverage-summary.json" }],
        ["json", { file: "coverage.json" }],
        "lcov",
      ],

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

    // Misc configuration
    slowTestThreshold: 100,
  },
});
