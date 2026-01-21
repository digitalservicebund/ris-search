import path from "node:path";
import { AxeBuilder } from "@axe-core/playwright";
import { expect, test } from "@playwright/test";
import { createHtmlReport } from "axe-html-reporter";
import { navigate } from "./utils/fixtures";

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
  {
    name: "Literature View Page",
    url: "/literature/XXLU000000001",
    tabs: ["Details"],
  },
  {
    name: "Translations List View",
    url: "/translations",
  },
  {
    name: "Translation View Page",
    url: "/translations/FG",
    tabs: ["Details"],
  },
  {
    name: "Introduction Page",
    url: "/einfuehrung",
  },
  {
    name: "Administrative Directive View Page",
    url: "/administrative-directives/KSNR000000001",
  },
];

test.describe("General Pages Accessibility Tests", () => {
  testPages.forEach(({ name, url, tabs }) => {
    test(`${name} should not have accessibility issues`, async ({ page }) => {
      await navigate(page, url);
      const tabsAnalysisResults = [];
      let currentTab = 0;
      tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
        .exclude("nuxt-devtools-frame")
        .analyze();
      if (tabs) {
        for (const tab of tabs) {
          await page.getByRole("tab", { name: tab }).click();
          await page
            .getByRole("heading", { name: tab, exact: true })
            .isVisible();
          tabsAnalysisResults[currentTab] = await new AxeBuilder({ page })
            .exclude("nuxt-devtools-frame")
            .analyze();
          currentTab++;
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
            reportFileName: `${name} Page ${nameSuffix}.html`,
          },
        });
      });

      expect(tabsAnalysisResults.flatMap((r) => r.violations)).toEqual([]);
    });
  });
});
