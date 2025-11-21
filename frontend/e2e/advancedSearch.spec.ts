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

  await page.getByRole("button", { name: "Suchen" }).click();
}

test.describe.skip("reach advanced search search page", () => {
  // TODO: Add once RISDEV-10185 is implemented
});

test.describe("general advanced search page features", () => {
  test.skip("pagination switches pages", async ({ page }) => {
    await navigate(
      page,
      "/advanced-search?q=und&documentKind=N&dateFilterType=allTime",
    );

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText(nonZeroResultCount);

    const searchResults = getSearchResults(page);
    await expect(searchResults).toHaveCount(10);

    const pagination = page.getByRole("navigation", { name: "Paginierung" });
    await expect(pagination).toHaveText(/Seite 1: Treffer 1–10 von \d+/);

    await page.getByLabel("nächste Ergebnisse").click();
    await page.waitForURL("/advanced-search?q=und&pageNumber=1", {
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
    await page.waitForURL("/advanced-search?q=und&documentKind=N", {
      waitUntil: "commit",
    });
    await expect(searchResults).toHaveCount(10);
  });

  test("sort by date in ascending order", async ({ page }) => {
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

  test("sort by date in descending order", async ({ page }) => {
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

  test.skip("change number of results per page", async ({ page }) => {
    await navigate(
      page,
      "/advanced-search?q=und&documentKind=N&itemsPerPage=10",
    );

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByRole("combobox", { name: "10" }).click();
    await page.getByRole("option", { name: "50" }).click();

    await expect(searchResults).toHaveCount(15);
  });

  test.skip("reacts to browser back/forward navigation", () => {});
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

  test("shows the search result contents", async ({ page }) => {
    await navigate(page, "/advanced-search");

    await searchFor(page, {
      q: "FrSaftErfrischV",
      documentKind: "Gesetze & Verordnungen",
    });

    const searchResult = getSearchResults(page).first();

    // Header
    await expect(searchResult).toHaveText(/Norm/);
    await expect(searchResult).toHaveText(/FrSaftErfrischV/);
    await expect(searchResult).toHaveText(/29.04.2023/);

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
  test.skip("only shows caselaw results", async ({ page }) => {
    await navigate(page, "/advanced-search");

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();

    await expect(page).toHaveURL(/documentKind=R/);

    await expect(resultCounter).toHaveText(nonZeroResultCount);

    // Ensure all visible entries are of type caselaw
    await expect(searchResults).toHaveText(
      Array(10).fill(/^(Beschluss|Urteil)/),
    );
  });

  test.skip("shows the search result contents", async ({ page }) => {
    // TODO: searchFor
    await navigate(page, "/advanced-search?q=34+X+(xyz)+456/78&documentKind=R");

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

  test.skip("navigates to the document detail page", async ({ page }) => {
    await navigate(page, "/advanced-search?q=34+X+(xyz)+456/78&documentKind=R");

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

  test.skip("navigates to the document detail page", () => {});

  test.skip("searches without date restrictions", () => {});

  test.skip("filter to show specific date", () => {});

  test.skip("filters to show date range", () => {});

  test.skip("sorts by court in ascending order", () => {});

  test.skip("sorts by court in descending order", () => {});
});

test.describe("responsive", () => {
  // TODO: Desktop: show all fields
  // TODO: Mobile: show fields accordion
});
