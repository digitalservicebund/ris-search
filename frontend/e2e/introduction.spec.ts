import { expect, test } from "./utils/fixtures";

test("opens Einführende Informationen via URL", async ({ page }) => {
  await page.goto("/einfuehrung");
  await expect(
    page.getByRole("heading", { name: "Einführende Informationen" }),
  ).toBeVisible();
});
