import type { Page } from "@playwright/test";
import { expect, navigate, test } from "./utils/fixtures";

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

async function searchFor(
  page: Page,
  search: {
    q: string;
    documentKind: string;
    dateFilter?: string;
    dateFilterSpecificDate?: string;
    dateFilterFrom?: string;
    dateFilterTo?: string;
  },
) {
  await page.getByRole("button", { name: search.documentKind }).click();
  await page.getByRole("textbox", { name: "Suchanfrage" }).fill(search.q);

  if (search.dateFilter) {
    await page.getByRole("radio", { name: search.dateFilter }).click();
  }

  if (search.dateFilterSpecificDate) {
    await page
      .getByRole("textbox", { name: "Datum" })
      .fill(search.dateFilterSpecificDate);
  }

  if (search.dateFilterFrom) {
    await page
      .getByRole("textbox", { name: "von" })
      .fill(search.dateFilterFrom);
  }

  if (search.dateFilterTo) {
    await page.getByRole("textbox", { name: "bis" }).fill(search.dateFilterTo);
  }

  const results = page.waitForResponse(/v1\/document\/lucene-search/);

  await page.getByRole("button", { name: "Suchen" }).click();

  await results;
}

test.describe("general advanced search page features", () => {
  test("reachable through the erweiterte-suche alias", async ({ page }) => {
    await navigate(page, "/erweiterte-suche");

    await expect(
      page.getByRole("heading", { level: 1, name: "Erweiterte Suche" }),
    ).toBeVisible();
  });

  test("pagination switches pages", async ({ page }) => {
    await navigate(
      page,
      "/advanced-search?documentKind=R&dateFilterType=period&dateFilterFrom=2023-01-01&dateFilterTo=2025-12-31&itemsPerPage=10",
    );

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(10);

    const pagination = page.getByRole("navigation", { name: "Paginierung" });
    await expect(pagination).toHaveText(/Seite 1: Treffer 1–10 von \d+/);

    await page.getByLabel("nächste Ergebnisse").click();
    await page.waitForURL(/pageIndex=1/);

    expect(resultCounter).toHaveText(nonZeroResultCount);
    // Warning: this is potentially flaky and only works because the previous
    // assertion about the result counter has already "stabilized" the page.
    // Unfortunately there is no other way of asserting a number that isn't
    // exact.
    expect(await searchResults.count()).toBeGreaterThan(1);
    await expect(pagination).toHaveText(/Seite 2: Treffer 11–\d+ von \d+/);

    await page.getByLabel("vorherige Ergebnisse").click();
    await page.waitForURL(/pageIndex=0/);
    await expect(searchResults).toHaveCount(10);
  });

  test("sort by date in ascending order", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled, "dates are not publicly available");

    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "FrSaftErfrischV OR BWahlGV",
      documentKind: "Gesetze & Verordnungen",
    });

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page
      .getByRole("option", { name: "Ausfertigungsdatum: Älteste zuerst" })
      .click();

    await expect(page).toHaveURL(/sort=date/);

    await expect(getSearchResults(page)).toHaveText([
      /24.04.1999/,
      /29.04.2023/,
    ]);
  });

  test("sort by date in descending order", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled, "dates are not publicly available");

    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "FrSaftErfrischV OR BWahlGV",
      documentKind: "Gesetze & Verordnungen",
    });

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page
      .getByRole("option", { name: "Ausfertigungsdatum: Neueste zuerst" })
      .click();

    await expect(page).toHaveURL(/sort=-date/);

    await expect(getSearchResults(page)).toHaveText([
      /29.04.2023/,
      /24.04.1999/,
    ]);
  });

  test("sort by relevance (default)", async ({ page }) => {
    await navigate(page, "/advanced-search?q=und&documentKind=N&sort=date");

    await page.getByRole("combobox", { name: "Datum: Älteste zuerst" }).click();
    await page.getByRole("option", { name: "Relevanz" }).click();

    // Don't have a great way of asserting relevance, so just making sure the
    // parameter is handled correctly
    await expect(page).toHaveURL(/sort=default/);

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);
  });

  test("change number of results per page", async ({ page }) => {
    await navigate(
      page,
      "/advanced-search?documentKind=R&dateFilterType=period&dateFilterFrom=2023-01-01&dateFilterTo=2025-12-31&itemsPerPage=10",
    );

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByRole("combobox", { name: "10" }).click();
    await page.getByRole("option", { name: "50" }).click();

    await expect(searchResults).toHaveCount(12);
  });

  test("reacts to browser back/forward navigation", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "AB:FrSaftErfrischV",
      documentKind: "Gesetze & Verordnungen",
    });

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveText([/FrSaftErfrischV/]);

    await searchFor(page, {
      q: "AB:NLV",
      documentKind: "Gesetze & Verordnungen",
    });

    await expect(searchResults).toHaveText([/NLV/]);

    await page.goBack();

    await expect(
      page.getByRole("textbox", { name: "Suchanfrage" }),
    ).toHaveValue("AB:FrSaftErfrischV");

    await expect(searchResults).toHaveText([/FrSaftErfrischV/]);
  });
});

test.describe("searching legislation", () => {
  test("only shows legislation results", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "fiktiv",
      documentKind: "Gesetze & Verordnungen",
    });

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type legislation
    await expect(searchResults).toHaveText(Array(5).fill(/^Norm/));
  });

  test("shows the search result contents", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "FrSaftErfrischV",
      documentKind: "Gesetze & Verordnungen",
    });

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/FrSaftErfrischV/);

    if (privateFeaturesEnabled) {
      await expect(searchResult).toHaveText(/29.04.2023/);
    } else {
      await expect(searchResult).not.toHaveText(/29.04.2023/);
    }

    // Result detail link
    await expect(
      searchResult.getByRole("link", {
        name: "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
      }),
    ).toBeVisible();

    // Advanced search of norms doesn't support highlights
  });

  test("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/advanced-search?q=FrSaftErfrischV&documentKind=N");

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

  test("searches without date restrictions, shows validity badge", async ({
    page,
  }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: 'LU:"Zum Testen von Fassungen"',
      documentKind: "Gesetze & Verordnungen",
      dateFilter: "Keine zeitliche Begrenzung",
    });

    const results = getSearchResults(page);

    expect(results).toHaveText([
      /Zukünftig in Kraft/,
      /Aktuell gültig/,
      /Außer Kraft/,
    ]);
  });

  test("filters to show only currently valid, shows validity badge", async ({
    page,
  }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: 'LU:"Zum Testen von Fassungen"',
      documentKind: "Gesetze & Verordnungen",
      dateFilter: "Aktuell gültig",
    });

    const results = getSearchResults(page);

    expect(results).toHaveText([/Aktuell gültig/]);
  });

  test("filters to show specific date", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gesetze & Verordnungen",
      q: "",
      dateFilter: "Bestimmtes Datum",
      dateFilterSpecificDate: "01.01.2001",
    });

    // Don't have a great way of asserting this filter, so just making sure the
    // parameter is handled correctly
    await expect(page).toHaveURL(/dateFilterType=specificDate/);
    await expect(page).toHaveURL(/dateFilterFrom=2001-01-01/);

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);
  });

  test("filters to show date range", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gesetze & Verordnungen",
      q: "",
      dateFilter: "Innerhalb einer Zeitspanne",
      dateFilterFrom: "01.01.2001",
      dateFilterTo: "01.01.2022",
    });

    // Don't have a great way of asserting this filter, so just making sure the
    // parameter is handled correctly
    await expect(page).toHaveURL(/dateFilterType=period/);
    await expect(page).toHaveURL(/dateFilterFrom=2001-01-01/);
    await expect(page).toHaveURL(/dateFilterTo=2022-01-01/);

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);
  });
});

test.describe("searching caselaw", () => {
  test("only shows caselaw results", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "urteil",
      documentKind: "Gerichtsentscheidungen",
    });

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type caselaw
    await expect(searchResults).toHaveText(
      Array(10).fill(/^(Beschluss|Urteil)/),
    );
  });

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: 'AZ:"34 X (xyz) 456/78"',
      documentKind: "Gerichtsentscheidungen",
    });

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
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: 'AZ:"34 X (xyz) 456/78"',
      documentKind: "Gerichtsentscheidungen",
    });

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

  test("searches without date restrictions", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gerichtsentscheidungen",
      q: "urteil",
      dateFilter: "Keine zeitliche Begrenzung",
    });

    // Don't have a great way of asserting this filter, so just making sure the
    // parameter is handled correctly
    await expect(page).toHaveURL(/dateFilterType=allTime/);

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);
  });

  test("filter to show specific date", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gerichtsentscheidungen",
      q: "urteil",
      dateFilter: "Bestimmtes Datum",
      dateFilterSpecificDate: "15.06.2024",
    });

    const results = getSearchResults(page);

    expect(results).toHaveText([/15.06.2024/]);
  });

  test("filters to show date range", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gerichtsentscheidungen",
      q: "urteil",
      dateFilter: "Innerhalb einer Zeitspanne",
      dateFilterFrom: "01.01.2020",
      dateFilterTo: "31.12.2024",
    });

    const load = page.waitForResponse(/v1\/document\/lucene-search/);
    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page
      .getByRole("option", { name: "Entscheidungsdatum: Älteste zuerst" })
      .click();
    await load;

    const results = getSearchResults(page);

    expect(results).toHaveText([/22.11.2023/, /15.06.2024/]);
  });

  test("sorts by court in ascending order", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gerichtsentscheidungen",
      q: 'GERICHT:"ArbG Köln" OR GERICHT:"BDiG Frankfurt"',
    });

    const load = page.waitForResponse(/v1\/document\/lucene-search/);
    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Gericht: Von A nach Z" }).click();
    await load;

    const results = getSearchResults(page);

    expect(results).toHaveText([/ArbG Köln/, /BDiG Frankfurt/]);
  });

  test("sorts by court in descending order", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      documentKind: "Gerichtsentscheidungen",
      q: 'GERICHT:"ArbG Köln" OR GERICHT:"BDiG Frankfurt"',
    });

    const load = page.waitForResponse(/v1\/document\/lucene-search/);
    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Gericht: Von Z nach A" }).click();
    await load;

    const results = getSearchResults(page);

    expect(results).toHaveText([/BDiG Frankfurt/, /ArbG Köln/]);
  });
});

test.describe("responsive", () => {
  test.beforeEach(({ isMobileTest }) => {
    test.skip(!isMobileTest);
  });

  test("displays the available data fields in an accordion", async ({
    page,
  }) => {
    await navigate(page, "/advanced-search");

    const dataFieldsToggle = page.getByRole("button", {
      name: "Auswahl für gezielte Suche",
    });

    const dataFieldsList = page.getByRole("list", {
      name: "Durchsuchbare Datenfelder",
    });

    await expect(dataFieldsToggle).toBeVisible();
    await expect(dataFieldsList).not.toBeVisible();

    await dataFieldsToggle.click();
    await expect(dataFieldsList).toBeVisible();
  });
});

test.describe("search by AND + OR operators", { tag: ["@RISDEV-8385"] }, () => {
  test.describe("legislation", () => {
    test("searches with AND operator", async ({ page }) => {
      await navigate(page, "/advanced-search");

      await searchFor(page, {
        q: 'LU:"Verordnung" AND LU:"Kontrolle"',
        documentKind: "Gesetze & Verordnungen",
      });

      const results = getSearchResults(page);

      await expect(results).toHaveCount(1);
      await expect(results).toHaveText(/Verordnung/);
      await expect(results).toHaveText(/Kontrolle/);
    });

    test("searches with OR operator", async ({ page }) => {
      await navigate(page, "/advanced-search");

      await searchFor(page, {
        q: "LU:Fruchtsaft OR LU:Fruchtsirup",
        documentKind: "Gesetze & Verordnungen",
        dateFilter: "Keine zeitliche Begrenzung",
      });

      const results = getSearchResults(page);

      await expect(results).toHaveCount(2);
      await expect(results).toHaveText([/Fruchtsaft/, /Fruchtsirup/]);
    });
  });

  test.describe("caselaw", () => {
    test("searches with AND operator", async ({ page }) => {
      await navigate(page, "/advanced-search");

      await searchFor(page, {
        q: 'GERICHT:"LG Hamburg" AND "Urteil 4"',
        documentKind: "Gerichtsentscheidungen",
      });

      const results = getSearchResults(page);

      await expect(results).toHaveCount(1);
      await expect(results).toHaveText(/LG Hamburg/);
      await expect(results).toHaveText(/Urteil 4/);
    });

    test("searches with OR operator", async ({ page }) => {
      await navigate(page, "/advanced-search");

      await searchFor(page, {
        q: 'GERICHT:"ArbG Köln" OR GERICHT:"BDiG Frankfurt"',
        documentKind: "Gerichtsentscheidungen",
      });

      const results = getSearchResults(page);

      await expect(results).toHaveCount(2);
      await expect(results).toHaveText([/ArbG Köln/, /BDiG Frankfurt/]);
    });
  });
});
