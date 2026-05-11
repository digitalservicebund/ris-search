import { defineConfig, devices, type Project } from "@playwright/test";

export const environment = {
  baseUrl: "http://localhost:3000/",
  remoteDebuggingPort: 9222,
};

const ISOLATED_SPECS = ["seo.spec.ts", "accessibility.spec.ts"];

const projects: Project[] = [
  {
    name: "chromium",
    use: {
      ...devices["Desktop Chrome"],
      permissions: ["clipboard-read", "clipboard-write"],
    },
    testIgnore: ISOLATED_SPECS,
  },
  {
    name: "firefox",
    use: {
      ...devices["Desktop Firefox"],
    },
    testIgnore: ISOLATED_SPECS,
  },
  {
    name: "webkit",
    use: {
      ...devices["Desktop Safari"],
    },
    testIgnore: ISOLATED_SPECS,
  },
  {
    name: "mobile",
    use: {
      ...devices["Desktop Firefox"],
      viewport: { width: 320, height: 600 },
      touch: true,
    },
    testIgnore: ISOLATED_SPECS,
  },
  {
    name: "seo",
    use: {
      ...devices["Desktop Chrome"],
    },
    testMatch: "seo.spec.ts",
  },
  {
    name: "accessibility-chromium",
    use: {
      ...devices["Desktop Chrome"],
    },
    testMatch: "accessibility.spec.ts",
  },
  {
    name: "accessibility-firefox",
    use: {
      ...devices["Desktop Firefox"],
    },
    testMatch: "accessibility.spec.ts",
  },
  {
    name: "accessibility-mobile",
    use: {
      ...devices["Desktop Firefox"],
      viewport: { width: 320, height: 600 },
      touch: true,
    },
    testMatch: "accessibility.spec.ts",
  },
  {
    name: "smoke-tests",
    use: {
      ...devices["Desktop Chrome"],
      baseURL: "https://ris-portal-staging.dev.tech.digitalservice.dev",
      httpCredentials: {
        username: process.env.STAGING_BASIC_AUTH_USERNAME,
        password: process.env.STAGING_BASIC_AUTH_PASSWORD,
      },
    },
    testDir: "./smoke-tests",
  },
];

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: 1,

  reporter: process.env.CI
    ? [
        ["line", { printSteps: true }],
        ["html", { open: "never" }],
      ]
    : "html",

  use: {
    baseURL: environment.baseUrl,
    screenshot: { mode: "only-on-failure", fullPage: true },
    trace: "retain-on-first-failure",
  },

  projects,
});
