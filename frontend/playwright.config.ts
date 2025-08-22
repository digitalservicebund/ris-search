import { defineConfig, devices, type Project } from "@playwright/test";
export const authFile = "playwright/.auth/user.json";

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */

const extraHTTPHeaders: Record<string, string> = {};
if (process.env.E2E_RIS_BASIC_AUTH) {
  extraHTTPHeaders["Authorization"] = `Basic ${process.env.E2E_RIS_BASIC_AUTH}`;
}

export const environment = {
  user: {
    displayName: process.env.E2E_KEYCLOAK_USER_DISPLAY_NAME || "Jane Doe",
    credentials: {
      username: process.env.E2E_KEYCLOAK_USERNAME || "jane.doe",
      password: process.env.E2E_KEYCLOAK_PASSWORD || "test",
    },
  },
  baseUrl: process.env.RIS_BASE_URL || "http://localhost:3000/",
};

const browserConfigurations: Project[] = [
  {
    name: "chromium",
    use: {
      ...devices["Desktop Chrome"],
      storageState: authFile,
    },
    dependencies: ["setup"],
  },
  {
    name: "firefox",
    use: {
      ...devices["Desktop Firefox"],
      storageState: authFile,
    },
    dependencies: ["setup"],
  },
  {
    name: "webkit",
    use: {
      ...devices["Desktop Safari"],
      storageState: authFile,
    },
    dependencies: ["setup"],
  },
];

const projects: Project[] = [
  {
    name: "setup",
    testMatch: /.*\.setup\.ts/,
  },
  ...browserConfigurations,
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
  retries: process.env.CI ? 2 : 1,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 1 : undefined,
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

    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "on-first-retry",
    extraHTTPHeaders,
  },

  /* Configure projects for major browsers */
  projects,

  /* Run your local dev server before starting the tests */
  // webServer: {
  //   command: 'npm run start',
  //   url: 'http://127.0.0.1:3000',
  //   reuseExistingServer: !process.env.CI,
  // },
  timeout: 10_000,
});
