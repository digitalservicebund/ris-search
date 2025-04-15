import type { Page } from "@playwright/test";
import { test } from "@playwright/test";
import { expect } from "./fixtures";
import { getResultCount } from "./utils";

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

    expect(await getResultCount(page)).toBe(15);
  });

  let resultsListUrl: string;

  await test.step("Filter for Gerichtsentscheidungen", async () => {
    await page.getByRole("button", { name: "Gerichtsentscheidungen" }).click();
    await expect
      .poll(() => getResultCount(page), {
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
      .getByRole("heading")
      .first();
    await expect(firstSectionHeader).toBeVisible();

    await firstSectionHeader.scrollIntoViewIfNeeded();

    const currentSection = sidebar.locator('a[aria-current="section"]');
    await expect(currentSection).toHaveCount(1);
  });

  for (const sectionName of ["Tenor", "Orientierungssatz", "Tatbestand"]) {
    await test.step(`Jump straight to a specific section, ${sectionName}`, async () => {
      await page.goto(resultsListUrl);
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
        .getByText(sectionName, { exact: true });
      await expect(heading).toBeInViewport();
    });
  }
});
