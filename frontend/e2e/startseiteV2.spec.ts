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
      await expect(page.getByPlaceholder("Suchbegriff eingeben")).toBeVisible();
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

    test("links to advanced search", async ({
      page,
      privateFeaturesEnabled,
    }) => {
      test.skip(!privateFeaturesEnabled);

      await navigate(page, "/startseite-v2");

      await page.getByRole("link", { name: "Erweiterte Suche" }).click();

      await expect(
        page.getByRole("heading", { level: 1, name: "Erweiterte Suche" }),
      ).toBeVisible();
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

test.describe(
  "Aktuelles section on the new start page",
  { tag: ["@RISDEV-12432"] },
  () => {
    const tabNames = [
      "Gesetze und Verordnungen",
      "Gerichtsentscheidungen",
      "Verwaltungsvorschriften",
      "Literaturnachweise",
    ];

    test("shows a tab per document kind", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      await expect(
        page.getByRole("heading", { name: "Aktuelles", level: 2 }),
      ).toBeVisible();

      const tabs = page.getByRole("tablist", {
        name: "Aktuelles nach Dokumentart",
      });
      await expect(tabs.getByRole("tab")).toHaveText(tabNames);

      // The first tab is the one selected on load
      await expect(
        tabs.getByRole("tab", { name: tabNames[0] }),
      ).toHaveAttribute("aria-selected", "true");
    });

    // Norms are left out: they are filtered to a window around the current
    // date, which the fixtures with their fixed dates never fall into
    const kindsWithResults = [
      {
        tab: "Gerichtsentscheidungen",
        detailPath: /^\/gerichtsentscheidungen\//,
      },
      {
        tab: "Verwaltungsvorschriften",
        detailPath: /^\/verwaltungsregelungen\//,
      },
      { tab: "Literaturnachweise", detailPath: /^\/literaturnachweise\// },
    ];

    for (const { tab, detailPath } of kindsWithResults) {
      test(`shows the most recent documents under ${tab}`, async ({ page }) => {
        await navigate(page, "/startseite-v2");

        await page.getByRole("tab", { name: tab }).click();

        const results = page.getByRole("tabpanel").getByRole("listitem");
        await expect(results).not.toHaveCount(0);
        await expect(
          results.first().getByRole("heading", { level: 3 }),
        ).toBeVisible();
        await expect(results.first().getByRole("link").first()).toHaveAttribute(
          "href",
          detailPath,
        );
      });
    }

    test("switches between document kinds", async ({ page }) => {
      await navigate(page, "/startseite-v2");

      const panel = page.getByRole("tabpanel");
      await expect(
        panel.getByRole("link", { name: "Zu den Gesetzen und Verordnungen" }),
      ).toBeVisible();

      await page.getByRole("tab", { name: "Literaturnachweise" }).click();

      await expect(
        page.getByRole("tab", { name: "Literaturnachweise" }),
      ).toHaveAttribute("aria-selected", "true");
      await expect(
        panel.getByRole("link", { name: "Zu den Literaturnachweisen" }),
      ).toBeVisible();
    });

    test("links to the search for the selected document kind", async ({
      page,
    }) => {
      await navigate(page, "/startseite-v2");

      await page.getByRole("tab", { name: "Verwaltungsvorschriften" }).click();
      await page
        .getByRole("link", { name: "Zu den Verwaltungsvorschriften" })
        .click();

      await expect(page).toHaveURL("/suche?documentKind=V");
      await expect(
        page.getByRole("heading", { name: "Suche", level: 1 }),
      ).toBeVisible();
    });
  },
);
