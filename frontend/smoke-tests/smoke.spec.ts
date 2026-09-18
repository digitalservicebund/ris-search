import { expect, test } from "../e2e/utils/fixtures";

test("search from homepage", async ({ page }) => {
  await page.goto("/");

  await expect(
    page.getByRole("heading", {
      level: 1,
      name: "Rechtsinformationen des Bundes",
    }),
  ).toBeVisible();

  const searchInput = page.getByRole("searchbox");
  await searchInput.fill("apfel");

  const searchButton = page.getByRole("button", { name: "Suchen" });
  await searchButton.click();

  await expect(
    page.getByRole("heading", {
      level: 1,
      name: "Suche",
    }),
  ).toBeVisible();

  // None zero result count
  await expect(page.getByText(/[\d.]+ Suchergebnisse/)).toBeVisible();
});

test.describe("find and display norms", () => {
  test("open norm from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox");
    await searchInput.fill("Brennereiordnung");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("radio", { name: "Gesetze & Verordnungen" }).click();
    await page
      .getByRole("link", { name: "Brennereiordnung", exact: true })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Brennereiordnung",
      }),
    ).toBeVisible();
  });

  test("open norm directly (server side rendered)", async ({ page }) => {
    await page.goto("/gesetze/eli/bund/banz-at/1922/s717/1922-09-12/1/deu");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Brennereiordnung",
      }),
    ).toBeVisible();
  });
});

test.describe("find and display caselaws", () => {
  test("open caselaw from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox");
    await searchInput.fill(
      '"BVerwG, Beschluss vom 7. Januar 2010 - 20 F 5/09"',
    );

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("radio", { name: "Gerichtsentscheidungen" }).click();
    await page
      .getByRole("link", {
        name: "BVerwG, Beschluss vom 7. Januar 2010 - 20 F 5/09",
        exact: true,
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "BVerwG, Beschluss vom 7. Januar 2010 - 20 F 5/09",
      }),
    ).toBeVisible();
  });

  test("open caselaw directly (server side rendered)", async ({ page }) => {
    await page.goto("/gerichtsentscheidungen/JURE100055708");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "BVerwG, Beschluss vom 7. Januar 2010 - 20 F 5/09",
      }),
    ).toBeVisible();
  });
});

test.describe("find and display literature", () => {
  test("open literature from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox");
    await searchInput.fill("Die Arbeitergilden der Gegenwart");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("radio", { name: "Literaturnachweise" }).click();
    await page
      .getByRole("link", {
        name: "Die Arbeitergilden der Gegenwart",
        exact: true,
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Die Arbeitergilden der Gegenwart",
      }),
    ).toBeVisible();
  });

  test("open literature directly (server side rendered)", async ({ page }) => {
    await page.goto("/literaturnachweise/KSLS051342704");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Die Arbeitergilden der Gegenwart",
      }),
    ).toBeVisible();
  });
});

test.describe("serve document sitemaps", () => {
  test("verify sitemap index referenced in robots.txt is reachable", async ({
    request,
  }) => {
    const response = await request.get("/robots.txt");

    expect(response.ok()).toBeTruthy();
    const body = await response.text();

    const sitemapReference = body
      .split("\n")
      .map((line) => line.trim())
      .find((line) => line.toLowerCase().startsWith("sitemap:"));

    const sitemapPath = sitemapReference
      ? sitemapReference.slice(sitemapReference.lastIndexOf("/"))
      : null;

    expect(sitemapPath).not.toBeNull();

    const sitemapIndexResponse = await request.get(sitemapPath!);
    expect(sitemapIndexResponse.ok()).toBeTruthy();

    const sitemapIndexContent = await sitemapIndexResponse.text();
    expect(sitemapIndexContent).toContain(
      "http://www.sitemaps.org/schemas/sitemap/0.9",
    );
  });
});

test.describe("serve ecli sitemaps", () => {
  test("verify correct robots.txt is served for user-agent", async ({
    request,
  }) => {
    const response = await request.get("/robots.txt", {
      headers: {
        "User-Agent": "DG_JUSTICE_CRAWLER",
      },
    });

    expect(response.ok()).toBeTruthy();
    const body = await response.text();
    expect(body).toContain("DG_JUSTICE_CRAWLER");
  });

  test("verify sitemap route returns content", async ({ request }) => {
    const response = await request.get(
      "/v1/eclicrawler/2026/08/20/sitemap_1.xml",
    );

    expect(response.ok()).toBeTruthy();
    const body = await response.text();
    expect(body.length).toBeGreaterThan(0);
  });
});

test.describe("find and display administrative directives", () => {
  test("open administrative directives from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox");
    await searchInput.fill("Leistungsanspruch in der Dreiwochenfrist");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("radio", { name: "Verwaltungsvorschrift" }).click();
    await page
      .getByRole("link", {
        name: "Leistungsanspruch in der Dreiwochenfrist",
        exact: true,
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Leistungsanspruch in der Dreiwochenfrist",
      }),
    ).toBeVisible();
  });

  test("open administrative directives directly (server side rendered)", async ({
    page,
  }) => {
    await page.goto("/verwaltungsregelungen/KSNR132460020");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Leistungsanspruch in der Dreiwochenfrist",
      }),
    ).toBeVisible();
  });
});
