import { expect, test } from "../e2e/utils/fixtures";

test("start page is reachable", async ({ page }) => {
  await page.goto("/");

  await expect(
    page.getByRole("heading", { name: "Rechtsinformationen des Bundes " }),
  ).toBeVisible();
});
