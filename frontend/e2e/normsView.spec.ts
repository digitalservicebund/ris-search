import { expect, navigate, noJsTest, test } from "./utils/fixtures";

const expectedNorms = [
  "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
];

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
        name: "§ 1 Anwendungsbereich",
      });
      await page.waitForSelector("h1, h2", {
        state: "visible",
        timeout: 10000,
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
      await page.getByRole("link", { name: expectedNorms[0] }).first().click();
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

    expect(detailsList).toHaveText([
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
      "nicht vorhanden",
      "Download:",
      "FrSaftErfrischV als ZIP herunterladen",
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
      await page.getByRole("link", { name: expectedNorms[0] }).first().click();
      await page.waitForURL(mainExpressionEliUrl, { waitUntil: "commit" });
    });
  });

  test("can view images", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

    await page.getByRole("img", { name: "Beispielbild" }).isVisible();

    await test.step("in a single article", async () => {
      await page
        .getByRole("main")
        .getByRole("link", { name: "§ 1 Beispielhafte Illustration" })
        .first()
        .click();
      await page.waitForURL(/\/art-z1$/g, { waitUntil: "commit" });
      await page.getByRole("img", { name: "Beispielbild" }).isVisible();
    });
  });
});

test.describe("shows link to translation if exists", () => {
  test("has link to translation", async ({ page, isMobileTest }) => {
    await navigate(page, "norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu");

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    const translationButton = page.getByRole("link", {
      name: "Zur englischen Übersetzung",
    });
    await translationButton.click();
    await expect(
      page.getByRole("heading", {
        name: "Test Regulation for the Model Framework of the Public Service",
      }),
    ).toBeVisible();
  });

  test("if there is no translation, there is not link", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    await expect(
      page.getByRole("link", { name: "Zur englischen Übersetzung" }),
    ).toHaveCount(0);
  });
});

test.describe("actions menu", () => {
  const testCases = [
    {
      name: "can use link action button to copy link to currently valid expression",
      linkText: "Link zur jeweils gültigen Fassung",
      clipboardText: "/norms/eli/bund/bgbl-1/2024/383",
    },
    {
      name: "can use permalink action button to copy permalink to viewed expression",
      linkText: "Permalink zu dieser Fassung",
      clipboardText: "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu",
    },
  ];

  for (const testCase of testCases) {
    test(
      testCase.name,
      async ({ page, browserName, baseURL, context, isMobileTest }) => {
        await navigate(
          page,
          "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu",
        );

        if (browserName === "chromium") {
          const origin = baseURL
            ? new URL(baseURL).origin
            : new URL(page.url()).origin;
          await context.grantPermissions(
            ["clipboard-read", "clipboard-write"],
            {
              origin,
            },
          );
        }

        if (isMobileTest) {
          await page.getByLabel("Aktionen anzeigen").click();
        }

        const button = page.getByRole("link", { name: testCase.linkText });
        await button.isVisible();

        if (!isMobileTest) {
          await button.hover();
          await expect(
            page.getByRole("tooltip", { name: testCase.linkText }),
          ).toBeVisible({
            timeout: 15000,
          });
        }

        await test.step("can copy the link", async () => {
          await button.click();
          if (!isMobileTest) {
            await expect(page.getByText("Kopiert!")).toBeVisible();
          }
          if (browserName === "chromium") {
            const clipboardContents = await page.evaluate(() =>
              navigator.clipboard.readText(),
            );
            expect(clipboardContents.endsWith(testCase.clipboardText)).toBe(
              true,
            );
          }
        });
      },
    );
  }

  test("can use print action button to open print menu", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();

    const button = isMobileTest
      ? page.getByRole("menuitem", { name: "Drucken" })
      : page.getByRole("button", { name: "Drucken" });

    if (!isMobileTest) {
      await button.hover();
      await expect(page.getByRole("tooltip", { name: "Drucken" })).toBeVisible({
        timeout: 15000,
      });
    }

    await test.step("can open print menu", async () => {
      await page.evaluate(
        "(() => {window.waitForPrintDialog = new Promise(f => window.print = f);})()",
      );
      await button.click();
      await page.waitForFunction("window.waitForPrintDialog");
    });
  });

  test("can't use PDF action as it is disabled", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();

    const button = isMobileTest
      ? page.getByText("Als PDF speichern")
      : page.getByRole("button", { name: "Als PDF speichern" });

    if (!isMobileTest) {
      await button.hover();
      await expect(
        page.getByRole("tooltip", { name: "Als PDF speichern" }),
      ).toBeVisible({
        timeout: 15000,
      });
    }

    if (!isMobileTest) {
      await expect(button).toBeDisabled();
    }
  });

  test("can use XML action to view norms xml file", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("link", { name: "XML anzeigen" });

    if (!isMobileTest) {
      await button.hover();
      await expect(
        page.getByRole("tooltip", { name: "XML anzeigen" }),
      ).toBeVisible({
        timeout: 15000,
      });
    }

    await button.click();
    await page.waitForURL(
      `v1/legislation/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/2024-12-19/regelungstext-1.xml`,
      { waitUntil: "commit" },
    );
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
