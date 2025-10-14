import { test } from "@playwright/test";
import { expect } from "./fixtures";

test("opens the page via URL", async ({ page }) => {
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

test("displays search bar and list", async ({ page }) => {
  await page.goto("/translations");
  const searchInput = page.locator('input[type="search"]');
  await expect(searchInput).toHaveCount(1);

  const links = page.locator('a[href^="translations"]');
  await expect(links).toHaveCount(3);
});

test("filters translations correctly", async ({ page }) => {
  await page.goto("/translations");
  await page.waitForLoadState("networkidle");
  const searchTerm = "Dentist";

  const input = page.getByPlaceholder("Enter search term");
  await input.fill(searchTerm);

  await expect(input).toHaveValue(searchTerm);

  await input.press("Enter");

  const links = page.locator('a[href^="translations"]');
  await expect(links).toHaveCount(1);
});
