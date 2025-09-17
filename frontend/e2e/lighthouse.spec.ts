import path from "node:path";
import { test } from "@playwright/test";
import { playAudit } from "playwright-lighthouse";
import { environment } from "~~/playwright.config";

const REPORT_DIR = path.join(process.cwd(), "test-results", "lighthouse-seo");
type Mode = "desktop" | "mobile";

export const testPages = [
  {
    name: "Not Found Page",
    url: "/404",
  },
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
    name: "Article View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/regelungstext-1/art-z1",
  },
  {
    name: "Caselaw View Page",
    url: "/case-law/STRE300770800",
  },
  {
    name: "Norm View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/regelungstext-1",
  },
];

function makeSeoConfig(mode: Mode) {
  const isMobile = mode === "mobile";
  const screenEmulation = isMobile
    ? {
        mobile: true,
        width: 360,
        height: 740,
        deviceScaleRatio: 2.625,
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
      formFactor: mode,
      screenEmulation,
      throttling,
    },
  };
}

const VIEWPORTS: Record<Mode, { width: number; height: number }> = {
  mobile: { width: 360, height: 740 },
  desktop: { width: 1350, height: 940 },
};

const MODES = [
  { mode: "desktop" as const, pageConfig: makeSeoConfig("desktop") },
  { mode: "mobile" as const, pageConfig: makeSeoConfig("mobile") },
];

test.describe.skip("Lighthouse SEO (desktop + mobile)", () => {
  test.skip(({ browserName }) => browserName !== "chromium");
  for (const { mode, pageConfig } of MODES) {
    for (const testPage of testPages) {
      test(`${mode} SEO checks for page ${testPage.name}`, async ({ page }) => {
        await page.goto(testPage.url);
        await page.setViewportSize(VIEWPORTS[mode]);
        const reportName = `${mode}-${testPage.name}`;
        await playAudit({
          page,
          port: environment.remoteDebuggingPort,
          thresholds: {
            seo: 90,
          },
          config: pageConfig,
          reports: {
            formats: { html: true },
            name: reportName,
            directory: REPORT_DIR,
          },
        });
      });
    }
  }
});
