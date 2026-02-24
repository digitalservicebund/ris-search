import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/feedback");
  await expect(
    page.getByRole("heading", { name: "Geben Sie uns Feedback", exact: true }),
  ).toBeVisible();
});
