import type { Page } from "@playwright/test";
import { expect, navigate, test } from "./utils/fixtures";

const testCases = [
  {
    name: "simple search",
    route: "/search?query=aktuelle%2520fassung&documentKind=N",
    returnTo: "/search?query=aktuelle%252520fassung%26documentKind=N",
    label: "Suche",
  },
  {
    name: "advanced search",
    route: "/advanced-search?q=aktuelle%2520fassung&documentKind=N",
    returnTo: "/advanced-search?q=aktuelle%252520fassung%26documentKind=N",
    label: "Erweiterte Suche",
  },
];

const caseLawTestCases = [
  {
    name: "simple search",
    route:
      "/search?query=Testangaben%2520zur%2520Dokumentennavigation&documentKind=R",
    returnTo:
      "/search?query=Testangaben%252520zur%252520Dokumentennavigation%26documentKind=R",
    label: "Suche",
    searchValue: "Testangaben zur Dokumentennavigation",
  },
  {
    name: "advanced search",
    route:
      "/advanced-search?q=Testangaben%2520zur%2520Dokumentennavigation&documentKind=R",
    returnTo:
      "/advanced-search?q=Testangaben%252520zur%252520Dokumentennavigation%26documentKind=R",
    label: "Erweiterte Suche",
    searchValue: "Testangaben zur Dokumentennavigation",
  },
];

const adminDirectiveTestCases = [
  {
    name: "simple search",
    route: "/search?query=Genaues&documentKind=V",
    returnTo: "/search?query=Genaues%26documentKind=V",
    label: "Suche",
    searchValue: "Genaues",
  },
  {
    name: "advanced search",
    route: "/advanced-search?q=Genaues&documentKind=V",
    returnTo: "/advanced-search?q=Genaues%26documentKind=V",
    label: "Erweiterte Suche",
    searchValue: "Genaues",
  },
];

const literatureTestCases = [
  {
    name: "simple search",
    route: "/search?query=Problemstellung&documentKind=L",
    returnTo: "/search?query=Problemstellung%26documentKind=L",
    label: "Suche",
    searchValue: "Problemstellung",
  },
  {
    name: "advanced search",
    route: "/advanced-search?q=Problemstellung&documentKind=L",
    returnTo: "/advanced-search?q=Problemstellung%26documentKind=L",
    label: "Erweiterte Suche",
    searchValue: "Problemstellung",
  },
];

testCases.forEach((t) => {
  test.describe(`retains search state for "${t.name}"`, () => {
    const fromParam = new RegExp(RegExp.escape(`from=${t.returnTo}`));

    test.describe("legislation", () => {
      // keep in local scope for readability
      // oxlint-disable-next-line unicorn/consistent-function-scoping
      async function goToDetail(page: Page) {
        await page
          .getByRole("link", {
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          })
          .click();
      }

      test("open detail page via result title", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(page).toHaveURL(fromParam);
      });

      test("restores search state via breadcrumb", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page
          .getByRole("navigation", { name: "Pfadnavigation" })
          .getByRole("link", { name: t.label })
          .click();

        await expect(page.getByRole("searchbox")).toHaveValue(
          "aktuelle fassung",
        );
      });

      test("keep search state on tabs", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(page.getByRole("tab", { name: "Text" })).toHaveAttribute(
          "href",
          fromParam,
        );
        await expect(
          page.getByRole("tab", { name: "Details" }),
        ).toHaveAttribute("href", fromParam);
        await expect(
          page.getByRole("tab", { name: "Fassungen" }),
        ).toHaveAttribute("href", fromParam);
      });

      test("keep search state in versions list", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        // Zukünftige Fassung
        await page.getByRole("tab", { name: "Fassungen" }).click();
        await page.getByRole("cell", { name: "Zukünftig in Kraft" }).click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Zukünftige Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);

        // Aktuelle Fassung
        await page.getByRole("tab", { name: "Fassungen" }).click();
        await page.getByRole("cell", { name: "Aktuell gültig" }).click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);

        // Außer Kraft
        await page.getByRole("tab", { name: "Fassungen" }).click();
        await page.getByRole("cell", { name: "Außer Kraft" }).click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Alte Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state in versions messages", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        // Zukünftige Fassung
        await page
          .getByRole("link", { name: "Zur zukünftigen Fassung" })
          .click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Zukünftige Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);

        // Aktuelle Fassung
        await page
          .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
          .click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);

        // Außer Kraft
        await page.getByRole("tab", { name: "Fassungen" }).click();
        await page.getByRole("cell", { name: "Außer Kraft" }).click();
        await page
          .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
          .click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state in table of contents section items", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page
          .getByRole("navigation", { name: "Inhalte" })
          .getByRole("link", { name: "Abschnitt 1" })
          .click();

        await expect(page).toHaveURL(/#hauptteil-n1_abschnitt-n1/);
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state in table of contents article items", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page
          .getByRole("navigation", { name: "Inhalte" })
          .getByRole("treeitem", { name: "Abschnitt 1" })
          .getByRole("button", { name: "Ebene öffnen" })
          .click();

        await page
          .getByRole("navigation", { name: "Inhalte" })
          .getByRole("link", { name: "§ 1", exact: true })
          .click();

        await expect(page).toHaveURL(/#hauptteil-n1_abschnitt-n1_art-z1/);
        await expect(page).toHaveURL(fromParam);
      });

      // Not yet implemented
      test.skip("keep search state when navigating to article in norm", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();

        await page
          .getByRole("heading", { level: 2, name: "Eingangsformel" })
          .click();
        await expect(
          page.getByRole("heading", { level: 1, name: "Eingangsformel" }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });
    });

    test.describe("legislation article", () => {
      // keep in local scope for readability
      // oxlint-disable-next-line unicorn/consistent-function-scoping
      async function goToDetail(page: Page) {
        await page
          .getByRole("link", { name: "Eingangsformel" })
          .first()
          .click();
      }

      test("open detail page via article match title", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(page).toHaveURL(fromParam);
      });

      test("restores search state via breadcrumb", async ({ page }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page
          .getByRole("navigation", { name: "Pfadnavigation" })
          .getByRole("link", { name: t.label })
          .click();

        await expect(page.getByRole("searchbox")).toHaveValue(
          "aktuelle fassung",
        );
      });

      test("keep search state when navigating to the parent norm", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page.getByRole("link", { name: "Nächster Paragraf" }).click();

        await page
          .getByRole("navigation", { name: "Pfadnavigation" })
          .getByRole("link", { name: "Zum Testen von…" })
          .click();

        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state when navigating to the parent section", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page.getByRole("link", { name: "Nächster Paragraf" }).click();

        await page
          .getByRole("navigation", { name: "Pfadnavigation" })
          .getByRole("link", { name: "Abschnitt 1" })
          .click();

        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state when navigating to other paragraphs", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page.getByRole("link", { name: "Nächster Paragraf" }).click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "§ 1 Anwendungsbereich",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);

        await page.getByRole("link", { name: "Vorheriger Paragraf" }).click();
        await expect(
          page.getByRole("heading", {
            level: 1,
            name: "Eingangsformel",
          }),
        ).toBeVisible();
        await expect(page).toHaveURL(fromParam);
      });

      test("keep search state in table of contents section items", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(
          page
            .getByRole("navigation", { name: "Inhalte" })
            .getByRole("link", { name: "Abschnitt 1" }),
        ).toHaveAttribute("href", fromParam);
      });

      test("keep search state in table of contents article items", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await page
          .getByRole("navigation", { name: "Inhalte" })
          .getByRole("treeitem", { name: "Abschnitt 1" })
          .getByRole("button", { name: "Ebene öffnen" })
          .click();

        await expect(
          page
            .getByRole("navigation", { name: "Inhalte" })
            .getByRole("link", { name: "§ 1" }),
        ).toHaveAttribute("href", fromParam);
      });

      test("keep search state in table of contents parent link", async ({
        page,
      }) => {
        await navigate(page, t.route);
        await goToDetail(page);

        await expect(
          page.getByRole("navigation", { name: "Inhalte" }).getByRole("link", {
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toHaveAttribute("href", fromParam);
      });
    });
  });
});

caseLawTestCases.forEach((t) => {
  test.describe(`retains search state for caselaw "${t.name}"`, () => {
    const fromParam = new RegExp(RegExp.escape(`from=${t.returnTo}`));

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaTitle(page: Page) {
      await page
        .getByRole("link", {
          name: "Testheader für Urteil 10 mit Randnummernverlinkung.",
        })
        .click();
    }

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaSection(page: Page) {
      await page
        .getByRole("listitem")
        .filter({
          has: page.getByRole("heading", {
            name: "Testheader für Urteil 10 mit Randnummernverlinkung.",
          }),
        })
        .getByRole("link", { name: "Orientierungssatz:" })
        .click();
    }

    test("open detail page via result title", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page).toHaveURL(fromParam);
    });

    test("open detail page via section title in search result", async ({
      page,
    }) => {
      await navigate(page, t.route);
      await goToDetailViaSection(page);

      await expect(page).toHaveURL(fromParam);
      await expect(page).toHaveURL(/#orientierungssatz/);
    });

    test("restores search state via breadcrumb", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Pfadnavigation" })
        .getByRole("link", { name: t.label })
        .click();

      await expect(page.getByRole("searchbox")).toHaveValue(t.searchValue);
    });

    test("keep search state on tabs", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page.getByRole("tab", { name: "Text" })).toHaveAttribute(
        "href",
        fromParam,
      );
      await expect(page.getByRole("tab", { name: "Details" })).toHaveAttribute(
        "href",
        fromParam,
      );
    });

    test("keep search state in table of contents", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Inhalte" })
        .getByRole("link", { name: "Orientierungssatz" })
        .click();

      await expect(page).toHaveURL(/#orientierungssatz/);
      await expect(page).toHaveURL(fromParam);
    });

    test("keep search state when clicking a randnummer link", async ({
      page,
    }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("link", { name: "Springe zu Randnummer: 1" })
        .click();

      await expect(page).toHaveURL(/#randnummer-1/);
      await expect(page).toHaveURL(fromParam);
    });
  });
});

adminDirectiveTestCases.forEach((t) => {
  test.describe(`retains search state for administrative directives "${t.name}"`, () => {
    const fromParam = new RegExp(RegExp.escape(`from=${t.returnTo}`));

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaTitle(page: Page) {
      await page
        .getByRole("link", {
          name: "Verwaltungsvorschrift für das Testen des Portals zur Darstellung von Verwaltungsvorschriften",
        })
        .click();
    }

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaSection(page: Page) {
      await page.getByRole("link", { name: "Inhalt:" }).click();
    }

    test("open detail page via result title", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page).toHaveURL(fromParam);
    });

    test("open detail page via section title in search result", async ({
      page,
    }) => {
      await navigate(page, t.route);
      await goToDetailViaSection(page);

      await expect(page).toHaveURL(fromParam);
      await expect(page).toHaveURL(/#inhalt/);
    });

    test("restores search state via breadcrumb", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Pfadnavigation" })
        .getByRole("link", { name: t.label })
        .click();

      await expect(page.getByRole("searchbox")).toHaveValue(t.searchValue);
    });

    test("keep search state on tabs", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page.getByRole("tab", { name: "Text" })).toHaveAttribute(
        "href",
        fromParam,
      );
      await expect(page.getByRole("tab", { name: "Details" })).toHaveAttribute(
        "href",
        fromParam,
      );
    });

    test("keep search state in table of contents", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Inhalte" })
        .getByRole("link", { name: "Inhalt" })
        .click();

      await expect(page).toHaveURL(/#inhalt/);
      await expect(page).toHaveURL(fromParam);
    });
  });
});

literatureTestCases.forEach((t) => {
  test.describe(`retains search state for literature "${t.name}"`, () => {
    const fromParam = new RegExp(RegExp.escape(`from=${t.returnTo}`));

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaTitle(page: Page) {
      await page
        .getByRole("link", { name: "Test-Dokument SLI" })
        .first()
        .click();
    }

    // oxlint-disable-next-line unicorn/consistent-function-scoping
    async function goToDetailViaSection(page: Page) {
      await page.getByRole("link", { name: "Gliederung:" }).first().click();
    }

    test("open detail page via result title", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page).toHaveURL(fromParam);
    });

    test("open detail page via section title in search result", async ({
      page,
    }) => {
      await navigate(page, t.route);
      await goToDetailViaSection(page);

      await expect(page).toHaveURL(fromParam);
      await expect(page).toHaveURL(/#gliederung/);
    });

    test("restores search state via breadcrumb", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Pfadnavigation" })
        .getByRole("link", { name: t.label })
        .click();

      await expect(page.getByRole("searchbox")).toHaveValue(t.searchValue);
    });

    test("keep search state on tabs", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await expect(page.getByRole("tab", { name: "Text" })).toHaveAttribute(
        "href",
        fromParam,
      );
      await expect(page.getByRole("tab", { name: "Details" })).toHaveAttribute(
        "href",
        fromParam,
      );
    });

    test("keep search state in table of contents", async ({ page }) => {
      await navigate(page, t.route);
      await goToDetailViaTitle(page);

      await page
        .getByRole("navigation", { name: "Inhalte" })
        .getByRole("link", { name: "Gliederung" })
        .click();

      await expect(page).toHaveURL(/#gliederung/);
      await expect(page).toHaveURL(fromParam);
    });
  });
});
