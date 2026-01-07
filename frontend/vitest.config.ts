/// <reference types="vitest" />
/// <reference types="vite" />

import { defineVitestConfig } from "@nuxt/test-utils/config";
import { FileSystemIconLoader } from "unplugin-icons/loaders";
import Icons from "unplugin-icons/vite";
import { configDefaults } from "vitest/config";

export default defineVitestConfig({
  plugins: [
    Icons({
      scale: 1.3333, // ~24px at the current default font size of 18px
      customCollections: {
        custom: FileSystemIconLoader("./src/assets/icons"),
      },
    }),
  ],
  test: {
    environment: "nuxt",
    globals: true,
    exclude: [...configDefaults.exclude, "e2e/**"],
    setupFiles: ["src/tests/setup.ts"],
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
