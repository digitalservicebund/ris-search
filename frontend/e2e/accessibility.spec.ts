import path from "node:path";
import { AxeBuilder } from "@axe-core/playwright";
import { expect, test } from "@playwright/test";
import { createHtmlReport } from "axe-html-reporter";
import { navigate } from "./utils/fixtures";

const testPages = [
  {
    name: "not found",
    url: "/404",
  },
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
  {
    name: "introduction",
    url: "/einfuehrung",
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
    tabs: [
      { name: "Details" },
      {
        name: "Fassungen",
        heading: /^Fassungen( sind noch nicht verfügbar)?$/,
      },
    ],
  },
  {
    name: "norm view - single article",
    url: "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
  },
  {
    name: "caselaw view",
    url: "/case-law/STRE300770800",
    tabs: [{ name: "Details" }],
  },
  {
    name: "literature view",
    url: "/literature/XXLU000000001",
    tabs: [{ name: "Details" }],
  },
  {
    name: "administrative directive view",
    url: "/administrative-directives/KSNR000000001",
  },
  {
    name: "translations",
    url: "/translations",
  },
  {
    name: "translated norm",
    url: "/translations/CBG",
    tabs: [{ name: "Details" }],
  },
];

testPages.forEach(({ name, url, tabs }) => {
  test(`"${name}" page should not have accessibility issues`, async ({
    page,
  }) => {
    await navigate(page, url);
    const tabsAnalysisResults = [];
    tabsAnalysisResults.push(
      await new AxeBuilder({ page }).exclude("nuxt-devtools-frame").analyze(),
    );
    if (tabs) {
      for (const tab of tabs) {
        await page.getByRole("tab", { name: tab.name }).click();
        await expect(
          page.getByRole("heading", {
            name: tab.heading ?? tab.name,
            exact: true,
          }),
        ).toBeVisible();
        tabsAnalysisResults.push(
          await new AxeBuilder({ page })
            .exclude("nuxt-devtools-frame")
            .analyze(),
        );
      }
    }
    tabsAnalysisResults.forEach((result, index) => {
      const nameSuffix = index === 0 ? "" : ` - Tab ${index + 1}`;
      createHtmlReport({
        results: result,
        options: {
          outputDir: path.join("e2e", "test-results", "accessibility-results"),
          reportFileName: `${name} Page ${nameSuffix}.html`,
        },
      });
    });

    expect(tabsAnalysisResults.flatMap((r) => r.violations)).toEqual([]);
  });
});
