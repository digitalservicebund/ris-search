import type { Page } from "@playwright/test";
import { expect, test, noJsTest } from "./utils/fixtures";

function getSearchResults(page: Page) {
  return page
    .getByRole("list", { name: "Suchergebnisse" })
    .getByRole("listitem");
}

function getResultCounter(page: Page) {
  // Not an ideal way for selecting this but I can't find a more semantic option
  // of doing it given the current page structure
  return page.getByText(/\d+ Suchergebnis(se)?/, { exact: true });
}

test.describe("reach search from start page", () => {
  test("searches for a query from the start page", async ({ page }) => {
    await page.goto("/");
    const searchInput = page.getByRole("searchbox", { name: "Suchbegriff" });
    await searchInput.fill("Fiktiv");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await expect(searchButton).not.toBeDisabled();
    await searchButton.click();

    await expect(getResultCounter(page)).toHaveText("15 Suchergebnisse");
    await expect(searchInput).toHaveValue("Fiktiv");
  });

  test("navigates back to the start page", async ({ page }) => {
    await page.goto("/search?query=Fiktiv");
    await page.getByRole("link", { name: "Zur Startseite" }).click();

    await expect(
      page.getByRole("searchbox", { name: "Suchbegriff" }),
      "should be reset",
    ).toBeEmpty();
  });
});

test.describe("general search page features", () => {
  test("pagination switches pages", async ({ page }) => {
    await page.goto("/search?query=und");

    const resultCounter = getResultCounter(page);
    await expect(resultCounter).toHaveText("13 Suchergebnisse");

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByLabel("nächste Ergebnisse").click();
    await page.waitForURL("/search?query=und&pageNumber=1");

    expect(resultCounter).toHaveText("13 Suchergebnisse");
    await expect(searchResults).toHaveCount(3);

    await page.getByLabel("vorherige Ergebnisse").click();
    await page.waitForURL("/search?query=und");
    await expect(searchResults).toHaveCount(10);
  });

  test("sort by date in ascending order", async ({ page }) => {
    await page.goto("/search?query=fiktiv", { waitUntil: "networkidle" });

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Datum: Älteste zuerst" }).click();

    await expect(page).toHaveURL(/sort=date/);
  });

  test("sort by date in descending order", async ({ page }) => {
    await page.goto("/search?query=fiktiv", { waitUntil: "networkidle" });

    await page.getByRole("combobox", { name: "Relevanz" }).click();
    await page.getByRole("option", { name: "Datum: Neueste zuerst" }).click();

    await expect(page).toHaveURL(/sort=-date/);
  });

  test("sort by relevance", async ({ page }) => {
    await page.goto("/search?query=fiktiv&sort=date", {
      waitUntil: "networkidle",
    });

    await page.getByRole("combobox", { name: "Datum: Älteste zuerst" }).click();
    await page.getByRole("option", { name: "Relevanz" }).click();

    await expect(page).not.toHaveURL(/sort=/);
  });

  test("change number of results per page", async ({ page }) => {
    await page.goto("/search?query=fiktiv", { waitUntil: "networkidle" });

    const searchResults = getSearchResults(page);

    await expect(searchResults).toHaveCount(10);

    await page.getByRole("combobox", { name: "10" }).click();
    await page.getByRole("option", { name: "50" }).click();

    await expect(searchResults).toHaveCount(15);
  });
});

test.describe("searching legislation", () => {
  test("narrows search", async ({ page }) => {
    await page.goto("/search?query=fiktiv", { waitUntil: "networkidle" });

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText("15 Suchergebnisse");

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gesetze & Verordnungen" })
      .click();

    await expect(page).toHaveURL(/category=N/);

    await expect(resultCounter).toHaveText("5 Suchergebnisse");

    // Ensure all visible entries are of type legislation
    await expect(searchResults).toHaveText(Array(5).fill(/^Norm/));
  });

  test("shows the search result contents", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    await page.goto("/search?query=FrSaftErfrischV&category=N", {
      waitUntil: "networkidle",
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
    await page.goto("/search?query=FrSaftErfrischV&category=N", {
      waitUntil: "networkidle",
    });

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
});

test.describe("searching caselaw", () => {
  test("narrows search", async ({ page }) => {
    await page.goto("/search?query=fiktiv", { waitUntil: "networkidle" });

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText("15 Suchergebnisse");

    await page
      .getByRole("group", { name: "Filter" })
      .getByRole("button", { name: "Gerichtsentscheidungen" })
      .click();

    await expect(page).toHaveURL(/category=R/);

    await expect(resultCounter).toHaveText("10 Suchergebnisse");

    // Ensure all visible entries are of type caselaw
    await expect(searchResults).toHaveText(
      Array(10).fill(/^(Beschluss|Urteil)/),
    );
  });

  test("shows the search result contents", async ({ page }) => {
    await page.goto("/search?query=34+X+(xyz)+456/78&category=R", {
      waitUntil: "networkidle",
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
    await page.goto("/search?query=34+X+(xyz)+456/78&category=R", {
      waitUntil: "networkidle",
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

  test("narrows by subtypes", async ({ page }) => {
    await page.goto("/search?category=R", { waitUntil: "networkidle" });

    await test.step("Urteil", async () => {
      await page
        .getByRole("group", { name: "Dokumentarten" })
        .getByRole("treeitem", { name: "Urteil" })
        .click();

      await expect(page).toHaveURL(/category=R\.urteil/);

      await expect(getSearchResults(page)).toHaveText(
        new Array(8).fill(/^Urteil/),
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
    await page.goto("/search?category=R", { waitUntil: "networkidle" });

    await page.getByRole("button", { name: "Vorschläge anzeigen" }).click();
    await page
      .getByRole("option", { name: "Bundesverfassungsgericht" })
      .click();

    await expect(page).toHaveURL(/court=BVerfG/);

    // No search results
  });

  test("searches by custom court", async ({ page }) => {
    await page.goto("/search?category=R", { waitUntil: "networkidle" });

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
    await page.goto("/search?query=ein", { waitUntil: "networkidle" });

    const searchResults = getSearchResults(page);
    const resultCounter = getResultCounter(page);

    await expect(resultCounter).toHaveText("12 Suchergebnisse");

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
    await page.goto("/search?query=FooBar,+1982,+123-123&category=L", {
      waitUntil: "networkidle",
    });

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

  test("navigates to the document detail page", async ({ page }) => {
    await page.goto("/search?query=FooBar,+1982,+123-123&category=L", {
      waitUntil: "networkidle",
    });

    // Result detail link
    await page
      .getByRole("link", {
        name: "Erstes Test-Dokument ULI",
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Erstes Test-Dokument ULI",
      }),
    ).toBeVisible();
  });

  test("searches by publication year with dateBefore", async ({ page }) => {
    await page.goto("/search?category=L", { waitUntil: "networkidle" });

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Bis zu einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2013");

    await expect(page).toHaveURL(/dateBefore=2013-12-31/);

    await expect(getSearchResults(page)).toHaveCount(3);
  });

  test("searches by publication year with dateAfter", async ({ page }) => {
    await page.goto("/search?category=L", { waitUntil: "networkidle" });

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "Ab einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2024");

    await expect(page).toHaveURL(/dateAfter=2024-01-01/);

    await expect(getSearchResults(page)).toHaveCount(1);
  });

  test("searches by publication year with dateBefore and dateAfter", async ({
    page,
  }) => {
    await page.goto("/search?category=L", { waitUntil: "networkidle" });

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "In einem Jahr" }).click();

    await page.getByRole("textbox", { name: "Jahr" }).fill("2015");

    await expect(page).toHaveURL(/dateAfter=2015-01-01&dateBefore=2015-12-31/);

    await expect(getSearchResults(page)).toHaveCount(1);
  });

  test("searches by publication year with range", async ({ page }) => {
    await page.goto("/search?category=L", { waitUntil: "networkidle" });

    await page
      .getByRole("combobox", { name: "Keine zeitliche Begrenzung" })
      .click();
    await page.getByRole("option", { name: "In einem Zeitraum" }).click();

    await page.getByRole("textbox", { name: "Ab dem Jahr" }).fill("2015");
    await page.getByRole("textbox", { name: "Bis zum Jahr" }).fill("2024");

    await expect(page).toHaveURL(/dateAfter=2015-01-01&dateBefore=2024-12-31/);

    await expect(getSearchResults(page)).toHaveCount(4);
  });
});

noJsTest("search works without JavaScript", async ({ page }) => {
  const searchTerm = "Fiktiv";

  await test.step("search from landing page", async () => {
    await page.goto("/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill(searchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/search?query=${searchTerm}`);
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      searchTerm,
    );
  });

  await test.step("search from search page", async () => {
    const newSearchTerm = "Test";
    await page.getByPlaceholder("Suchbegriff eingeben").fill(newSearchTerm);
    await page.getByRole("button", { name: "Suchen" }).click();

    await page.waitForURL(`/search?query=${newSearchTerm}`);
    expect(await getSearchResults(page).count()).toBeGreaterThan(0);
    await expect(page.getByPlaceholder("Suchbegriff eingeben")).toHaveValue(
      newSearchTerm,
    );
  });
});

noJsTest("pagination works without JavaScript", async ({ page }) => {
  await page.goto("/search?query=und", { waitUntil: "networkidle" });

  await expect(getResultCounter(page)).toHaveText("13 Suchergebnisse");
  await expect(getSearchResults(page)).toHaveCount(10);

  await page.getByLabel("nächste Ergebnisse").click();
  await page.waitForURL("/search?query=und&pageNumber=1", {
    waitUntil: "networkidle",
  });

  await expect(getResultCounter(page)).toHaveText("13 Suchergebnisse");
  await expect(getSearchResults(page)).toHaveCount(3);

  await page.getByLabel("vorherige Ergebnisse").click();
  await page.waitForURL("/search?query=und", { waitUntil: "networkidle" });

  await expect(getResultCounter(page)).toHaveText("13 Suchergebnisse");
  await expect(getSearchResults(page)).toHaveCount(10);
});
