import { defineConfig, devices, type Project } from "@playwright/test";

export const environment = {
  baseUrl: process.env.RIS_BASE_URL || "http://localhost:3000/",
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
];

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: "./e2e",
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: 1,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: process.env.CI
    ? [
        ["line", { printSteps: true }],
        ["html", { open: "never" }],
      ]
    : "html",
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Base URL to use in actions like `await page.goto('/')`. */
    baseURL: environment.baseUrl,
    screenshot: { mode: "only-on-failure", fullPage: true },
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "retain-on-first-failure",
  },

  /* Configure projects for major browsers */
  projects,

  /* Run your local dev server before starting the tests */
  // webServer: {
  //   command: 'npm run start',
  //   url: 'http://127.0.0.1:3000',
  //   reuseExistingServer: !process.env.CI,
  // },
});
