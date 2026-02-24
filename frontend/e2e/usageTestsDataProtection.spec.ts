import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", async ({ page }) => {
  await navigate(page, "/nutzungstests-datenschutz");
  await expect(
    page.getByRole("heading", {
      name: `Datenschutzerklärung für die Registrierung für Nutzungsstudien für das Projekt „Testphase Rechtsinformationsportal"`,
    }),
  ).toBeVisible();
});
