import { expect, test } from "./utils/fixtures";

const expectedNorms = [
  "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
];

test("can navigate to a single norm article and between articles", async ({
  page,
}) => {
  const mainExpressionEliUrl =
    "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
  await page.goto(mainExpressionEliUrl);

  await test.step("Navigate from main norm view to a single article", async () => {
    await page.getByRole("link", { name: "§ 1 Anwendungsbereich" }).click();
    await page.waitForURL(mainExpressionEliUrl + "/art-z1");
    await expect(
      page.getByRole("heading", { name: "§ 1 Anwendungsbereich" }),
    ).toBeVisible();
  });

  await test.step("Navigate between single articles", async () => {
    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(mainExpressionEliUrl + "/art-z2");
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
    await page.waitForURL(mainExpressionEliUrl + "/art-z3");
    const h2Art3 = page
      .getByRole("main")
      .getByRole("heading", { name: /§\s*3\s+Kennzeichnung/i })
      .first();
    await h2Art3.scrollIntoViewIfNeeded();
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
    await page.goto(mainExpressionEliUrl + "/art-z3");
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
    await page.waitForURL(mainExpressionEliUrl + "/art-z2");
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
    await page.waitForURL(mainExpressionEliUrl);
  });

  await test.step("View footnotes in title", async () => {
    const marker = await page.getByRole("superscript").innerText();
    expect(marker).toBe("❃");

    const footnotes = page.locator(".dokumentenkopf-fussnoten");
    const footnotesContent = footnotes.locator("ol");
    await expect(footnotesContent).not.toBeVisible();

    const expandButton = page.getByRole("button", { name: "Fußnote anzeigen" });

    await expect
      .poll(
        async () => {
          await expandButton.click();
          return footnotesContent.isVisible();
        },
        { message: "wait for the expand button to become interactive" },
      )
      .toBeTruthy();

    await expect(footnotes).toContainText(
      "(Diese Fußnote im Titel wurde im End-to-end-Datenbestand ergänzt",
    );

    await page.getByRole("button", { name: "Fußnote ausblenden" }).click();
    await expect(footnotesContent).not.toBeVisible();
  });
});

test("tabs work without JavaScript", async ({ browser }) => {
  const context = await browser.newContext({ javaScriptEnabled: false });
  const page = await context.newPage();

  await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
    waitUntil: "networkidle",
  });

  await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();
  await expect(page.getByRole("heading", { name: "Fassungen" })).toBeVisible();

  await test.step("Navigate to Details tab", async () => {
    await page.getByRole("link", { name: "Details des Gesetzes" }).click();
    await expect(page).toHaveURL(/#details$/);
  });

  await test.step("Navigate to Fassungen tab", async () => {
    await page.getByRole("link", { name: "Fassungen des Gesetzes" }).click();
    await expect(page).toHaveURL(/#versions$/);
  });

  await test.step("Navigate back to Text tab", async () => {
    await page.getByRole("link", { name: "Text des Gesetzes" }).click();
    await expect(page).toHaveURL(/#text$/);
  });

  await context.close();
});

test("can navigate to and view an attachment", async ({ page }) => {
  const mainExpressionEliUrl =
    "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
  await page.goto(mainExpressionEliUrl);

  const table = page.getByRole("table");
  const cell = table.getByRole("cell", { name: "Produktionsanforderungen" });
  await expect(cell).toBeVisible();

  await test.step("Navigate from main norm view to a single article", async () => {
    const attachmentTitle =
      "Anlage T1 (zu § 4 Absatz 2, § 5 Absatz 1 bis 3, § 6 Absatz 2 bis 4 und § 8)";

    await page
      .getByRole("main")
      .getByRole("link", { name: attachmentTitle })
      .first()
      .click();

    await expect(
      page.getByRole("heading", { name: attachmentTitle }),
    ).toBeVisible();
    await page.waitForURL(mainExpressionEliUrl + "/anlagen-n1_anlage-n1");
    await expect(cell).toBeVisible();
  });

  await test.step("Navigate back to main norm view", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();
    await page.waitForURL(mainExpressionEliUrl);
  });
});

test("can view images", async ({ page }) => {
  await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu");

  await page.getByRole("img", { name: "Beispielbild" }).isVisible();

  await test.step("in a single article", async () => {
    await page
      .getByRole("main")
      .getByRole("link", { name: "§ 1 Beispielhafte Illustration" })
      .first()
      .click();
    await page.waitForURL(/\/art-z1$/g);
    await page.getByRole("img", { name: "Beispielbild" }).isVisible();
  });
});

test.describe("shows link to translation if exists", () => {
  test("has link to translation", async ({ page, isMobileTest }) => {
    await page.goto("norms/eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu", {
      waitUntil: "networkidle",
    });
    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    const translationButton = page.getByRole("link", {
      name: "Zur englischen Übersetzung",
    });
    translationButton.click();
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
    await page.goto("/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");
    await page.waitForLoadState("networkidle");
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
        await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
          waitUntil: "networkidle",
        });

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

        const button = page.getByRole("link", {
          name: testCase.linkText,
        });

        await button.isVisible();

        if (!isMobileTest) {
          await button.hover();

          await expect(
            page.getByRole("tooltip", {
              name: testCase.linkText,
            }),
          ).toBeVisible();
        }

        let clipboardContents: string;

        await test.step("can copy the link", async () => {
          await button.click();
          if (!isMobileTest)
            await expect(page.getByText("Kopiert!")).toBeVisible();
          if (browserName === "chromium") {
            clipboardContents = await page.evaluate(() => {
              return navigator.clipboard.readText();
            });
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
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();

    const button = isMobileTest
      ? page.getByRole("menuitem", { name: "Drucken" })
      : page.getByRole("button", {
          name: "Drucken",
        });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Drucken" }),
      ).toBeVisible();
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
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = isMobileTest
      ? page.getByText("Als PDF speichern")
      : page.getByRole("button", {
          name: "Als PDF speichern",
        });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Als PDF speichern" }),
      ).toBeVisible();
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use XML action to view norms xml file", async ({
    page,
    isMobileTest,
  }) => {
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });

    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("link", {
      name: "XML anzeigen",
    });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "XML anzeigen" }),
      ).toBeVisible();
    }

    await button.click();

    await page.waitForURL(
      "v1/legislation/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/2024-12-19/regelungstext-1.xml",
    );
  });
});
