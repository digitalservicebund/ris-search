import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/nutzungstests");
  await expect(
    page.getByRole("heading", {
      name: `Helfen Sie uns, Rechtsinformationen leichter zugänglich zu machen`,
    }),
  ).toBeVisible();

  const formbricksLink = page.getByRole("link", {
    name: "Jetzt registrieren",
  });

  await expect(formbricksLink).toBeVisible();
  // Make sure the link points to the DS formbricks instance
  await expect(formbricksLink).toHaveAttribute(
    "href",
    /^https:\/\/surveys.digitalservice.dev.*/,
  );
});
