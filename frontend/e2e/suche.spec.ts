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
    const searchInput = page.getByRole("searchbox");
    await searchInput.fill("Fiktiv");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await expect(searchButton).not.toBeDisabled();
    await searchButton.click();

    await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
    await expect(searchInput).toHaveValue("Fiktiv");
  });

  test("navigates back to the start page", async ({ page }) => {
    await navigate(page, "/suche?query=Fiktiv");
    await page
      .getByRole("link", { name: "Rechtsinformationen des Bundes" })
      .click();

    await expect(page.getByRole("searchbox"), "should be reset").toBeEmpty();
  });

  test("browser back restores previous state", async ({ page }) => {
    await navigate(page, "/");

    await page.getByRole("button", { name: "Suchen" }).click();
    await expect(
      page.getByRole("heading", { name: "Suche", level: 1 }),
    ).toBeVisible();

    const searchInput = page.getByRole("searchbox");
    await searchInput.fill("Fiktiv");
    await page.getByRole("button", { name: "Suchen" }).click();
    await page.waitForURL(/query=Fiktiv/);
    await expect(searchInput).toHaveValue("Fiktiv");

    await page.goBack();
    await expect(searchInput).toBeEmpty();

    await page.goBack();
    await expect(page).toHaveURL("/");
  });
});

test.describe("links to advanced search", () => {
  test("reachable from the simple search page", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(page, "/suche");

    await page.getByRole("link", { name: "Erweiterte Suche" }).click();

    await expect(
      page.getByRole("heading", { level: 1, name: "Erweiterte Suche" }),
    ).toBeVisible();
  });

  test("not publicly reachable", async ({ page, privateFeaturesEnabled }) => {
    test.skip(privateFeaturesEnabled);

    await navigate(page, "/suche");

    await expect(
      page.getByRole("link", { name: "Erweiterte Suche" }),
    ).not.toBeVisible();
  });
});

test.describe("general search page features", () => {
  test("does not show date search filter", async ({ page }) => {
    await navigate(page, "/suche?documentKind=N");

    await expect(
      page.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
    ).not.toBeVisible();
  });

  test("pagination switches pages", async ({ page }) => {
    await navigate(page, "/suche?query=und&itemsPerPage=10");

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(10);

    const pagination = page.getByRole("navigation", { name: "Paginierung" });
    await expect(pagination).toHaveText(/Seite 1: Treffer 1–10 von \d+/);

    await pagination.getByRole("link", { name: "Weiter" }).click();
    await page.waitForURL(/pageIndex=1/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);
    // Warning: this is potentially flaky and only works because the previous
    // assertion about the result counter has already "stabilized" the page.
    // Unfortunately there is no other way of asserting a number that isn't
    // exact.
    expect(await searchResults.count()).toBeGreaterThan(1);
    await expect(pagination).toHaveText(/Seite 2: Treffer 11–\d+ von \d+/);

    await pagination.getByRole("link", { name: "Zurück" }).click();
    await page.waitForURL(/pageIndex=0/);
    await expect(searchResults).toHaveCount(10);
  });

  test("focuses first search result after pagination", async ({ page }) => {
    await navigate(page, "/suche?query=und&itemsPerPage=10");

    await page
      .getByRole("navigation", { name: "Paginierung" })
      .getByRole("link", { name: "Weiter" })
      .click();
    await page.waitForURL(/pageIndex=1/);

    const firstResultLink = page
      .getByRole("list", { name: "Suchergebnisse" })
      .getByRole("link")
      .first();
    await expect(firstResultLink).toBeFocused();
  });

  test("sort by date in ascending order", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?query=fiktiv");

    await page.getByRole("combobox", { name: "Sortieren nach" }).click();
    await page.getByRole("option", { name: "Datum: Älteste zuerst" }).click();

    await expect(page).toHaveURL(/sort=date/);
  });

  test("sort by date in descending order", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?query=fiktiv");

    await page.getByRole("combobox", { name: "Sortieren nach" }).click();
    await page.getByRole("option", { name: "Datum: Neueste zuerst" }).click();

    await expect(page).toHaveURL(/sort=-date/);
  });

  test("sort by relevance (default)", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?query=fiktiv&sort=date");

    await page.getByRole("combobox", { name: "Sortieren nach" }).click();
    await page.getByRole("option", { name: "Relevanz" }).click();

    await expect(page).toHaveURL(/sort=default/);
  });

  test("change number of results per page", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?query=fiktiv&itemsPerPage=10");

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByRole("combobox", { name: "Einträge pro Seite" }).click();
    await page.getByRole("option", { name: "50" }).click();

    await expect(searchResults).toHaveCount(18);
  });

  test("falls back to last valid page when visiting an out-of-range pageIndex directly", async ({
    page,
  }) => {
    const nonExistingUrl =
      "/suche?itemsPerPage=100&documentKind=N&pageIndex=10";
    await navigate(page, nonExistingUrl);
    await expect(page).not.toHaveURL(/pageIndex=10/);
    const searchResults = await getSearchResults(page).all();
    expect(searchResults.length).toBeGreaterThan(0);
    await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  });
});

test.describe("searching all documents", () => {
  test("shows search results for all document kinds", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche");

    await page.getByRole("combobox", { name: "Einträge pro Seite" }).click();
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
      page.getByText("Beispielheader für den Beschlusstext."),
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
    await navigate(page, "/suche?query=fiktiv");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Gesetze & Verordnungen" })
      .click();

    await expect(page).toHaveURL(/documentKind=N/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type legislation
    await expect(searchResults).toHaveText(Array(5).fill(/^Norm/));
  });

  test("shows the search result contents when private features are enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(
      page,
      "/suche?query=Lebensmittel-+und+Bedarfsgegenständegesetzes&documentKind=N",
    );

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/NormFrSaftErfrischV/);
    await expect(searchResult).toHaveText(/29\.04\.2023/);
    await expect(searchResult).toHaveText(/Aktuell gültig/);
    await expect(searchResult).toHaveText(
      /Fruchtsaft- und Erfrischungsgetränkeverordnung/,
    );

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByRole("link", {
        name: "Eingangsformel",
      }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /Auf Grundlage des fiktiven Lebensmittel- und Bedarfsgegenständegesetzes in der Fassung vom 27\./,
      ),
    ).toBeVisible();

    await expect(
      searchResult.getByRole("link", {
        name: "§ 3 Kennzeichnung",
      }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /Alle wesentlichen Informationen wie Inhaltsstoffe, Mindesthaltbarkeitsdatum und Herkunft sind/,
      ),
    ).toBeVisible();

    await expect(
      searchResult.getByRole("link", {
        name: "Anlage T1 (zu § 4 Absatz 2, § 5 Absatz 1 bis 3, § 6 Absatz 2 bis 4 und § 8)",
      }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /Werden Säfte aus Früchten mit Kernen, Samenkörnern und Schale hergestellt/,
      ),
    ).toBeVisible();
  });

  test("shows from and to date when it exists and private features are enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(
      page,
      "/suche?query=Heimaturlaubsberechtigung&documentKind=N",
    );

    const searchResult = getSearchResults(page).first();
    await expect(searchResult).toHaveText(/01\.04\.1990 - 31\.12\.1990/);
  });

  test("doesn't show article highlights without a direct text match when private features are enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(page, "/suche?query=FrSaftErfrischV&documentKind=N");

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/FrSaftErfrischV/);
    await expect(searchResult).toHaveText(/29.04.2023/);

    await expect(searchResult).toHaveText(/Aktuell gültig/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      }),
    ).toBeVisible();

    await expect(
      searchResult.getByRole("link", { name: "§ 1 Anwendungsbereich" }),
    ).not.toBeAttached();
  });

  test("shows the search result contents when private features are disabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(privateFeaturesEnabled);
    await navigate(page, "/suche?query=FrSaftErfrischV&documentKind=N");

    const searchResult = getSearchResults(page).first();

    await expect(searchResult).not.toHaveText(/29.04.2023/);
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/suche?query=FrSaftErfrischV&documentKind=N");

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
    await navigate(page, "/suche?query=Zukunftsgesetz&documentKind=N");
    await expect(getSearchResults(page).first()).toHaveText(
      /Zukünftig in Kraft/,
    );

    await navigate(page, "/suche?query=Fruchtsaftkonzentrat&documentKind=N");
    await expect(getSearchResults(page).first()).toHaveText(/Aktuell gültig/);

    await navigate(
      page,
      "/suche?query=Heimaturlaubsberechtigung&documentKind=N",
    );
    await expect(getSearchResults(page).first()).toHaveText(/Außer Kraft/);
  });

  test("shows only most relevant Fassung when private features are enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(
      page,
      '/suche?query="Zum+Testen+von+Fassungen"&documentKind=N',
    );

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);

    const searchResult = searchResults.first();

    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/04.08.2022/);

    await expect(searchResult).toHaveText(/Aktuell gültig/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });

  test("shows only most relevant Fassung when private features are disabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(privateFeaturesEnabled);
    await navigate(
      page,
      '/suche?query="Zum+Testen+von+Fassungen"&documentKind=N',
    );

    const searchResult = getSearchResults(page).first();

    await expect(searchResult).not.toHaveText(/04.08.2022/);
  });

  test("does not show date search filter", async ({ page }) => {
    await navigate(page, "/suche?documentKind=N");

    await expect(
      page.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
    ).not.toBeVisible();
  });

  test("navigates to article view for search results", async ({ page }) => {
    await navigate(page, "/suche?query=Eingangsformel&documentKind=N");

    const searchResult = getSearchResults(page).first();
    await searchResult.getByRole("link", { name: "Eingangsformel" }).click();

    await expect(
      page.getByRole("heading", { level: 1, name: "Eingangsformel" }),
    ).toBeVisible();
  });
});

test.describe("searching caselaw", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/suche?query=fiktiv");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();

    await expect(page).toHaveURL(/documentKind=R/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type caselaw
    await expect(searchResults).toHaveText(
      Array(13).fill(/^(Beschluss|Urteil)/),
    );
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(
      page,
      "/suche?query=34+X+(xyz)+456/78+Verfahrensbeschreibung&documentKind=R",
    );

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Beschluss/);
    await expect(searchResult).toHaveText(/BPatG Label/);
    await expect(searchResult).toHaveText(/09.04.2025/);
    await expect(searchResult).toHaveText(/34 X \(xyz\) 456\/78/);
    await expect(searchResult).toHaveText(/Beispielentscheid/);

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Beispielheader für den Beschlusstext.",
      }),
    ).toBeVisible();

    // Highlights
    await expect(
      searchResult.getByRole("link", { name: "Orientierungssatz:" }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(
        /Weitere fiktive Informationen zur Verfahrensbeschreibung./,
      ),
    ).toBeVisible();
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/suche?query=34+X+(xyz)+456/78&documentKind=R");

    // Result detail link
    await page
      .getByRole("link", {
        name: "Beispielheader für den Beschlusstext.",
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
    await navigate(page, "/suche?documentKind=R");

    await test.step("Urteil", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Urteil" })
        .click();

      await expect(page).toHaveURL(/documentKind=R/);
      await expect(page).toHaveURL(/typeGroup=urteil/);

      await expect(getSearchResults(page)).toHaveText(
        Array.from<RegExp>({ length: 12 }).fill(/^Urteil/),
      );
    });

    await test.step("Beschluss", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Beschluss" })
        .click();

      await expect(page).toHaveURL(/documentKind=R/);
      await expect(page).toHaveURL(/typeGroup=beschluss/);

      await expect(getSearchResults(page)).toHaveText(
        Array.from<RegExp>({ length: 3 }).fill(/^Beschluss/),
      );
    });

    await test.step("Other", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Sonstige Entscheidungen" })
        .click();

      await expect(page).toHaveURL(/documentKind=R/);
      await expect(page).toHaveURL(/typeGroup=other/);

      await expect(getSearchResults(page)).toHaveCount(0);
    });

    await test.step("reset", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Alle Gerichtsentscheidungen" })
        .click();

      await expect(page).toHaveURL(/documentKind=R/);
      await expect(page).toHaveURL(/typeGroup=($|&)/);

      await expect(getSearchResults(page)).toHaveText(
        Array(15).fill(/^(Beschluss|Urteil)/),
      );
    });
  });

  test("searches by suggested court", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("button", { name: "Vorschläge anzeigen" }).click();
    await page
      .getByRole("option", { name: "Bundesverfassungsgericht" })
      .click();

    await expect(page).toHaveURL(/court=BVerfG/);

    // No search results
  });

  test("searches by custom court", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("combobox", { name: "Bundesgericht" }).fill("LG");
    await page
      .getByRole("option", { name: "Landgericht Hamburg Label" })
      .click();

    await expect(page).toHaveURL(/court=LG\+Hamburg/);

    await expect(getSearchResults(page)).toHaveText(
      Array(3).fill(/LG Hamburg Label/),
    );
  });

  test("does not trigger a search when selecting a date filter type without entering a date", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=R");
    const initialUrl = page.url();

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page.getByRole("radio", { name: "Bestimmtes Datum" }).click();
    expect(page.url()).toBe(initialUrl);

    await page.getByRole("textbox", { name: "Datum" }).fill("10.04.2025");
    await expect(page).toHaveURL(/dateFilterFrom=2025-04-10/);
  });

  test("searches decision date on a specific date", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("radio", { name: "Bestimmtes Datum" }).click();

    await page.getByRole("textbox", { name: "Datum" }).fill("15.06.2024");

    await expect(page).toHaveURL(/dateFilterFrom=2024-06-15&dateFilterTo=/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);
    await expect(searchResults).toHaveText(/15.06.2024/);
  });

  test("searches decision date in a range", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=R");

    await page
      .getByRole("radio", { name: "Innerhalb eines Zeitraums" })
      .click();

    await page.getByRole("textbox", { name: "von" }).fill("01.01.2024");
    await page.getByRole("textbox", { name: "bis" }).fill("31.12.2024");

    await expect(page).toHaveURL(
      /dateFilterFrom=2024-01-01&dateFilterTo=2024-12-31/,
    );

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);
  });

  test("resets date input when switching filter types", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(
      page,
      "/suche?documentKind=R&dateFilterType=specificDate&dateFilterFrom=2020-01-01",
    );

    await expect(page.getByRole("textbox", { name: "Datum" })).toHaveValue(
      "01.01.2020",
    );

    await page
      .getByRole("radio", { name: "Innerhalb eines Zeitraums" })
      .click();

    await expect(page.getByRole("textbox", { name: "von" })).toHaveValue("");
    await expect(page.getByRole("textbox", { name: "bis" })).toHaveValue("");
  });

  test("resets caselaw-specific filters when switching to all documents", async ({
    page,
  }) => {
    // Start with caselaw search with typeGroup, date filter, and court
    await navigate(
      page,
      "/suche?documentKind=R&typeGroup=urteil&court=LG+Hamburg+Label&dateFilterType=period&dateFilterFrom=2025-01-01&dateFilterTo=2025-12-31",
    );

    await expect(getResultCounter(page)).toHaveText("3 Suchergebnisse");

    // Switch to All Documents
    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Alle Dokumentarten" })
      .click();

    await expect(getResultCounter(page)).toHaveText("44 Suchergebnisse");

    // Verify caselaw-specific filters are reset
    await expect(page).not.toHaveURL(/documentKind=R/);
    await expect(page).not.toHaveURL(/typeGroup=urteil/);
    await expect(page).not.toHaveURL(/court=LG\+Hamburg/);
    await expect(page).not.toHaveURL(/dateFilterFrom=2024-01-01/);
    await expect(page).not.toHaveURL(/dateFilterTo=2024-12-31/);
  });
});

test.describe("searching literature", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/suche?query=ein");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Literaturnachweise" })
      .click();

    await expect(page).toHaveURL(/documentKind=L/);

    await expect(resultCounter).toHaveText("1 Suchergebnis");

    // Ensure all visible entries are of type literature
    await expect(searchResults).toHaveText(/^Auf/);
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(
      page,
      "/suche?query=FooBar,+1982,+123-123+einfaches+Test-Dokument&documentKind=L",
    );

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
      searchResult.getByRole("link", { name: "Kurzreferat:" }),
    ).toBeVisible();
    await expect(
      searchResult.getByText(/Dies ist ein einfaches Test-Dokument./),
    ).toBeVisible();
  });

  test("shows placeholder headline for search result items without headline", async ({
    page,
  }) => {
    await navigate(
      page,
      "/suche?query=Dieses+Dokument+hat+keinen+Titel&documentKind=L",
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
    await navigate(page, "/suche?query=FooBar,+1982,+123-123&documentKind=L");

    // Result detail link
    await page.getByRole("link", { name: "Erstes Test-Dokument ULI" }).click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Erstes Test-Dokument ULI",
      }),
    ).toBeVisible();
  });

  test("searches for specific publication year", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=L");

    await page.getByRole("radio", { name: "Bestimmtes Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2015");

    await expect(page).toHaveURL(/dateFilterFrom=2015&dateFilterTo=($|&)/);

    await expect(getSearchResults(page)).toHaveCount(1);
  });

  test("searches by publication year with range", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=L");

    await page
      .getByRole("radio", { name: "Innerhalb eines Zeitraums" })
      .click();

    await page.getByRole("textbox", { name: "von" }).fill("2015");
    await page.getByRole("textbox", { name: "bis" }).fill("2024");

    await expect(page).toHaveURL(/dateFilterFrom=2015&dateFilterTo=2024/);

    await expect(getSearchResults(page)).toHaveCount(7);
  });

  test("resets year input when switching filter types", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(
      page,
      "/suche?documentKind=L&dateFilterType=period&dateFilterFrom=2020&dateFilterTo=2024",
    );

    await expect(page.getByRole("textbox", { name: "von" })).toHaveValue(
      "2020",
    );

    await page.getByRole("radio", { name: "Bestimmtes Jahr" }).click();

    await expect(page.getByRole("textbox", { name: "Jahr" })).toHaveValue("");
  });
});

test.describe("searching administrative directives", () => {
  test("narrows search", async ({ page }) => {
    await navigate(page, "/suche?query=wurde");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Verwaltungsvorschriften" })
      .click();

    await expect(page).toHaveURL(/documentKind=V/);

    await expect(resultCounter).toHaveText("1 Suchergebnis");

    // Ensure all visible entries are of type administrative directive
    await expect(searchResults).toHaveText(/^VB/);
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/suche?query=wurde&documentKind=V");

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
      searchResult.getByRole("link", { name: "Kurzreferat:" }),
    ).toBeVisible();
    await expect(searchResult.getByText(/Beschlossen wurde/)).toBeVisible();
  });

  test("shows placeholder headline for search result items without headline", async ({
    page,
  }) => {
    await navigate(page, "/suche?query=keinen+Titel&documentKind=V");

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
    await navigate(page, "/suche?query=Beschluss&documentKind=V");

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

  test("searches entry into force on a specific date", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=V");

    await page.getByRole("radio", { name: "Bestimmtes Datum" }).click();

    await page.getByRole("textbox", { name: "Datum" }).fill("23.12.2022");

    await expect(page).toHaveURL(/dateFilterFrom=2022-12-23&dateFilterTo=/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(0);

    await page.getByRole("textbox", { name: "Datum" }).fill("24.12.2022");
    await expect(page).toHaveURL(/dateFilterFrom=2022-12-24&dateFilterTo=/);

    await expect(searchResults).toHaveCount(1);
    await expect(searchResults).toHaveText(/24.12.2022/);
  });

  test("searches entry into force in a range", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=V");

    await page
      .getByRole("radio", { name: "Innerhalb eines Zeitraums" })
      .click();

    await page.getByRole("textbox", { name: "von" }).fill("14.03.2019");
    await page.getByRole("textbox", { name: "bis" }).fill("24.12.2022");

    await expect(page).toHaveURL(
      /dateFilterFrom=2019-03-14&dateFilterTo=2022-12-24/,
    );

    await expect(getSearchResults(page)).toHaveCount(2);
  });

  test("sort by date", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, "/suche?documentKind=V");

    await page.getByRole("combobox", { name: "Sortieren nach" }).click();
    await page.getByRole("option", { name: "Datum: Älteste zuerst" }).click();

    await expect(page).toHaveURL(/sort=date/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(5);

    await expect(searchResults.nth(0)).toHaveText(/01.11.2004/);

    await page.getByRole("combobox", { name: "Sortieren nach" }).click();
    await page.getByRole("option", { name: "Datum: Neueste zuerst" }).click();

    await expect(page).toHaveURL(/sort=-date/);

    await expect(searchResults).toHaveCount(5);

    await expect(searchResults.nth(0)).toHaveText(/01.07.2025/);
  });
});

test.describe("mobile filter and sort drawers", () => {
  test.beforeEach(({ isMobileTest }) => {
    test.skip(!isMobileTest);
  });

  test("shows the 'Filtern' button for document kinds with sub-filters", async ({
    page,
  }) => {
    await navigate(page, "/suche");
    await expect(
      page.getByRole("button", { name: "Filtern" }),
    ).not.toBeVisible();

    await page
      .getByRole("complementary", { name: "Dokumentart" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();

    await expect(page.getByRole("button", { name: "Filtern" })).toBeVisible();
  });

  test("applies a court filter from the filter drawer", async ({ page }) => {
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("button", { name: "Filtern" }).click();
    const dialog = page.getByRole("dialog", { name: "Filtern" });
    await expect(dialog).toBeVisible();

    await dialog.getByRole("combobox", { name: "Bundesgericht" }).fill("LG");
    await page
      .getByRole("option", { name: "Landgericht Hamburg Label" })
      .click();

    await dialog.getByRole("button", { name: "Anwenden" }).click();

    await expect(dialog).not.toBeVisible();
    await expect(page).toHaveURL(/court=LG\+Hamburg/);
    await expect(getSearchResults(page)).toHaveText(
      Array(3).fill(/LG Hamburg Label/),
    );
  });

  test("aligns the court suggestions overlay with the input field", async ({
    page,
  }) => {
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("button", { name: "Filtern" }).click();
    const dialog = page.getByRole("dialog", { name: "Filtern" });
    await expect(dialog).toBeVisible();

    const field = dialog.getByTestId("court-filter-field");
    await dialog.getByRole("combobox", { name: "Bundesgericht" }).fill("LG");

    // The overlay is teleported to <body>, so it's not a descendant of the
    // dialog - only one instance of it can be open at a time though. Its
    // `role="listbox"` sits on the inner options list, which is inset from
    // the outer panel by padding, so the panel (identified by the marker
    // class CourtFilter.vue applies for its own alignment fix) is what
    // needs to line up with the field, not the listbox itself.
    const overlayPanel = page.locator(".court-filter-overlay");

    // Wait for the actual search results, not just the overlay shell - the
    // option list keeps resizing (and re-triggering PrimeVue's own,
    // overridden alignment) until the debounced search resolves.
    await expect(
      page.getByRole("option", { name: "Landgericht Hamburg Label" }),
    ).toBeVisible();

    const fieldBox = await field.boundingBox();
    const overlayBox = await overlayPanel.boundingBox();
    expect(fieldBox).not.toBeNull();
    expect(overlayBox).not.toBeNull();

    expect(overlayBox!.x).toBeCloseTo(fieldBox!.x, 0);
    expect(overlayBox!.x + overlayBox!.width).toBeCloseTo(
      fieldBox!.x + fieldBox!.width,
      0,
    );
  });

  test("applies a date filter from the filter drawer", async ({ page }) => {
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("button", { name: "Filtern" }).click();
    const dialog = page.getByRole("dialog", { name: "Filtern" });
    await expect(dialog).toBeVisible();

    await dialog
      .getByRole("radio", { name: "Innerhalb eines Zeitraums" })
      .click();
    await dialog.getByRole("textbox", { name: "von" }).fill("01.01.2024");
    await dialog.getByRole("textbox", { name: "bis" }).fill("31.12.2024");

    await dialog.getByRole("button", { name: "Anwenden" }).click();

    await expect(dialog).not.toBeVisible();
    await expect(page).toHaveURL(
      /dateFilterFrom=2024-01-01&dateFilterTo=2024-12-31/,
    );

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(1);
    await expect(searchResults.nth(0)).toHaveText(/15.06.2024/);
  });

  test("discards filter drawer changes when closed without applying", async ({
    page,
  }) => {
    await navigate(page, "/suche?documentKind=R");

    await page.getByRole("button", { name: "Filtern" }).click();
    const dialog = page.getByRole("dialog", { name: "Filtern" });

    await dialog.getByRole("combobox", { name: "Bundesgericht" }).fill("LG");
    await page
      .getByRole("option", { name: "Landgericht Hamburg Label" })
      .click();

    await dialog.getByRole("button", { name: "Schließen" }).click();
    await expect(dialog).not.toBeVisible();

    await expect(page).not.toHaveURL(/court=/);

    // Reopening the drawer shows the field reset to the last committed value
    await page.getByRole("button", { name: "Filtern" }).click();
    await expect(
      dialog.getByRole("combobox", { name: "Bundesgericht" }),
    ).toHaveValue("");
  });

  test("'Zurücksetzen' resets and immediately commits the filters", async ({
    page,
  }) => {
    await navigate(
      page,
      "/suche?documentKind=R&court=LG+Hamburg+Label&dateFilterType=period&dateFilterFrom=2025-01-01&dateFilterTo=2025-12-31",
    );

    await page.getByRole("button", { name: "Filtern" }).click();
    const dialog = page.getByRole("dialog", { name: "Filtern" });

    await dialog.getByRole("button", { name: "Zurücksetzen" }).click();

    await expect(dialog).not.toBeVisible();
    await expect(page).not.toHaveURL(/court=LG\+Hamburg/);
    await expect(page).not.toHaveURL(/dateFilterFrom=2025-01-01/);
  });

  test("applies sorting from the sort drawer", async ({ page }) => {
    await navigate(page, "/suche?documentKind=V");

    await page.getByRole("button", { name: "Sortieren" }).click();
    const dialog = page.getByRole("dialog", { name: "Sortieren" });
    await expect(dialog).toBeVisible();

    await dialog.getByRole("radio", { name: "Datum: Älteste zuerst" }).click();
    await dialog.getByRole("button", { name: "Anwenden" }).click();

    await expect(dialog).not.toBeVisible();
    await expect(page).toHaveURL(/sort=date/);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(5);
    await expect(searchResults.nth(0)).toHaveText(/01.11.2004/);
  });

  test("applies page size from the sort drawer", async ({ page }) => {
    await navigate(page, "/suche?query=fiktiv&itemsPerPage=10");

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(10);

    await page.getByRole("button", { name: "Sortieren" }).click();
    const dialog = page.getByRole("dialog", { name: "Sortieren" });

    await dialog.getByRole("radio", { name: "50" }).click();
    await dialog.getByRole("button", { name: "Anwenden" }).click();

    await expect(dialog).not.toBeVisible();
    await expect(page).toHaveURL(/itemsPerPage=50/);
    await expect(searchResults).toHaveCount(18);
  });
});

test("restores search state from document breadcrumbs", async ({ page }) => {
  await navigate(page, "/suche");

  const searchInput = page.getByRole("searchbox");
  await searchInput.fill("FrSaftErfrischV");
  await page.getByRole("button", { name: "Suchen" }).click();

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

  await page
    .getByRole("navigation", { name: "Pfadnavigation" })
    .getByRole("link", { name: "Suche" })
    .click();

  await expect(page.getByRole("searchbox")).toHaveValue("FrSaftErfrischV");
});

noJsTest("search works without JavaScript", async ({ page }) => {
  const searchTerm = "Fiktiv";

  await test.step("search from landing page", async () => {
    await navigate(page, "/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill(searchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/suche?query=${searchTerm}`);
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      searchTerm,
    );
  });

  await test.step("search from search page", async () => {
    const newSearchTerm = "Test";
    await page.getByPlaceholder("Suchbegriff eingeben").fill(newSearchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/suche?query=${newSearchTerm}`);
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      newSearchTerm,
    );
  });
});

noJsTest("pagination works without JavaScript", async ({ page }) => {
  await navigate(page, "/suche?query=und&itemsPerPage=10");

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  await expect(getSearchResults(page)).toHaveCount(10);

  await page
    .getByRole("navigation", { name: "Paginierung" })
    .getByRole("link", { name: "Weiter" })
    .click();
  await page.waitForURL(/pageIndex=1/);

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  // Warning: this is potentially flaky and only works because the previous
  // assertion about the result counter has already "stabilized" the page.
  // Unfortunately there is no other way of asserting a number that isn't
  // exact.
  expect(await getSearchResults(page).count()).toBeGreaterThan(1);

  await page
    .getByRole("navigation", { name: "Paginierung" })
    .getByRole("link", { name: "Zurück" })
    .click();
  expect(page).not.toHaveURL(/pageIndex=\d+/);

  await expect(getResultCounter(page)).toHaveText(nonZeroResultCount);
  await expect(getSearchResults(page)).toHaveCount(10);
});
