import path from "node:path";
import os from "os";
import { test } from "@playwright/test";
import { chromium, type BrowserContext, type Page } from "playwright";
import { playAudit } from "playwright-lighthouse";
import { environment } from "~~/playwright.config";
import { loginUser } from "./auth.utils";

type Device = "desktop" | "mobile";
const REPORT_DIR = path.join(process.cwd(), "test-results", "lighthouse-seo");

export const testPages = [
  // TODO: Add SEO Meta Tags to the 404 page
  // {
  //   name: "Not Found Page",
  //   url: "/404",
  // }
  {
    name: "Home Page",
    url: "/",
  },
  {
    name: "All Search Results Page",
    url: "/search",
  },
  {
    name: "Contact Page",
    url: "/kontakt",
  },
  {
    name: "Imprint Page",
    url: "/impressum",
  },
  {
    name: "Data Protection Page",
    url: "/datenschutz",
  },
  {
    name: "Accessibility Page",
    url: "/barrierefreiheit",
  },
  {
    name: "Cookie Settings Page",
    url: "/cookie-einstellungen",
  },
  {
    name: "Open Source Page",
    url: "/opensource",
  },
  {
    name: "User Tests Page",
    url: "/nutzungstests",
  },
  {
    name: "Norms Search Page",
    url: "/search?category=N",
  },
  {
    name: "Caselaw Search Page",
    url: "/search?category=R",
  },
  {
    name: "Advanced Search Page",
    url: "/advanced-search",
  },
  {
    name: "Norm View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu",
  },
  {
    name: "Article View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
  },
  {
    name: "Caselaw View Page",
    url: "/case-law/STRE300770800",
  },
];

function getDeviceConfig(device: Device) {
  const isMobile = device === "mobile";
  const screenEmulation = isMobile
    ? {
        mobile: true,
        width: 320,
        height: 600,
        deviceScaleRatio: 2.5,
        disabled: false,
      }
    : {
        mobile: false,
        width: 1350,
        height: 940,
        deviceScaleRatio: 1,
        disabled: false,
      };
  const throttling = isMobile
    ? {
        rttMs: 150,
        throughputKbps: 1638.4,
        cpuSlowdownMultiplier: 4,
        requestLatencyMs: 0,
        downloadThroughputKbps: 0,
        uploadThroughputKbps: 0,
      }
    : {
        rttMs: 40,
        throughputKbps: 10240,
        cpuSlowdownMultiplier: 1,
        requestLatencyMs: 0,
        downloadThroughputKbps: 0,
        uploadThroughputKbps: 0,
      };

  return {
    extends: "lighthouse:default",
    settings: {
      onlyCategories: ["seo"],
      formFactor: device,
      screenEmulation,
      throttling,
      disableStorageReset: true,
    },
  };
}

const testCases = [
  { device: "desktop" as const, config: getDeviceConfig("desktop") },
  { device: "mobile" as const, config: getDeviceConfig("mobile") },
];

test.describe("SEO testing for desktop and mobile using lighthouse", () => {
  let context: BrowserContext;
  let authenticatedPage: Page;

  test.beforeAll(async () => {
    // Authenticate once for all tests
    context = await chromium.launchPersistentContext(os.tmpdir(), {
      args: [`--remote-debugging-port=${environment.remoteDebuggingPort}`],
    });
    authenticatedPage = await context.newPage();
    await authenticatedPage.goto(environment.baseUrl);
    await loginUser(authenticatedPage);
  });

  test.afterAll(async () => {
    await context.close();
  });

  for (const { device, config } of testCases) {
    for (const testPage of testPages) {
      test(`${device} SEO checks for page ${testPage.name}`, async () => {
        await authenticatedPage.goto(testPage.url);
        const { width, height } = config.settings.screenEmulation;
        await authenticatedPage.setViewportSize({ width, height });
        
        await playAudit({
          page: authenticatedPage,
          port: environment.remoteDebuggingPort,
          thresholds: { seo: 90 },
          config,
          reports: {
            formats: { html: true },
            name: `${device}-${testPage.name}`,
            directory: REPORT_DIR,
          },
        });
      });
    }
  }
});
