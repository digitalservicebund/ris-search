import path from "node:path";
import AxeBuilder from "@axe-core/playwright";
import { expect, test } from "@playwright/test";
import { createHtmlReport } from "axe-html-reporter";

const testPages = [
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
    name: "Advanced Search Page",
    url: "/advanced-search",
  },
  {
    name: "Norm View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu",
    tabs: ["Details", "Fassungen"],
  },
  {
    name: "Article View Page",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
  },
  {
    name: "Caselaw View Page",
    url: "/case-law/STRE300770800",
    tabs: ["Details"],
  },
];
test.describe("General Pages Accessibility Tests", () => {
  testPages.forEach(({ name, url, tabs }) => {
    // Desktop test
    test(`${name} should not have accessibility issues (desktop)`, async ({
      page,
    }) => {
      await page.setViewportSize({ width: 1280, height: 800 });
      await page.goto(url);
      await page.waitForLoadState("networkidle");

      const tabsAnalysisResults = [];
      let currentTab = 0;
      tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
        .exclude("nuxt-devtools-frame")
        .analyze();

      if (tabs) {
        for (const tab of tabs) {
          currentTab++;
          await page.getByRole("tab", { name: tab }).click();
          await page.waitForLoadState("networkidle");
          tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
            .exclude("nuxt-devtools-frame")
            .analyze();
        }
      }

      tabsAnalysisResults.forEach((result, index) => {
        const nameSuffix = index === 0 ? "" : ` - Tab ${index + 1}`;
        createHtmlReport({
          results: result,
          options: {
            outputDir: path.join(
              "e2e",
              "test-results",
              "accessibility-results",
            ),
            reportFileName: `${name} Page (desktop)${nameSuffix}.html`,
          },
        });
      });

      expect(tabsAnalysisResults.flatMap((r) => r.violations)).toEqual([]);
    });

    // Mobile test
    test(`${name} should not have accessibility issues (mobile)`, async ({
      page,
    }) => {
      await page.setViewportSize({ width: 320, height: 600 });
      await page.goto(url);
      await page.waitForLoadState("networkidle");
      const tabsAnalysisResults = [];
      let currentTab = 0;
      tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
        .exclude("nuxt-devtools-frame")
        .analyze();
      if (tabs) {
        for (const tab of tabs) {
          currentTab++;
          await page.getByRole("tab", { name: tab }).click();
          await page.waitForLoadState("networkidle");
          tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
            .exclude("nuxt-devtools-frame")
            .analyze();
        }
      }
      tabsAnalysisResults.forEach((result, index) => {
        const nameSuffix = index === 0 ? "" : ` - Tab ${index + 1}`;
        createHtmlReport({
          results: result,
          options: {
            outputDir: path.join(
              "e2e",
              "test-results",
              "accessibility-results",
            ),
            reportFileName: `${name} Page (mobile)${nameSuffix}.html`,
          },
        });
      });

      expect(tabsAnalysisResults.flatMap((r) => r.violations)).toEqual([]);
    });
  });
});
