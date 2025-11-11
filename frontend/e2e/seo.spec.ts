import { playAudit } from "playwright-lighthouse";
import { environment } from "../playwright.config";
import { seoTest as test } from "./utils/fixtures";

export const testPages = [
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
  // {
  //   name: "User Tests Page",
  //   url: "/nutzungstests",
  // },
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

test.beforeAll(async ({ privateFeaturesEnabled, browserName }) => {
  test.skip(!privateFeaturesEnabled || browserName !== "chromium");
});

test.describe("SEO testing for desktop and mobile using lighthouse", () => {
  for (const testPage of testPages) {
    test(`SEO checks for page ${testPage.name}`, async ({ page }) => {
      await page.goto(testPage.url, { waitUntil: "networkidle" });
      await playAudit({
        page,
        port: environment.remoteDebuggingPort,
        thresholds: { seo: 90 },
        config: {
          extends: "lighthouse:default",
          settings: {
            onlyCategories: ["seo"],
            disableStorageReset: true,
          },
        },
      });
    });
  }
});
