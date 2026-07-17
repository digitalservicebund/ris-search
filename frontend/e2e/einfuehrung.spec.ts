import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/einfuehrung");
  await expect(
    page.getByRole("heading", { name: "Einführende Informationen" }),
  ).toBeVisible();
});
