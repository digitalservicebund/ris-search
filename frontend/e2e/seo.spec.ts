import { playAudit } from "playwright-lighthouse";
import { environment } from "../playwright.config";
import { navigate, seoTest as test } from "./utils/fixtures";

export const testPages = [
  {
    name: "home",
    url: "/",
  },
  {
    name: "contact",
    url: "/kontakt",
  },
  {
    name: "imprint",
    url: "/impressum",
  },
  {
    name: "data protection",
    url: "/datenschutz",
  },
  {
    name: "accessibility",
    url: "/barrierefreiheit",
  },
  {
    name: "cookie settings",
    url: "/cookie-einstellungen",
  },
  {
    name: "open source",
    url: "/open-source",
  },
  // {
  //   name: "user tests",
  //   url: "/nutzungstests",
  // },
  {
    name: "simple search - all documents",
    url: "/search",
  },
  {
    name: "simple search - legislation",
    url: "/search?documentKind=N",
  },
  {
    name: "simple search - caselaw",
    url: "/search?documentKind=R",
  },
  {
    name: "advanced search",
    url: "/advanced-search",
  },
  {
    name: "norm view",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu",
  },
  {
    name: "norm view - single article",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
  },
  {
    name: "caselaw view",
    url: "/case-law/STRE300770800",
  },
  {
    name: "literature view",
    url: "/literature/XXLU000000001",
  },
  {
    name: "administrative directive view",
    url: "/administrative-directives/KSNR000000001",
  },
];

test.beforeAll(async ({ privateFeaturesEnabled, browserName }) => {
  test.skip(!privateFeaturesEnabled || browserName !== "chromium");
});

test.setTimeout(120000);

for (const testPage of testPages) {
  test(`SEO checks for "${testPage.name}" page`, async ({ page }) => {
    await navigate(page, testPage.url);
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
