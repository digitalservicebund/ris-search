import type { Page } from "@playwright/test";
import { expect, navigate, noJsTest, test } from "./utils/fixtures";

function getSearchResults(page: Page) {
  return page
    .getByRole("list", { name: "Suchergebnisse" })
    .getByRole("listitem");
}

function getResultCounter(page: Page) {
  // Not an ideal way for selecting this but I can't find a more semantic option
  // of doing it given the current page structure
  return page.getByText(/[\d.]+ Suchergebnis(se)?/, { exact: true });
}

const nonZeroResultCount = /[1-9][\d.]* Suchergebnis(se)?/;

test.describe("reach search from start page", () => {
  test("searches for a query from the start page", async ({ page }) => {
    await navigate(page, "/");
    const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
    await searchInput.fill("Fiktiv");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await expect(searchButton).not.toBeDisabled();
    await searchButton.click();

    await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
    await expect(searchInput).toHaveValue("Fiktiv");
  });

  test("navigates back to the start page", async ({ page }) => {
    await navigate(page, "/search?query=Fiktiv");
    await page.getByRole("link", { name: "Zur Startseite" }).click();

    await expect(
      page.getByRole("searchbox", { name: "Suchfeld" }),
      "should be reset",
    ).toBeEmpty();
  });
});

test.describe("links to advanced search", () => {
  test("reachable from the simple search page", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(page, "/search");

    await page.getByRole("link", { name: "Erweiterte Suche" }).click();

    await expect(
      page.getByRole("heading", { level: 1, name: "Erweiterte Suche" }),
    ).toBeVisible();
  });

  test("not publicly reachable", async ({ page, privateFeaturesEnabled }) => {
    test.skip(privateFeaturesEnabled);

    await navigate(page, "/search");

    await expect(
      page.getByRole("link", { name: "Erweiterte Suche" }),
    ).not.toBeVisible();
  });
});

test.describe("general search page features", () => {
  test("sets the pages title", async ({ page }) => {
    await navigate(page, "/search");

    await expect(page).toHaveTitle("Suche | Rechtsinformationen des Bundes");

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gesetze & Verordnungen" })
      .click();
    await expect(page).toHaveTitle(
      "Gesetze & Verordnungen — Suche | Rechtsinformationen des Bundes",
    );

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();
    await expect(page).toHaveTitle(
      "Rechtsprechung — Suche | Rechtsinformationen des Bundes",
    );

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Verwaltungsvorschriften" })
      .click();
    await expect(page).toHaveTitle(
      "Verwaltungsvorschriften — Suche | Rechtsinformationen des Bundes",
    );

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Literaturnachweise" })
      .click();
    await expect(page).toHaveTitle(
      "Literaturnachweise — Suche | Rechtsinformationen des Bundes",
    );

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Alle Dokumentarten" })
      .click();

    await expect(page).toHaveTitle("Suche | Rechtsinformationen des Bundes");

    await page
      .getByRole("searchbox", { name: "Suchfeld" })
      .fill("frühstück brötchen");

    await page.getByRole("button", { name: "Suchen" }).click();

    await expect(page).toHaveTitle(
      "frühstück brötchen — Suche | Rechtsinformationen des Bundes",
    );
  });

  test("does not show date search filter", async ({ page }) => {
    await navigate(page, "/search?category=N");

    await expect(
      page.getByRole("combobox", { name: "Keine zeitliche Begrenzung" }),
    ).not.toBeVisible();
  });

  test("pagination switches pages", async ({ page }) => {
    await navigate(page, "/search?query=und");

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(10);

    const pagination = page.getByRole("navigation", { name: "Paginierung" });
    await expect(pagination).toHaveText(/Seite 1: Treffer 1–10 von \d+/);

    await page.getByLabel("nächste Ergebnisse").click();
    await page.waitForURL("/search?query=und&pageNumber=1", {
      waitUntil: "commit",
    });

    expect(resultCounter).toHaveText(nonZeroResultCount);
    // Warning: this is potentially flaky and only works because the previous
    // assertion about the result counter has already "stabilized" the page.
    // Unfortunately there is no other way of asserting a number that isn't
    // exact.
    expect(await searchResults.count()).toBeGreaterThan(1);
    await expect(pagination).toHaveText(/Seite 2: Treffer 11–\d+ von \d+/);

    await page.getByLabel("vorherige Ergebnisse").click();
    await page.waitForURL("/search?query=und", { waitUntil: "commit" });
    await expect(searchResults).toHaveCount(10);
  });

  test("sort by date in ascending order", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv");

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Datum: Älteste zuerst" }).click();

    await expect(page).toHaveURL(/sort=date/);
  });

  test("sort by date in descending order", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv");

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Datum: Neueste zuerst" }).click();

    await expect(page).toHaveURL(/sort=-date/);
  });

  test("sort by relevance (default)", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv&sort=date");

    await page.getByRole("combobox", { name: "Datum: Älteste zuerst" }).click();
    await page.getByRole("option", { name: "Relevanz" }).click();

    await expect(page).not.toHaveURL(/sort=/);
  });

  test("change number of results per page", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv");

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByRole("combobox", { name: "10" }).click();
    await page.getByRole("option", { name: "50" }).click();

    await expect(searchResults).toHaveCount(18);
  });

  test("falls back to last valid page when visiting an out-of-range pageNumber directly", async ({
    page,
  }) => {
    const nonExistingUrl = "/search?itemsPerPage=100&category=N&pageNumber=10";
    await navigate(page, nonExistingUrl);
    await expect(page).not.toHaveURL(/pageNumber=10/);
    const searchResults = await getSearchResults(page).all();
    expect(searchResults.length).toBeGreaterThan(0);
    await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  });
});

test.describe("searching all documents", () => {
  test("shows search results for all document kinds", async ({ page }) => {
    await navigate(page, "/search");

    await page.getByRole("combobox", { name: "10" }).click();
    await page.getByRole("option", { name: "50" }).click();

    // Norm
    await expect(
      page.getByText(
        "Fiktives Gesetz zur Musterfinanzierung politischer Einrichtungen",
      ),
    ).toBeVisible();

    // Caselaw Urteil
    await expect(page.getByText("Testheader für Urteil 7.")).toBeVisible();

    // Caselaw Beschluss
    await expect(
      page.getByText(
        "Beispielentscheid — Beispielheader für den Beschlusstext.",
      ),
    ).toBeVisible();

    // Literature
    await expect(page.getByText("Erstes Test-Dokument ULI")).toBeVisible();

    // Administrative directive
    await expect(
      page.getByText(
        "Verwaltungsvorschrift für das Testen des Portals zur Darstellung von Verwaltungsvorschriften",
      ),
    ).toBeVisible();
  });
});

test.describe("searching legislation", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gesetze & Verordnungen" })
      .click();

    await expect(page).toHaveURL(/category=N/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type legislation
    await expect(searchResults).toHaveText(Array(5).fill(/^Norm/));
  });

  test("shows the search result contents", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    await navigate(page, "/search?query=FrSaftErfrischV&category=N");

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/FrSaftErfrischV/);
    if (privateFeaturesEnabled) {
      await expect(searchResult).toHaveText(/29.04.2023/);
    } else {
      await expect(searchResult).not.toHaveText(/29.04.2023/);
    }

    await expect(searchResult).toHaveText(/Aktuell gültig/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByRole("link", { name: "§ 1 Anwendungsbereich" }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /\(1\) Die in Anlage 1 aufgeführten Erzeugnisse unterliegen/,
      ),
    ).toBeVisible();

    await expect(
      searchResult.getByRole("link", {
        name: "§ 2 Zutaten, Herstellungsanforderungen",
      }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /\(1\) Die Ausgangserzeugnisse für Erzeugnisse gemäß/,
      ),
    ).toBeVisible();

    await expect(
      searchResult.getByRole("link", { name: "§ 3 Kennzeichnung" }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /\(1\) Die Kennzeichnung der Erzeugnisse erfolgt nach/,
      ),
    ).toBeVisible();
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/search?query=FrSaftErfrischV&category=N");

    // Result detail link
    await page
      .getByRole("link", {
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      }),
    ).toBeVisible();
  });

  test("displays validity status batches", async ({ page }) => {
    await navigate(page, "/search?query=Zukunftsgesetz&category=N");
    await expect(getSearchResults(page).first()).toHaveText(
      /Zukünftig in Kraft/,
    );

    await navigate(page, "/search?query=Fruchtsaftkonzentrat&category=N");
    await expect(getSearchResults(page).first()).toHaveText(/Aktuell gültig/);

    await navigate(page, "/search?query=Heimaturlaubsberechtigung&category=N");
    await expect(getSearchResults(page).first()).toHaveText(/Außer Kraft/);
  });

  test("shows only most relevant Fassung", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    await navigate(page, '/search?query="Zum+Testen+von+Fassungen"&category=N');

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);

    const searchResult = searchResults.first();

    await expect(searchResult).toHaveText(/Norm/);
    if (privateFeaturesEnabled) {
      await expect(searchResult).toHaveText(/04.08.2022/);
    } else {
      await expect(searchResult).not.toHaveText(/04.08.2022/);
    }

    await expect(searchResult).toHaveText(/Aktuell gültig/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });

  test("does not show date search filter", async ({ page }) => {
    await navigate(page, "/search?category=N");

    await expect(
      page.getByRole("combobox", { name: "Keine zeitliche Begrenzung" }),
    ).not.toBeVisible();
  });
});

test.describe("searching caselaw", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/search?query=fiktiv");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();

    await expect(page).toHaveURL(/category=R/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type caselaw
    await expect(searchResults).toHaveText(
      Array(10).fill(/^(Beschluss|Urteil)/),
    );
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/search?query=34+X+(xyz)+456/78&category=R");

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Beschluss/);
    await expect(searchResult).toHaveText(/BPatG Teststadt/);
    await expect(searchResult).toHaveText(/09.04.2025/);
    await expect(searchResult).toHaveText(/34 X \(xyz\) 456\/78/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Beispielentscheid — Beispielheader für den Beschlusstext.",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByRole("link", { name: "Orientierungssatz:" }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(/1. Fiktiver Satz für Testzwecke im Beschluss/),
    ).toBeVisible();
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/search?query=34+X+(xyz)+456/78&category=R");

    // Result detail link
    await page
      .getByRole("link", {
        name: "Beispielentscheid — Beispielheader für den Beschlusstext.",
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Beispielheader für den Beschlusstext.",
      }),
    ).toBeVisible();
  });

  test("narrows by subtypes", async ({ page }) => {
    await navigate(page, "/search?category=R");

    await test.step("Urteil", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Urteil" })
        .click();

      await expect(page).toHaveURL(/category=R\.urteil/);

      await expect(getSearchResults(page)).toHaveText(
        new Array(10).fill(/^Urteil/),
      );
    });

    await test.step("Beschluss", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Beschluss" })
        .click();

      await expect(page).toHaveURL(/category=R\.beschluss/);

      await expect(getSearchResults(page)).toHaveText(
        new Array(2).fill(/^Beschluss/),
      );
    });

    await test.step("Other", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Sonstige Entscheidungen" })
        .click();

      await expect(page).toHaveURL(/category=R\.other/);

      await expect(getSearchResults(page)).toHaveCount(0);
    });

    await test.step("reset", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Alle Gerichtsentscheidungen" })
        .click();

      await expect(page).toHaveURL(/category=R/);

      await expect(getSearchResults(page)).toHaveText(
        Array(10).fill(/^(Beschluss|Urteil)/),
      );
    });
  });

  test("searches by suggested court", async ({ page }) => {
    await navigate(page, "/search?category=R");

    await page.getByRole("button", { name: "Vorschläge anzeigen" }).click();
    await page
      .getByRole("option", { name: "Bundesverfassungsgericht" })
      .click();

    await expect(page).toHaveURL(/court=BVerfG/);

    // No search results
  });

  test("searches by custom court", async ({ page }) => {
    await navigate(page, "/search?category=R");

    await page.getByRole("combobox", { name: "Gericht" }).fill("LG");
    await page.getByRole("option", { name: "LG Hamburg" }).click();

    await expect(page).toHaveURL(/court=LG\+Hamburg/);

    await expect(getSearchResults(page)).toHaveText(
      Array(2).fill(/LG Hamburg/),
    );
  });

  test.skip("filters by date", async () => {
    // Filters will be aligned with the advanced search, so test will be added
    // later
  });
});

test.describe("searching literature", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/search?query=ein");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Literaturnachweise" })
      .click();

    await expect(page).toHaveURL(/category=L/);

    await expect(resultCounter).toHaveText("1 Suchergebnis");

    // Ensure all visible entries are of type literature
    await expect(searchResults).toHaveText(/^Auf/);
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/search?query=FooBar,+1982,+123-123&category=L");

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Auf/);
    await expect(searchResult).toHaveText(/FooBar, 1982, 123-123/);
    await expect(searchResult).toHaveText(/2024/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Erstes Test-Dokument ULI",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByText(/Dies ist ein einfaches Test-Dokument./),
    ).toBeVisible();
  });

  test("shows placeholder title for search result items without title", async ({
    page,
  }) => {
    await navigate(
      page,
      "/search?query=Dieses+Dokument+hat+keinen+Titel&category=L",
    );

    // Result detail link
    await page
      .getByRole("link", {
        name: "Titelzeile nicht vorhanden",
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Titelzeile nicht vorhanden",
      }),
    ).toBeVisible();
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/search?query=FooBar,+1982,+123-123&category=L");

    // Result detail link
    await page.getByRole("link", { name: "Erstes Test-Dokument ULI" }).click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Erstes Test-Dokument ULI",
      }),
    ).toBeVisible();
  });

  test("searches by publication year with dateBefore", async ({ page }) => {
    await navigate(page, "/search?category=L");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Bis zu einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2013");

    await expect(page).toHaveURL(/dateBefore=2013-12-31/);

    await expect(getSearchResults(page)).toHaveCount(4);
  });

  test("searches by publication year with dateAfter", async ({ page }) => {
    await navigate(page, "/search?category=L");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Ab einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2024");

    await expect(page).toHaveURL(/dateAfter=2024-01-01/);

    await expect(getSearchResults(page)).toHaveCount(3);
  });

  test("searches by publication year with dateBefore and dateAfter", async ({
    page,
  }) => {
    await navigate(page, "/search?category=L");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "In einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2015");

    await expect(page).toHaveURL(/dateAfter=2015-01-01&dateBefore=2015-12-31/);

    await expect(getSearchResults(page)).toHaveCount(1);
  });

  test("searches by publication year with range", async ({ page }) => {
    await navigate(page, "/search?category=L");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "In einem Zeitraum" }).click();

    await page.getByRole("textbox", { name: "Ab dem Jahr" }).fill("2015");
    await page.getByRole("textbox", { name: "Bis zum Jahr" }).fill("2024");

    await expect(page).toHaveURL(/dateAfter=2015-01-01&dateBefore=2024-12-31/);

    await expect(getSearchResults(page)).toHaveCount(6);
  });
});

test.describe("searching administrative directives", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/search?query=wurde");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Verwaltungsvorschriften" })
      .click();

    await expect(page).toHaveURL(/category=V/);

    await expect(resultCounter).toHaveText("1 Suchergebnis");

    // Ensure all visible entries are of type administrative directive
    await expect(searchResults).toHaveText(/^VB/);
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/search?query=wurde&category=V");

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/VB/);
    await expect(searchResult).toHaveText(/FooBar/);
    await expect(searchResult).toHaveText(/Baz - 121 - 1/);
    await expect(searchResult).toHaveText(/24.12.2022/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Beschluss über den Beschluss",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByText(
        /… Beschlossen wurde, das Beschlüsse beschlossen werden müssen./,
      ),
    ).toBeVisible();
  });

  test("shows placeholder title for search result items without title", async ({
    page,
  }) => {
    await navigate(page, "/search?query=keinen+Titel&category=V");

    // Result detail link
    await page
      .getByRole("link", {
        name: "Titelzeile nicht vorhanden",
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Titelzeile nicht vorhanden",
      }),
    ).toBeVisible();
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/search?query=Beschluss&category=V");

    // Result detail link
    await page
      .getByRole("link", { name: "Beschluss über den Beschluss" })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Beschluss über den Beschluss",
      }),
    ).toBeVisible();

    await expect(
      page.getByText(
        /Dies ist ein Testdokument. Beschlossen wurde, das Beschlüsse beschlossen werden müssen./,
      ),
    ).toBeVisible();
  });

  test("searches by entryIntoForce date with dateBefore", async ({ page }) => {
    await navigate(page, "/search?category=V");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Bis zu einem Datum" }).click();

    await page.getByRole("textbox", { name: "Datum" }).fill("15.03.2019");

    await expect(page).toHaveURL(/dateBefore=2019-03-15/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(3);
    await expect(searchResults.nth(0)).toHaveText(/14.03.2019/);
  });

  test("searches by entryIntoForce date with dateAfter", async ({ page }) => {
    await navigate(page, "/search?category=V");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Ab einem Datum" }).click();

    await page.getByRole("textbox", { name: "Datum" }).fill("01.07.2025");

    await expect(page).toHaveURL(/dateAfter=2025-07-01/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);
    await expect(searchResults).toHaveText(/01.07.2025/);
  });

  test("searches by specific date", async ({ page }) => {
    await navigate(page, "/search?category=V");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "An einem Datum" }).click();

    await page.getByRole("textbox", { name: "Datum" }).fill("23.12.2022");

    await expect(page).toHaveURL(/date=2022-12-23/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(0);

    await page.getByRole("textbox", { name: "Datum" }).fill("24.12.2022");
    await expect(page).toHaveURL(/date=2022-12-24/);

    await expect(searchResults).toHaveCount(1);
    await expect(searchResults).toHaveText(/24.12.2022/);
  });

  test("searches by entryIntoForce date with range", async ({ page }) => {
    await navigate(page, "/search?category=V");

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "In einem Zeitraum" }).click();

    await page
      .getByRole("textbox", { name: "Ab dem Datum" })
      .fill("14.03.2019");
    await page
      .getByRole("textbox", { name: "Bis zum Datum" })
      .fill("24.12.2022");

    await expect(page).toHaveURL(/dateAfter=2019-03-14&dateBefore=2022-12-24/);

    await expect(getSearchResults(page)).toHaveCount(2);
  });

  test("sort by date", async ({ page }) => {
    await navigate(page, "/search?category=V");

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Datum: Älteste zuerst" }).click();

    await expect(page).toHaveURL(/sort=date/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(5);

    await expect(searchResults.nth(0)).toHaveText(/01.11.2004/);

    await page.getByRole("combobox", { name: "Datum: Älteste zuerst" }).click();
    await page.getByRole("option", { name: "Datum: Neueste zuerst" }).click();

    await expect(page).toHaveURL(/sort=-date/);

    await expect(searchResults).toHaveCount(5);

    await expect(searchResults.nth(0)).toHaveText(/01.07.2025/);
  });
});

noJsTest("search works without JavaScript", async ({ page }) => {
  const searchTerm = "Fiktiv";

  await test.step("search from landing page", async () => {
    await navigate(page, "/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill(searchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/search?query=${searchTerm}`, {
      waitUntil: "commit",
    });
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      searchTerm,
    );
  });

  await test.step("search from search page", async () => {
    const newSearchTerm = "Test";
    await page.getByPlaceholder("Suchbegriff eingeben").fill(newSearchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/search?query=${newSearchTerm}`, {
      waitUntil: "commit",
    });
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      newSearchTerm,
    );
  });
});

noJsTest("pagination works without JavaScript", async ({ page }) => {
  await navigate(page, "/search?query=und");

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  await expect(getSearchResults(page)).toHaveCount(10);

  await page.getByLabel("nächste Ergebnisse").click();
  await page.waitForURL("/search?query=und&pageNumber=1", {
    waitUntil: "commit",
  });

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  // Warning: this is potentially flaky and only works because the previous
  // assertion about the result counter has already "stabilized" the page.
  // Unfortunately there is no other way of asserting a number that isn't
  // exact.
  expect(await getSearchResults(page).count()).toBeGreaterThan(1);

  await page.getByLabel("vorherige Ergebnisse").click();
  await page.waitForURL("/search?query=und", { waitUntil: "commit" });

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  await expect(getSearchResults(page)).toHaveCount(10);
});
