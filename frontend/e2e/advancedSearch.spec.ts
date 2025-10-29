import { expect, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await page.goto("/advanced-search");

  await expect(
    page.getByRole("heading", { name: "Erweiterte Suche" }),
  ).toBeVisible();
  await expect(page).toHaveTitle(
    "Erweiterte Suche | Rechtsinformationen des Bundes",
  );
});
