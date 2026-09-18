import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/ueber");
  await expect(
    page.getByRole("heading", {
      name: "Rechtsinformationen zentral an einem Ort",
    }),
  ).toBeVisible();
});
