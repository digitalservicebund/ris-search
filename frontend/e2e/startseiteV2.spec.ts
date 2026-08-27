import { expect, navigate, test } from "./utils/fixtures";

test.describe(
  "search section on the new start page",
  { tag: ["@RISDEV-12431"] },
  () => {
    test("shows headline, explanation and search input", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      await expect(
        page.getByRole("heading", {
          name: "Rechtsinformationen finden",
          level: 2,
        }),
      ).toBeVisible();
      await expect(
        page.getByText(
          "Nutzen Sie Stichwörter, Themen oder direkte Angaben wie Paragrafen, Normen oder Aktenzeichen.",
        ),
      ).toBeVisible();
      await expect(
        page.getByPlaceholder("z.B. Mietrecht, § 535 BGB, 1 BvR 123/20 …"),
      ).toBeVisible();
    });

    test("searches for a query", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      await page.getByRole("searchbox").fill("Fiktiv");
      await page.getByRole("button", { name: "Suchen" }).click();

      await expect(page).toHaveURL("/suche?query=Fiktiv");
      await expect(
        page.getByRole("heading", { name: "Suche", level: 1 }),
      ).toBeVisible();
      await expect(page.getByRole("searchbox")).toHaveValue("Fiktiv");
    });

    test("searches by pressing enter", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      await page.getByRole("searchbox").fill("Fiktiv");
      await page.getByRole("searchbox").press("Enter");

      await expect(page).toHaveURL("/suche?query=Fiktiv");
      await expect(page.getByRole("searchbox")).toHaveValue("Fiktiv");
    });

    test("supports an empty search", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      await page.getByRole("button", { name: "Suchen" }).click();

      await expect(page).toHaveURL("/suche");
      await expect(
        page.getByRole("heading", { name: "Suche", level: 1 }),
      ).toBeVisible();
      await expect(page.getByRole("searchbox")).toBeEmpty();
    });
  },
);
