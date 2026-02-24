import {
  testCopyLinkButton,
  testPdfButton,
  testPrintButton,
  testXmlButton,
} from "./utils/actionMenuHelper";
import { expect, navigate, noJsTest, test } from "./utils/fixtures";

test.describe("view norm page", async () => {
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
      const mainExpressionEliUrl =
        "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
      await page.goto(`${mainExpressionEliUrl}/art-z3`, {
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

    await test.step("View footnotes in title", async () => {
      const marker = await page.getByRole("superscript").innerText();
      expect(marker).toBe("❃");

      const footnotes = page.locator(".dokumentenkopf-fussnoten");

      await expect(footnotes).toContainText(
        "(Diese Fußnote im Titel wurde im End-to-end-Datenbestand ergänzt",
      );
    });
  });

  noJsTest("tabs work without JavaScript", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

    await test.step("text", async () => {
      await expect(
        page.getByRole("heading", { name: "Text", exact: false }),
      ).toBeVisible();

      await expect(
        page.getByRole("tab", { name: "Text", selected: true }),
      ).toBeVisible();
    });

    await test.step("details", async () => {
      await page.getByRole("tab", { name: "Details" }).click();

      await expect(
        page.getByRole("heading", { name: "Details" }),
      ).toBeVisible();

      await expect(
        page.getByRole("tab", { name: "Details", selected: true }),
      ).toBeVisible();
    });

    await test.step("versions", async () => {
      await page.getByRole("tab", { name: "Fassungen" }).click();

      await expect(
        page.getByRole("tab", { name: "Fassungen", selected: true }),
      ).toBeVisible();
    });
  });

  test("shows detailed information in the Details tab", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

    await page.getByRole("tab", { name: "Details" }).click();

    await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();

    const detailsList = page
      .getByTestId("details-list")
      .getByRole("term")
      .or(page.getByTestId("details-list").getByRole("definition"));

    await expect(detailsList).toHaveText([
      "Ausfertigungsdatum:",
      "nicht vorhanden",
      "Vollzitat:",
      "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung vom 27. Mai 2000 (BGBl. I S. 1016), zuletzt modifiziert im Testverfahren",
      "Stand:",
      "Zuletzt geändert durch Testanpassungen",
      "Neugefasst durch Testdaten",
      "Hinweis zum Stand:",
      "nicht vorhanden",
      "Fußnoten:",
      /\*T \(\+{3} Textnachweis ab: 27\.5\.2000 \+{3}\).*\(\+{3} Zur Anwendung vgl\. §§ 5, 12, 15 \+{3}\)/,
      "Download:",
      /FrSaftErfrischV als ZIP herunterladen/,
    ]);
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

  test("scrolls to an article with encoded hash in URL", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    const normUrl = "/norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu";

    await navigate(page, normUrl);

    const articleLink = page.getByRole("treeitem").getByRole("link", {
      name: /§\s*18\s*bis\s*21/i,
    });

    await expect(articleLink).toBeVisible();
    await articleLink.click();

    await expect(page).toHaveURL(/#art-z.*18.*21/i);

    const targetHeading = page
      .getByRole("main")
      .getByRole("heading", {
        name: /§\s*18\s*bis\s*21/i,
      })
      .first();

    await expect(targetHeading).toBeVisible();
    await expect(targetHeading).toBeInViewport();
  });
});

test.describe("actions menu", () => {
  test.describe("can copy currently valid expression link", () => {
    testCopyLinkButton(
      "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu",
      "Link zur jeweils gültigen Fassung",
      RegExp(".*/norms/eli/bund/bgbl-1/2024/383"),
    );
  });

  test.describe("can copy permalink to currently viewed expression", () => {
    testCopyLinkButton(
      "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu",
      "Permalink zu dieser Fassung kopieren",
      RegExp(".*/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu"),
    );
  });

  test.describe("can use print action button to open print menu", () => {
    testPrintButton("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");
  });

  test.describe("can't use PDF action as it is disabled", () => {
    testPdfButton("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");
  });

  test.describe("can use XML action to view norms xml file", () => {
    testXmlButton(
      "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu",
      "http://localhost:8090/v1/legislation/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/2024-12-19/regelungstext-1.xml",
    );
  });

  test.describe("can navigate to english translation if available", () => {
    test("desktop", async ({ page, isMobileTest }) => {
      test.skip(isMobileTest);
      await navigate(
        page,
        "/norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu",
      );

      const button = page.getByRole("menuitem", {
        name: "Zur englischen Übersetzung",
      });

      await button.hover();
      await expect(
        page.getByRole("tooltip", { name: "Zur englischen Übersetzung" }),
      ).toBeVisible({
        timeout: 15000,
      });

      await button.click();
      await expect(page.getByRole("heading", { level: 1 })).toHaveText(
        "Test Regulation for the Model Framework of the Public Service",
      );
    });

    test("mobile", async ({ page, isMobileTest }) => {
      test.skip(!isMobileTest);
      await navigate(
        page,
        "/norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu",
      );

      await page.getByLabel("Aktionen anzeigen").click();

      const button = page.getByRole("menuitem", {
        name: "Zur englischen Übersetzung",
      });

      await button.click();
      await expect(page.getByRole("heading", { level: 1 })).toHaveText(
        "Test Regulation for the Model Framework of the Public Service",
      );
    });
  });

  test.describe("does not show english translation link if no translation exists", () => {
    test("desktop", async ({ page, isMobileTest }) => {
      test.skip(isMobileTest);
      await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

      await expect(
        page.getByRole("menuitem", {
          name: "Zur englischen Übersetzung",
        }),
      ).toHaveCount(0);
    });

    test("mobile", async ({ page, isMobileTest }) => {
      test.skip(!isMobileTest);
      await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

      await page.getByLabel("Aktionen anzeigen").click();

      await expect(
        page.getByRole("menuitem", {
          name: "Zur englischen Übersetzung",
        }),
      ).toHaveCount(0);
    });
  });
});

test.describe("can view metadata of norms and articles", () => {
  test("can view full set of metadata when private Features enabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(page, "/norms/eli/bund/bgbl-1/2025/130/2025-05-05/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "GeGuGe 2025",
      "Status",
      "Aktuell gültig",
      "Gültig ab",
      "06.05.2025",
      "Gültig bis",
      "31.03.2037",
    ]);
  });

  test("can view reduced set of metadata when private Features are disabled", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(privateFeaturesEnabled);
    await navigate(page, "/norms/eli/bund/bgbl-1/2025/130/2025-05-05/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText(["Abkürzung", "GeGuGe 2025", "Status", "—"]);
  });

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

test("sets up meta tags for norm page", async ({
  page,
  privateFeaturesEnabled,
}) => {
  await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

  const title = await page.title();
  expect(title).toContain("FrSaftErfrischV");

  const canonicalLink = await page
    .locator('link[rel="canonical"]')
    .getAttribute("href");
  expect(canonicalLink).toMatch(/\/norms\/eli\/bund\//);

  const metaDescription = await page
    .locator('meta[name="description"]')
    .getAttribute("content");
  expect(metaDescription).toBe(
    "Fruchtsaft- und Erfrischungsgetränkeverordnung",
  );

  const ogType = await page
    .locator('meta[property="og:type"]')
    .getAttribute("content");
  expect(ogType).toBe("article");

  const ogDescription = await page
    .locator('meta[property="og:description"]')
    .getAttribute("content");
  expect(ogDescription).toBe("Fruchtsaft- und Erfrischungsgetränkeverordnung");

  const ogUrl = await page
    .locator('meta[property="og:url"]')
    .getAttribute("content");
  expect(ogUrl).toMatch(/\/norms\/eli\/bund\//);

  const twitterDescription = await page
    .locator('meta[name="twitter:description"]')
    .getAttribute("content");
  expect(twitterDescription).toBe(
    "Fruchtsaft- und Erfrischungsgetränkeverordnung",
  );

  const ogTitle = await page
    .locator('meta[property="og:title"]')
    .getAttribute("content");
  expect(ogTitle).toBe(
    privateFeaturesEnabled
      ? "FrSaftErfrischV: Fassung vom 29.04.2023, Aktuell gültig"
      : "FrSaftErfrischV",
  );

  const twitterTitle = await page
    .locator('meta[name="twitter:title"]')
    .getAttribute("content");
  expect(twitterTitle).toBe(
    privateFeaturesEnabled
      ? "FrSaftErfrischV: Fassung vom 29.04.2023, Aktuell gültig"
      : "FrSaftErfrischV",
  );
});
