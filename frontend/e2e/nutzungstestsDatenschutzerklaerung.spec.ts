import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/nutzungstests-datenschutzerklaerung");
  await expect(
    page.getByRole("heading", {
      name: `Datenschutzerklärung für die Registrierung für Nutzungsstudien für das Projekt „Testphase Rechtsinformationsportal"`,
    }),
  ).toBeVisible();

  const formbricksLink = page.getByRole("link", {
    name: /^https:\/\/surveys.digitalservice.dev.*/,
  });

  await expect(formbricksLink).toBeVisible();
  // Make sure the link points to the DS formbricks instance
  await expect(formbricksLink).toHaveAttribute(
    "href",
    /^https:\/\/surveys.digitalservice.dev.*/,
  );
});
