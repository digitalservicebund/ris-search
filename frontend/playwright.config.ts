import { defineConfig, devices, type Project } from "@playwright/test";

export const environment = {
  baseUrl: "http://localhost:3000/",
  remoteDebuggingPort: 9222,
};

const projects: Project[] = [
  {
    name: "chromium",
    use: {
      ...devices["Desktop Chrome"],
      permissions: ["clipboard-read", "clipboard-write"],
    },
  },
  {
    name: "firefox",
    use: {
      ...devices["Desktop Firefox"],
    },
    testIgnore: "seo.spec.ts",
  },
  {
    name: "webkit",
    use: {
      ...devices["Desktop Safari"],
    },
    testIgnore: "seo.spec.ts",
  },
  {
    name: "mobile",
    use: {
      ...devices["Desktop Firefox"],
      viewport: { width: 320, height: 600 },
      touch: true,
    },
    testIgnore: "seo.spec.ts",
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
