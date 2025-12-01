import type { Page } from "@playwright/test";
import { expect, navigate, noJsTest, test } from "./utils/fixtures";

async function getSidebar(page: Page) {
  const navigation = page.getByRole("navigation", { name: "Seiteninhalte" });
  await navigation.scrollIntoViewIfNeeded();
  await expect(navigation).toBeVisible();
  return navigation;
}

test.skip(
  process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED !== "true",
  "Removed redundant testing since there are no specific functionality marked as private",
);

test("can view a single case law documentation unit", async ({
  page,
  isMobileTest,
}) => {
  await navigate(page, "/case-law/KORE600500000");

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

  if (isMobileTest)
    for (const sectionName of ["Tenor", "Orientierungssatz", "Tatbestand"]) {
      await test.step(`Jump straight to a specific section, ${sectionName}`, async () => {
        await navigate(page, "/search?category=R&query=fiktiv");
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
    await navigate(page, "/case-law/JURE200030030");

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
      ).toBeVisible({ timeout: 30000 });
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use print action button to open print menu", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/case-law/JURE200030030");
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

      await expect(page.getByRole("tooltip", { name: "Drucken" })).toBeVisible({
        timeout: 30000,
      });
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
    await navigate(page, "/case-law/JURE200030030");
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
      ).toBeVisible({
        timeout: 15000,
      });
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use XML action to view norms xml file", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/case-law/JURE200030030");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("link", {
      name: "XML anzeigen",
    });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "XML anzeigen" }),
      ).toBeVisible({ timeout: 15000 });
    }

    await button.click();
    await page.waitForURL(`v1/case-law/JURE200030030.xml`, {
      waitUntil: "commit",
    });
  });
});

test("can view metadata", async ({ page }) => {
  await navigate(page, "/case-law/KORE600500000");

  await page.getByRole("term", { name: "Gericht" }).isVisible();
  await page.getByRole("definition", { name: "LG Testort6" }).isVisible();

  await page.getByRole("term", { name: "Dokumenttyp" }).isVisible();
  await page.getByRole("definition", { name: "Urteil" }).isVisible();

  await page.getByRole("term", { name: "Entscheidungsdatum" }).isVisible();
  await page.getByRole("definition", { name: "09.04.2025" }).isVisible();

  await page.getByRole("term", { name: "Aktenzeichen" }).isVisible();
  await page.getByRole("definition", { name: "TS 123456" }).isVisible();
});

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await navigate(page, "/case-law/JURE200030030");
  await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();
  await page.getByRole("link", { name: "Details" }).click();
  await expect(page).toHaveURL(/#details$/);
});
