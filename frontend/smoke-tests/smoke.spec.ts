import { expect, test } from "../e2e/utils/fixtures";

test("search from homepage", async ({ page }) => {
  await page.goto("/");

  await expect(
    page.getByRole("heading", {
      level: 1,
      name: "Rechtsinformationen des Bundes",
    }),
  ).toBeVisible();

  const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
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
  await expect(page.getByText(/[1-9]+ Suchergebnisse/)).toBeVisible();
});

test.describe("find and display norms", () => {
  test("open norm from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
    await searchInput.fill("Brennereiordnung");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("button", { name: "Gesetze & Verordnungen" }).click();
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
    await page.goto("/norms/eli/bund/banz-at/1922/s717/1922-09-12/1/deu");

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

    const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
    await searchInput.fill("Rechtmäßigkeit einer Sperrerklärung");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("button", { name: "Gerichtsentscheidungen" }).click();
    await page
      .getByRole("link", {
        name: "Rechtmäßigkeit einer Sperrerklärung",
        exact: true,
      })
      .click();

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Rechtmäßigkeit einer Sperrerklärung",
      }),
    ).toBeVisible();
  });

  test("open caselaw directly (server side rendered)", async ({ page }) => {
    await page.goto("/case-law/JURE100055708");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Rechtmäßigkeit einer Sperrerklärung",
      }),
    ).toBeVisible();
  });
});

test.describe("find and display literature", () => {
  test("open literature from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
    await searchInput.fill("Die Arbeitergilden der Gegenwart");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("button", { name: "Literaturnachweise" }).click();
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
    await page.goto("/literature/KSLS051342704");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Die Arbeitergilden der Gegenwart",
      }),
    ).toBeVisible();
  });
});

test.describe("find and display administrative directives", () => {
  test("open administrative directives from search results (client side rendered)", async ({
    page,
  }) => {
    await page.goto("/");

    const searchInput = page.getByRole("searchbox", { name: "Suchfeld" });
    await searchInput.fill("Leistungsanspruch in der Dreiwochenfrist");

    const searchButton = page.getByRole("button", { name: "Suchen" });
    await searchButton.click();

    await page.getByRole("button", { name: "Verwaltungsvorschrift" }).click();
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
    await page.goto("/administrative-directives/KSNR132460020");

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Leistungsanspruch in der Dreiwochenfrist",
      }),
    ).toBeVisible();
  });
});
