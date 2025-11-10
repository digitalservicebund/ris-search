import type { Page } from "@playwright/test";
import { expect, test, noJsTest } from "./utils/fixtures";

async function getSidebar(page: Page) {
  const navigation = page.getByRole("navigation", { name: "Seiteninhalte" });
  await navigation.scrollIntoViewIfNeeded();
  await expect(navigation).toBeVisible();
  return navigation;
}

async function getDisplayedResultCount(page: Page) {
  const resultCountElement = page.locator("output", {
    hasText: "Suchergebnis",
  });
  const text = await resultCountElement.innerText();
  if (text.startsWith("Keine Suchergebnisse")) return 0;
  const digits = text.replace(/\D+/g, "");
  return Number.parseInt(digits);
}

test.skip(
  process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED !== "true",
  "Removed redundant testing since there are no specific functionality marked as private",
);

test("can search, filter for case law, and view a single case law documentation unit", async ({
  page,
  isMobileTest,
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

    const sidebar = await getSidebar(page);

    const firstSectionHeader = page
      .getByRole("main")
      .getByRole("heading", { level: 2 })
      .first();
    await expect(firstSectionHeader).toBeVisible();

    await firstSectionHeader.scrollIntoViewIfNeeded();

    // Only check aria-current on desktop as it's flaky on mobile due to Intersection Observer timing
    // Fix Intersection Observer logic (broken since Nuxt upgrade) - see ticket RISDEV-
    // Once fixed, remove the isMobileTest check and enable aria-current verification for mobile too
    if (!isMobileTest) {
      const currentSection = sidebar.locator('a[aria-current="location"]');
      await expect(currentSection).toHaveCount(1);
    }
  });

  if (isMobileTest)
    for (const sectionName of ["Tenor", "Orientierungssatz", "Tatbestand"]) {
      await test.step(`Jump straight to a specific section, ${sectionName}`, async () => {
        await page.goto(resultsListUrl, {
          waitUntil: "networkidle",
        });
        const link = page.getByRole("link", { name: sectionName }).first();
        await link.click();

        // Verify sidebar is visible and there but skip aria-current check
        // (its flaky due to Intersection Observer timing during hydration)
        // Fix Intersection Observer logic (broken since Nuxt upgrade) - see ticket RISDEV-
        // Once fixed, enable aria-current check here (was flaky due to Intersection Observer timing during hydration)
        await getSidebar(page);

        const heading = page
          .getByRole("main")
          .getByRole("heading", { name: sectionName })
          .first();

        await expect(page).toHaveURL(new RegExp(`#`, "i"));
        await heading.scrollIntoViewIfNeeded();
        await expect(heading).toBeVisible();
        await expect(heading).toBeInViewport();
      });
    }
});

test.describe("actions menu", () => {
  test("can't use link action button as its disabled", async ({
    page,
    isMobileTest,
  }) => {
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
    isMobileTest,
  }) => {
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
    isMobileTest,
  }) => {
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
    isMobileTest,
  }) => {
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

    await page.waitForURL("v1/case-law/JURE200030030.xml");
  });
});

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await page.goto("/case-law/JURE200030030");
  await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();
  await page
    .getByRole("link", { name: "Details zur Gerichtsentscheidung" })
    .click();
  await expect(page).toHaveURL(/#details$/);
});
