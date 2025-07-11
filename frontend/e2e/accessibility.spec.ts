import path from "node:path";
import AxeBuilder from "@axe-core/playwright";
import { expect, test } from "@playwright/test";
import { createHtmlReport } from "axe-html-reporter";

const testPages = [
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
    url: "/norms/eli/bund/bgbl-1/1972/s2459/1999-04-20/4/deu/regelungstext-1/hauptteil-1_abschnitt-1_art-1",
  },
];
test.describe.skip("General Pages Accessibility Tests", () => {
  testPages.forEach(({ name, url }) => {
    test(`${name} should not have accessibility issues`, async ({ page }) => {
      await page.goto(url);
      await page.waitForLoadState("networkidle");
      const accessibilityScanResults = await new AxeBuilder({ page }).analyze();
      createHtmlReport({
        results: accessibilityScanResults,
        options: {
          outputDir: path.join("e2e", "test-results", "accessibility-results"),
          reportFileName: `${name}.html`,
        },
      });
      expect(accessibilityScanResults.violations).toEqual([]);
    });
  });
});
test.describe.skip("View Page Accessibility Tests", () => {
  test(`Norms page should not have accessibility issues`, async ({ page }) => {
    await page.goto(
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/regelungstext-1",
    );
    await page.waitForLoadState("networkidle");
    const tabsAnalysisResults = [];
    tabsAnalysisResults[0] = await new AxeBuilder({ page }).analyze();
    await page.getByRole("tab", { name: "Details" }).click();
    await page.waitForLoadState("networkidle");
    tabsAnalysisResults[1] = await new AxeBuilder({ page }).analyze();
    await page.getByRole("tab", { name: "Fassungen" }).click();
    await page.waitForLoadState("networkidle");
    tabsAnalysisResults[2] = await new AxeBuilder({ page }).analyze();
    tabsAnalysisResults.forEach((result, index) => {
      createHtmlReport({
        results: result,
        options: {
          outputDir: path.join("e2e", "test-results", "accessibility-results"),
          reportFileName: `Norms View Page - Tab ${index + 1}.html`,
        },
      });
    });
    expect(tabsAnalysisResults.map((result) => result.violations)).toEqual([
      [],
      [],
      [],
    ]);
  });
  test(`Caselaw page should not have accessibility issues`, async ({
    page,
  }) => {
    await page.goto("/case-law/STRE300770800");
    await page.waitForLoadState("networkidle");
    const tabsAnalysisResults = [];
    tabsAnalysisResults[0] = await new AxeBuilder({ page }).analyze();
    await page.getByRole("tab", { name: "Details" }).click();
    await page.waitForLoadState("networkidle");
    tabsAnalysisResults[1] = await new AxeBuilder({ page }).analyze();
    tabsAnalysisResults.forEach((result, index) => {
      createHtmlReport({
        results: result,
        options: {
          outputDir: path.join("e2e", "test-results", "accessibility-results"),
          reportFileName: `Caselaw View Page - Tab ${index + 1}.html`,
        },
      });
    });
    expect(tabsAnalysisResults.map((result) => result.violations)).toEqual([
      [],
      [],
    ]);
  });
});
