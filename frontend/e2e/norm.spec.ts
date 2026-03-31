import {
  testCopyLinkButton,
  testPdfButton,
  testPrintButton,
  testXmlButton,
} from "./utils/actionMenuHelper";
import { expect, navigate, noJsTest, test } from "./utils/fixtures";

test.describe("view norm page", async () => {
  test.setTimeout(60000);

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

  test("official table of contents is visible and expandable", async ({
    page,
  }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

    const button = page.getByRole("button", {
      name: "Amtliches Inhaltsverzeichnis einblenden",
    });

    await button.click();
    const toc = page.getByRole("region", {
      name: "Amtliches Inhaltsverzeichnis ausblenden",
    });
    expect(toc).toBeVisible();
    expect(toc.getByRole("listitem")).toBeVisible();
  });

  test("view footnotes in title", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

    const marker = await page.getByRole("superscript").innerText();
    expect(marker).toBe("❃");

    const footnotes = page.locator(".dokumentenkopf-fussnoten");

    await expect(footnotes).toContainText(
      "(Diese Fußnote im Titel wurde im End-to-end-Datenbestand ergänzt",
    );
  });

  test("table of contents renders and clicking a link scrolls to the article", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    const normUrl = "/norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu";

    await navigate(page, normUrl);

    const tocNav = page.getByRole("navigation", { name: "Inhalte" });
    await expect(tocNav).toBeVisible();

    const articleLink = tocNav.getByRole("link", { name: "§ 1" }).first();
    await articleLink.click();

    await expect(page).toHaveURL(/#art-z1$/);

    const targetHeading = page
      .getByRole("main")
      .getByRole("heading", { name: /§\s*1\s+Erlaubnis/i });

    await expect(targetHeading).toBeInViewport();
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

  test("clicking Eingangsformel in TOC scrolls to Eingangsformel section", async ({
    page,
    isMobileTest,
  }) => {
    test.skip(isMobileTest);
    const normUrl = "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu";
    await navigate(page, normUrl);

    const tocNav = page.getByRole("navigation", { name: "Inhalte" });
    await tocNav.getByRole("link", { name: "Eingangsformel" }).click();

    await expect(page).toHaveURL(
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu#praeambel-n1_formel-n1",
    );

    const heading = page
      .getByRole("main")
      .getByRole("heading", { name: "Eingangsformel" });
    await expect(heading).toBeInViewport();
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

test.describe("can view metadata of norms", () => {
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
});

test("sets up meta tags for norm page when private features are enabled", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(!privateFeaturesEnabled);
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
    "FrSaftErfrischV: Fassung vom 29.04.2023, Aktuell gültig",
  );

  const twitterTitle = await page
    .locator('meta[name="twitter:title"]')
    .getAttribute("content");
  expect(twitterTitle).toBe(
    "FrSaftErfrischV: Fassung vom 29.04.2023, Aktuell gültig",
  );
});

test("sets up meta tags for norm page when private features are disabled", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(privateFeaturesEnabled);
  await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

  const ogTitle = await page
    .locator('meta[property="og:title"]')
    .getAttribute("content");
  expect(ogTitle).toBe("FrSaftErfrischV");

  const twitterTitle = await page
    .locator('meta[name="twitter:title"]')
    .getAttribute("content");
  expect(twitterTitle).toBe("FrSaftErfrischV");
});
