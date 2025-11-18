import { expect, navigate, test } from "./utils/fixtures";

test("opens Einführende Informationen via URL", async ({ page }) => {
  await navigate(page, "/einfuehrung");
  await expect(
    page.getByRole("heading", { name: "Einführende Informationen" }),
  ).toBeVisible();
});
