import { test } from "@playwright/test";
import { expect } from "./fixtures";
import { getDisplayedResultCount } from "./utils";

test("can enter a query on the start page and go back", async ({ page }) => {
  const searchTerm = "Fiktiv";

  await page.goto("/");
  await page.getByPlaceholder("Suchbegriff eingeben").fill(searchTerm);

  const searchButton = page.getByRole("button", { name: "Suchen" });
  await expect(searchButton).not.toBeDisabled();
  await searchButton.click();
  expect(await getDisplayedResultCount(page)).toBeGreaterThan(0);
  await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
    searchTerm,
  );

  await test.step("go back to home page by clicking the logo", async () => {
    await page.getByLabel("Zur Startseite").click();
    await expect(
      page.getByPlaceholder("Suchbegriff eingeben"),
      "should be reset",
    ).not.toHaveValue(searchTerm);
  });
});

test("can use pagination to switch pages", async ({ page }) => {
  await page.goto("/search");
  expect(await getDisplayedResultCount(page)).toBe(17);

  const searchResults = page.getByTestId("searchResult");
  expect(await searchResults.count()).toBe(10);

  await page.getByLabel("nächste Ergebnisse").click();
  await page.waitForURL("/search?pageNumber=1");
  expect(await getDisplayedResultCount(page)).toBe(17);
  expect(await searchResults.count()).toBe(7);

  await page.getByLabel("vorherige Ergebnisse").click();
  await page.waitForURL("/search");

  await expect
    .poll(() => searchResults.count(), {
      message: "the count should go back to 10",
    })
    .toBe(10);
});

test("the main category filters are mutually exclusive", async ({ page }) => {
  let totalCount = 0;
  let legislationCount = 0;
  let caseLawCount = 0;
  await test.step("Basic search", async () => {
    await page.goto("/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill("Fiktiv");
    await page.getByLabel("Suchen").click();

    totalCount = await getDisplayedResultCount(page);
    expect(totalCount).toBeGreaterThan(0);
  });

  await test.step("Filter for norms", async () => {
    await page.getByRole("button", { name: "Gesetze & Verordnungen" }).click();
    await expect
      .poll(() => getDisplayedResultCount(page), {
        message: "the count should decrease",
      })
      .toBeLessThan(totalCount);
    legislationCount = await getDisplayedResultCount(page);
    expect(legislationCount).toBeGreaterThan(0);
    await page.getByRole("button", { name: "Alle Dokumentarten" }).click();
  });

  await test.step("Filter for case law", async () => {
    await page.getByRole("button", { name: "Gerichtsentscheidungen" }).click();

    await expect
      .poll(() => getDisplayedResultCount(page), {
        message: "the count should decrease",
      })
      .toBeLessThan(totalCount);
    caseLawCount = await getDisplayedResultCount(page);
  });

  expect(caseLawCount + legislationCount, {
    message: "The sum should equal the previous total",
  }).toEqual(totalCount);
});

test("can select courts for case law", async ({ page }) => {
  const courtName = "LG Hamburg";

  await page.goto("/search?query=Fiktiv&category=R", {
    waitUntil: "networkidle",
  });

  const panel = page.getByLabel("Optionsliste");
  const suggestions = panel.getByRole("option");
  const firstSuggestion = suggestions.first();

  await expect
    .poll(
      async () => {
        const openButton = page
          .getByRole("group", { name: "Filter" })
          .locator("button");
        await openButton.click();
        return firstSuggestion.isVisible();
      },
      { message: "Wait for the autocomplete to be interactive" },
    )
    .toBeTruthy();

  await page.getByLabel("Gericht", { exact: true }).pressSequentially("LG ");

  await expect(firstSuggestion).toBeVisible();

  await expect.poll(() => suggestions.count()).toBe(4);

  await firstSuggestion.click();
  const searchResults = page.getByTestId("searchResult");
  for (let i = 0; i < (await searchResults.count()); i++) {
    await expect(searchResults.nth(i)).toContainText(courtName);
  }

  await test.step("updates the URL accordingly", async () => {
    const expectedUrlParameter =
      "court=" + encodeURIComponent(courtName).replace(/%20/g, "+");
    await expect.poll(() => page.url()).toContain(expectedUrlParameter);
  });
});
