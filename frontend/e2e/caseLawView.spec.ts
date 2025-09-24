import type { Page } from "@playwright/test";
import { test } from "@playwright/test";
import { expect } from "./fixtures";
import { getDisplayedResultCount } from "./utils";

function getSidebar(page: Page) {
  const sidebarLabel = page.getByText("Seiteninhalte");
  return page.getByRole("navigation").filter({ has: sidebarLabel });
}

test("can search, filter for case law, and view a single case law documentation unit", async ({
  page,
}) => {
  // authentication should happen in the setup flow in auth.setup.ts
  await test.step("Basic search", async () => {
    await page.goto("/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill("Fiktiv");
    await page.getByLabel("Suchen").click();
    await page.waitForLoadState("networkidle");

    expect(await getDisplayedResultCount(page)).toBe(15);
  });

  let resultsListUrl: string;

  await test.step("Filter for Gerichtsentscheidungen", async () => {
    await page.getByRole("button", { name: "Gerichtsentscheidungen" }).click();
    await expect
      .poll(() => getDisplayedResultCount(page), {
        message: "the count should decrease",
      })
      .toBe(10);

    resultsListUrl = page.url();
  });

  await test.step("View a caselaw documentation unit", async () => {
    await page.getByRole("link", { name: "Testheader für Urteil 6." }).click();
    await expect(
      page.getByRole("heading", { name: "Testheader für Urteil 6." }).first(),
    ).toBeVisible();

    const sidebar = getSidebar(page);
    await expect(sidebar).toBeVisible();

    const firstSectionHeader = page
      .getByRole("main")
      .getByRole("heading", { level: 2 })
      .first();
    await expect(firstSectionHeader).toBeVisible();

    await firstSectionHeader.scrollIntoViewIfNeeded();

    const currentSection = sidebar.locator('a[aria-current="section"]');
    await expect(currentSection).toHaveCount(1);
  });

  for (const sectionName of ["Tenor", "Orientierungssatz", "Tatbestand"]) {
    await test.step(`Jump straight to a specific section, ${sectionName}`, async () => {
      await page.goto(resultsListUrl, {
        waitUntil: "networkidle",
      });
      const link = page.getByRole("link", { name: sectionName }).first();
      await link.click();

      const sidebar = getSidebar(page);
      await expect(sidebar).toBeVisible();

      const expectedSidebarItem = sidebar.getByRole("link", {
        name: sectionName,
      });
      // ensure the previous section is out of sight and gets deselected
      await page.mouse.wheel(0, 10);
      await expect(expectedSidebarItem).toHaveAttribute(
        "aria-current",
        "section",
      );

      const heading = page
        .getByRole("main")
        .getByRole("heading", { name: sectionName });
      await expect(heading).toBeInViewport();
    });
  }
});

test.describe("actions menu", () => {
  test("can't use link action button as its disabled", async ({
    page,
  }, workerInfo) => {
    const isMobileTest = workerInfo.project.name === "mobile";
    await page.goto("/case-law/JURE200030030", { waitUntil: "networkidle" });

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    const button = isMobileTest
      ? page.getByText("Link kopieren")
      : page.getByRole("button", {
          name: "Link kopieren",
        });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Link kopieren" }),
      ).toBeVisible();
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use print action button to open print menu", async ({
    page,
  }, workerInfo) => {
    const isMobileTest = workerInfo.project.name === "mobile";
    await page.goto("/case-law/JURE200030030", { waitUntil: "networkidle" });
    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    const button = isMobileTest
      ? page.getByRole("menuitem", { name: "Drucken" })
      : page.getByRole("button", {
          name: "Drucken",
        });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Drucken" }),
      ).toBeVisible();
    }

    await test.step("can open print menu", async () => {
      await page.evaluate(
        "(() => {window.waitForPrintDialog = new Promise(f => window.print = f);})()",
      );
      await button.click();

      await page.waitForFunction("window.waitForPrintDialog");
    });
  });

  test("can't use PDF action as it is disabled", async ({
    page,
  }, workerInfo) => {
    const isMobileTest = workerInfo.project.name === "mobile";
    await page.goto("/case-law/JURE200030030", { waitUntil: "networkidle" });
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = isMobileTest
      ? page.getByText("Als PDF speichern")
      : page.getByRole("button", {
          name: "Als PDF speichern",
        });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Als PDF speichern" }),
      ).toBeVisible();
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use XML action to view norms xml file", async ({
    page,
  }, workerInfo) => {
    const isMobileTest = workerInfo.project.name === "mobile";
    await page.goto("/case-law/JURE200030030", { waitUntil: "networkidle" });
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("link", {
      name: "XML anzeigen",
    });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "XML anzeigen" }),
      ).toBeVisible();
    }

    await button.click();

    await page.waitForURL("api/v1/case-law/JURE200030030.xml");
  });
});
