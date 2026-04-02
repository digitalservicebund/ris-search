import { expect, navigate, test } from "./utils/fixtures";

test.describe("view norm article page", () => {
  test.setTimeout(60000);

  test("can navigate to a single norm article and between articles", async ({
    page,
  }) => {
    const mainExpressionEliUrl =
      "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
    await navigate(page, mainExpressionEliUrl);

    await test.step("Navigate from main norm view to a single article", async () => {
      await page.getByRole("link", { name: "§ 1 Anwendungsbereich" }).click();
      await page.waitForURL(`${mainExpressionEliUrl}/art-z1`, {
        waitUntil: "commit",
      });
      const heading = page.getByRole("heading", {
        level: 1,
        name: "§ 1 Anwendungsbereich",
      });
      await expect.soft(heading).toBeVisible();
    });

    await test.step("Navigate between single articles", async () => {
      await page.getByRole("link", { name: "Nächster Paragraf" }).click();
      await page.waitForURL(new RegExp("/art-z2$"), { waitUntil: "commit" });
      const h2Art2 = page
        .getByRole("main")
        .getByRole("heading", { name: /§\s*2\s+Zutaten/i })
        .first();
      await h2Art2.scrollIntoViewIfNeeded();
      await expect(h2Art2).toBeVisible();
      await expect(
        page.getByRole("heading", {
          name: "§ 2 Zutaten, Herstellungsanforderungen",
        }),
      ).toBeVisible();

      await page.getByRole("link", { name: "Nächster Paragraf" }).click();
      await page.waitForURL(new RegExp("/art-z3$"), { waitUntil: "commit" });
      const h2Art3 = page
        .getByRole("main")
        .getByRole("heading", { name: /§\s*3\s+Kennzeichnung/i })
        .first();
      await page.evaluate(
        (element) => element?.scrollIntoView({ block: "center" }),
        await h2Art3.elementHandle(),
      );
      await expect(h2Art3).toBeVisible();
      await expect(
        page.getByRole("heading", {
          level: 1,
          name: "§ 3 Kennzeichnung",
        }),
      ).toBeVisible();
    });

    await test.step("Navigate back between single articles", async () => {
      const expressionEliUrl =
        "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
      await page.goto(`${expressionEliUrl}/art-z3`, {
        waitUntil: "commit",
      });
      const h2Art3Back = page
        .getByRole("main")
        .getByRole("heading", { name: /§\s*3\s+Kennzeichnung/i })
        .first();
      await h2Art3Back.scrollIntoViewIfNeeded();
      await expect(h2Art3Back).toBeVisible();
      await expect(
        page.getByRole("heading", {
          level: 1,
          name: "§ 3 Kennzeichnung",
        }),
      ).toBeVisible();

      await page.getByRole("link", { name: "Vorheriger Paragraf" }).click();
      await page.waitForURL(new RegExp("/art-z2$"), { waitUntil: "commit" });
      const h2Art2Back = page
        .getByRole("main")
        .getByRole("heading", { name: /§\s*2\s+Zutaten/i })
        .first();
      await h2Art2Back.scrollIntoViewIfNeeded();
      await expect(h2Art2Back).toBeVisible();
      await expect(
        page.getByRole("heading", {
          name: "§ 2 Zutaten, Herstellungsanforderungen",
        }),
      ).toBeVisible();
    });

    await test.step("Navigate back to main norm view", async () => {
      await page.getByRole("link", { name: "FrSaftErfrischV" }).first().click();
      await page.waitForURL(mainExpressionEliUrl, { waitUntil: "commit" });
    });
  });

  test("can navigate to and view an attachment", async ({ page }) => {
    const mainExpressionEliUrl =
      "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
    await navigate(page, mainExpressionEliUrl);

    const table = page.getByRole("table");
    const cell = table.getByRole("columnheader", {
      name: "Produktionsanforderungen",
    });
    await expect(cell).toBeVisible();

    await test.step("Navigate from main norm view to a single article", async () => {
      const attachmentTitle =
        "Anlage T1 (zu § 4 Absatz 2, § 5 Absatz 1 bis 3, § 6 Absatz 2 bis 4 und § 8)";

      const link = page
        .getByRole("main")
        .getByRole("link", { name: attachmentTitle })
        .first();

      await expect(
        page.getByRole("heading", { name: attachmentTitle }).first(),
      ).toBeVisible();
      await link.click({ force: true });
      await page.waitForURL(new RegExp("anlagen-n1_anlage-n1$"), {
        waitUntil: "commit",
        timeout: 15000,
      });
      await expect(cell).toBeVisible();
    });

    await test.step("Navigate back to main norm view", async () => {
      await page.getByRole("link", { name: "FrSaftErfrischV" }).first().click();
      await page.waitForURL(mainExpressionEliUrl, { waitUntil: "commit" });
    });
  });

  test("can view images", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

    await expect(page.getByRole("img", { name: "Beispielbild" })).toBeVisible();

    await test.step("in a single article", async () => {
      await page
        .getByRole("main")
        .getByRole("link", { name: "§ 1 Beispielhafte Illustration" })
        .first()
        .click();
      await page.waitForURL(/\/art-z1$/g, { waitUntil: "commit" });
      await expect(
        page.getByRole("img", { name: "Beispielbild" }),
      ).toBeVisible();
    });
  });

  test("table of contents renders on article page", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);

    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu/art-z1",
    );

    const tocNav = page.getByRole("navigation", { name: "Inhalte" });
    await expect(tocNav).toBeVisible();

    const selectedItem = tocNav.getByRole("treeitem", {
      name: /§\s*1,/i,
    });
    await expect(selectedItem).toHaveAttribute("aria-selected", "true");
  });

  test("clicking Eingangsformel heading link navigates to Eingangsformel article", async ({
    page,
  }) => {
    const normUrl = "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu";
    await navigate(page, normUrl);

    // The Eingangsformel heading in the main content is wrapped in a link to the article page
    await page
      .getByRole("main")
      .getByRole("link", { name: "Eingangsformel" })
      .first()
      .click();

    await page.waitForURL(
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/pr%C3%A4ambel-n1_formel-n1",
      { waitUntil: "commit" },
    );

    await expect(
      page.getByRole("heading", { level: 1, name: "Eingangsformel" }),
    ).toBeVisible();
  });

  test("clicking Nächster Paragraf on Eingangsformel article navigates to next article", async ({
    page,
  }) => {
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/pr%C3%A4ambel-n1_formel-n1",
    );

    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
      { waitUntil: "commit" },
    );

    await expect(
      page.getByRole("heading", {
        level: 1,
        name: "Art 1 Fiktive Bestimmungen zur Einführung",
      }),
    ).toBeVisible();
  });

  test("clicking Eingangsformel in TOC on art-z1 page navigates to Eingangsformel article", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/art-z1",
    );

    const tocNav = page.getByRole("navigation", { name: "Inhalte" });
    await tocNav.getByRole("link", { name: "Eingangsformel" }).click();

    await page.waitForURL(
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/pr%C3%A4ambel-n1_formel-n1",
      { waitUntil: "commit" },
    );

    await expect(
      page.getByRole("heading", { level: 1, name: "Eingangsformel" }),
    ).toBeVisible();
  });

  test("clicking the TOC subheading navigates back to the parent norm expression", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    const expressionEliUrl =
      "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
    await navigate(page, `${expressionEliUrl}/art-z1`);

    const tocNav = page.getByRole("navigation", { name: "Inhalte" });
    await expect(tocNav).toBeVisible();

    const subheadingLink = tocNav.getByRole("link", {
      name: /FrSaftErfrischV/i,
    });
    await expect(subheadingLink).toBeVisible();

    await subheadingLink.click();
    await expect(page).toHaveURL(expressionEliUrl);
  });
});

test.describe("can view metadata of norm articles", () => {
  test("can view full set of metadata in a single article when private Features enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2025/130/2025-05-05/1/deu/art-z1",
    );
    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText(["Gültig ab", "06.05.2025", "Gültig bis", "31.03.2037"]);
  });

  test("shows no metadata on a single article when private Features disabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(privateFeaturesEnabled);

    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2025/130/2025-05-05/1/deu/art-z1",
    );

    await expect(page.getByTestId("metadata-list")).not.toBeVisible();
  });
});

test("sets up meta tags for article page", async ({ page }) => {
  await navigate(
    page,
    "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/art-z1",
  );

  const title = await page.title();
  expect(title).toContain("§ 1 Anwendungsbereich");

  const canonicalLink = await page
    .locator('link[rel="canonical"]')
    .getAttribute("href");
  expect(canonicalLink).toMatch(/\/norms\/eli\/bund\//);

  const metaDescription = await page
    .locator('meta[name="description"]')
    .getAttribute("content");
  expect(metaDescription).toBe(
    "Die in Anlage 1 aufgeführten Erzeugnisse unterliegen dieser Verordnung, soweit sie zum gewerbsmäßigen Vertrieb bestimmt sind.",
  );

  const ogType = await page
    .locator('meta[property="og:type"]')
    .getAttribute("content");
  expect(ogType).toBe("article");

  const ogTitle = await page
    .locator('meta[property="og:title"]')
    .getAttribute("content");
  expect(ogTitle).toContain("§ 1 Anwendungsbereich");

  const ogDescription = await page
    .locator('meta[property="og:description"]')
    .getAttribute("content");
  expect(ogDescription).toBe(
    "Die in Anlage 1 aufgeführten Erzeugnisse unterliegen dieser Verordnung, soweit sie zum gewerbsmäßigen Vertrieb bestimmt sind.",
  );

  const ogUrl = await page
    .locator('meta[property="og:url"]')
    .getAttribute("content");
  expect(ogUrl).toMatch(/\/norms\/eli\/bund\//);

  const twitterTitle = await page
    .locator('meta[name="twitter:title"]')
    .getAttribute("content");
  expect(twitterTitle).toContain("§ 1 Anwendungsbereich");

  const twitterDescription = await page
    .locator('meta[name="twitter:description"]')
    .getAttribute("content");
  expect(twitterDescription).toBe(
    "Die in Anlage 1 aufgeführten Erzeugnisse unterliegen dieser Verordnung, soweit sie zum gewerbsmäßigen Vertrieb bestimmt sind.",
  );
});
