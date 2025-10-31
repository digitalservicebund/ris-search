import { expect, test } from "./utils/fixtures";

test("opens the page via URL", { tag: ["@RISDEV-8949"] }, async ({ page }) => {
  await page.goto("/translations");
  await expect(
    page.getByRole("heading", {
      name: "English Translations of German Federal Laws and Regulations",
    }),
  ).toBeVisible();
  await expect(page).toHaveTitle(
    "English Translations of German Federal Laws and Regulations | Rechtsinformationen des Bundes",
  );
});

test(
  "displays search bar and list",
  { tag: ["@RISDEV-8949"] },
  async ({ page }) => {
    await page.goto("/translations");
    const searchInput = page.getByRole("searchbox");
    await expect(searchInput).toHaveCount(1);

    const resultsRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = resultsRegion.getByRole("listitem");
    await expect(items).toHaveCount(3);
  },
);

test(
  "filters translations correctly",
  { tag: ["@RISDEV-8949"] },
  async ({ page }) => {
    await page.goto("/translations");
    await page.waitForLoadState("networkidle");
    const searchTerm = "Dentist";

    const input = page.getByRole("searchbox", { name: "search term" });
    await input.fill(searchTerm);

    await expect(input).toHaveValue(searchTerm);

    await input.press("Enter");

    const translationListRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = translationListRegion.getByRole("listitem");
    await expect(items).toHaveCount(1);

    await input.fill("not there");
    await input.press("Enter");
    await expect(translationListRegion.getByRole("listitem")).toHaveCount(0);

    await expect(page.getByText("We didn’t find anything.")).toBeVisible();
  },
);
