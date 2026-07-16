import { expect, navigate, test } from "./utils/fixtures";

test.describe("open norm by abbreviation", async () => {
  test("redirects to most relevant expression", async ({ page }) => {
    await navigate(page, "/gesetze/RisFassTest");

    await page.waitForURL("norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu");
    await expect(page.getByRole("heading", { level: 1 })).toHaveText(
      "Zum Testen von Fassungen - Aktuelle Fassung",
    );
  });

  ["GeGuGe 2025", "geguge 2025", "geguge_2025"].forEach(
    (abbreviationVariant) => {
      test(`redirect works for abbreviation variant '${abbreviationVariant}'`, async ({
        page,
      }) => {
        await navigate(page, `/gesetze/${abbreviationVariant}`);

        await page.waitForURL(
          "norms/eli/bund/bgbl-1/2025/130/2025-05-05/1/deu",
        );
        await expect(page.getByRole("heading", { level: 1 })).toHaveText(
          "Gerade gültiges Gesetz",
        );
      });
    },
  );

  test("redirects to error page for unknown abbreviation", async ({ page }) => {
    await navigate(page, "/gesetze/unknownAbr");

    await page.waitForURL("/gesetze/unknownAbr");
    await expect(page.getByRole("heading", { level: 1 })).toHaveText(
      "Diese Seite existiert nicht",
    );
  });
});
