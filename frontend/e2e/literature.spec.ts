import { expect, test, noJsTest, navigate } from "./utils/fixtures";

test("displays literature page with metadata and text tab by default", async ({
  page,
}) => {
  await navigate(page, "/literature/XXLU000000001");

  // Breadcrumb navigation
  const breadcrumb = page.getByRole("navigation", { name: "Pfadnavigation" });

  await expect(breadcrumb).toBeVisible();
  await expect(breadcrumb.getByRole("link")).toContainText([
    "Startseite",
    "Literaturnachweise",
  ]);

  // Main title
  await expect(breadcrumb.getByText("Erstes Test-Dokument ULI")).toBeVisible();

  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: "Erstes Test-Dokument ULI" })
      .first(),
  ).toBeVisible();

  // Metadata section
  await expect(
    page.getByRole("term").or(page.getByRole("definition")),
  ).toHaveText([
    "Dokumenttyp",
    "Auf",
    "Fundstelle",
    "FooBar, 1982, 123-123, SelbstFund, 1982, 123-123",
    "Autor",
    "Sabine Musterfrau",
    "Veröffentlichungsjahr",
    "2024",
  ]);

  // Text section
  const textSection = page.getByRole("region", { name: "Text" });
  await expect(textSection.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  await expect(
    textSection.getByRole("heading", { level: 2, name: "Gliederung" }),
  ).toBeVisible();
  await expect(textSection.getByText("I. Problemstellung.")).toBeVisible();
  await expect(textSection.getByText("II. Lösung.")).toBeVisible();
  await expect(textSection.getByText("III. Zusammenfassung.")).toBeVisible();

  await expect(
    textSection.getByRole("heading", { level: 2, name: "Kurzreferat" }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Dies ist ein einfaches Test-Dokument."),
  ).toBeVisible();
  await expect(textSection.getByText("In sem neque")).toBeVisible();

  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Dieser Beitrag zitiert",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByRole("heading", {
      level: 3,
      name: "Rechtsprechung",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByText(
      "Vergleiche aktiv EuGH 2. Kammer, 3. April 2008, Az: C-346/06",
    ),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche aktiv FooBar 1. Kammer, 3. April 2008"),
  ).toBeVisible();

  await expect(
    textSection
      .getByRole("heading", {
        level: 3,
        name: "Literaturnachweise",
      })
      .first(),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche aktiv Selbstständigeliterature 2025"),
  ).toBeVisible();

  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Dieser Beitrag wird zitiert",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByRole("heading", {
      level: 3,
      name: "Verwaltungsvorschriften",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche passiv NaNu 1. Kammer, 2009, Az: XY"),
  ).toBeVisible();

  await expect(
    textSection
      .getByRole("heading", {
        level: 3,
        name: "Literaturnachweise",
      })
      .nth(1),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche passiv Unselbstständigeliterature 2023"),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche passiv Unselbstständigeliterature 1989"),
  ).toBeVisible();
});

test("displays sli footnotes in text section", async ({ page }) => {
  await navigate(page, "/literature/XXLS000000001");

  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: "Test-Dokument SLI" })
      .first(),
  ).toBeVisible();

  const textSection = page.getByRole("region", { name: "Text" });
  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Fußnoten",
    }),
  ).toBeVisible();
  await expect(textSection.getByText("Eine Fußnote.")).toBeVisible();
  await expect(
    textSection.getByText("Dies ist eine andere längere Fußnote."),
  ).toBeVisible();
});

test("displays all titles", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000002");

  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: "Zweites Test-Dokument ULI" })
      .first(),
  ).toBeVisible();

  const textSection = page.getByRole("region", { name: "Text" });
  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Zusätzliche Titel",
    }),
  ).toBeVisible();

  await expect(textSection).toContainText("Dokumentarischer Titel");
  await expect(textSection).toContainText("Zusatz zum Haupttitel");
});

test("displays all sli titles", async ({ page }) => {
  await navigate(page, "/literature/XXLS000000001");

  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: "Test-Dokument SLI" })
      .first(),
  ).toBeVisible();

  const textSection = page.getByRole("region", { name: "Text" });
  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Zusätzliche Titel",
    }),
  ).toBeVisible();

  await expect(textSection).toContainText("Dokumentarischer Titel");
  await expect(textSection).toContainText("Zusatz zum Haupttitel");
  await expect(textSection).toContainText("Gesamttitel Bandbezeichnung");
  await expect(textSection).toContainText("Gesamttitel 2 Bandbezeichnung 2");
  await expect(textSection).toContainText("Titel Kurzform");
  await expect(textSection).toContainText("sonstiger Titel");
});

test("can navigate to search via breadcrumb", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000001");

  await page.getByRole("link", { name: "Literaturnachweise" }).click();
  await page.waitForURL("**/search?category=L");

  await expect(
    page.getByRole("heading", { level: 1, name: "Suche" }),
  ).toBeVisible();
});

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000001");

  await test.step("text", async () => {
    await expect(
      page.getByRole("heading", { name: "Kurzreferat" }),
    ).toBeVisible();

    await expect(
      page.getByRole("tab", { name: "Text", selected: true }),
    ).toBeVisible();
  });

  await test.step("details", async () => {
    await page.getByRole("tab", { name: "Details" }).click();

    await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();

    await expect(
      page.getByRole("tab", { name: "Details", selected: true }),
    ).toBeVisible();
  });
});

test("shows detailed information in the 'Details' tab", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000001");

  const detailsLink = page.getByRole("tab", {
    name: "Details",
  });
  await detailsLink.click();

  await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();
  await expect(page.getByRole("main").getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  const detailsList = page.getByTestId("details-list");
  await expect(
    detailsList.getByRole("term").or(detailsList.getByRole("definition")),
  ).toHaveText([
    "Normen:",
    "BMV-Ä, GG, Art 6 Abs 2 S 1, 1949-05-23",
    "Mitarbeiter:",
    "Peter Foo",
    "Urheber:",
    "DGB",
    "Sprache:",
    "deu",
    "Kongress:",
    "Internationaler Kongreß für das Recht, 1991, Athen, GRC",
  ]);
});

test("shows detailed information in the 'Details' tab of sli documents", async ({
  page,
}) => {
  await navigate(page, "/literature/XXLS000000001");

  const detailsLink = page.getByRole("tab", {
    name: "Details",
  });
  await detailsLink.click();

  await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();
  await expect(page.getByRole("main").getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  const detailsList = page.getByTestId("details-list");
  await expect(
    detailsList.getByRole("term").or(detailsList.getByRole("definition")),
  ).toHaveText([
    "Normen:",
    "BMV-Ä, GG, Art 6 Abs 2 S 1, 1949-05-23",
    "Bearbeiter:",
    "Foo Bearbeiter",
    "Mitarbeiter:",
    "Peter Foo",
    "Urheber:",
    "DGB",
    "Begründer:",
    "Foo Begruender",
    "Herausgeber:",
    "Mitarbeiter Eins",
    "Verlag:",
    "herausgeber institution",
    "Ausgabe:",
    "1. Auflage",
    "Bestellnummer:",
    "ISBN 3-XXXXX-XX-X",
    "Teilband:",
    "Teilband 1",
    "Teilband 2",
    "Sprache:",
    "deu",
    "Kongress:",
    "Internationaler Kongreß für das Recht, 1991, Athen, GRC",
    "Hochschule:",
    "Universität Foo",
  ]);
});

test.describe("actions menu", () => {
  test("can use 'copy link' button to copy url to clipboard", async ({
    page,
    browserName,
    context,
    isMobileTest,
  }) => {
    await navigate(page, "/literature/XXLU000000001");

    if (browserName === "chromium") {
      await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    }

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }

    const button = page.getByRole("menuitem", {
      name: "Link kopieren",
    });

    await button.isVisible();

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", {
          name: "Link kopieren",
        }),
      ).toBeVisible({
        timeout: 15000,
      });
    }

    await button.click();
    if (!isMobileTest) await expect(page.getByText("Kopiert!")).toBeVisible();
    if (browserName === "chromium") {
      const clipboardContents = await page.evaluate(() => {
        return navigator.clipboard.readText();
      });
      expect(clipboardContents.endsWith("/literature/XXLU000000001")).toBe(
        true,
      );
    }
  });

  test("can use 'print button' to open print menu", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/literature/XXLU000000001");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();

    const button = page.getByRole("menuitem", { name: "Drucken" });

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
    await navigate(page, "/literature/XXLU000000001");
    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("menuitem", {
      name: "Als PDF speichern",
    });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Als PDF speichern" }),
      ).toBeVisible({
        timeout: 15000,
      });
    }

    if (!isMobileTest) await expect(button).toBeDisabled();
  });

  test("can use XML action to view literature xml file", async ({
    page,
    isMobileTest,
  }) => {
    await navigate(page, "/literature/XXLU000000001");

    if (isMobileTest) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("menuitem", {
      name: "XML anzeigen",
    });

    if (!isMobileTest) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "XML anzeigen" }),
      ).toBeVisible({
        timeout: 15000,
      });
    }

    await button.click();
    await page.waitForURL(`v1/literature/XXLU000000001.xml`, {
      waitUntil: "commit",
    });
  });
});

test("hides tabs and shows details if document is empty", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000005");

  await expect(
    page.getByRole("navigation", {
      name: "Details",
    }),
  ).not.toBeVisible();

  await expect(
    page.getByRole("heading", { level: 2, name: "Details" }),
  ).toBeVisible();

  await expect(page.getByRole("main").getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  const detailsList = page.getByTestId("details-list");
  await expect(
    detailsList.getByRole("term").or(detailsList.getByRole("definition")),
  ).toHaveText([
    "Norm:",
    "nicht vorhanden",
    "Mitarbeiter:",
    "nicht vorhanden",
    "Urheber:",
    "nicht vorhanden",
    "Sprache:",
    "deu",
    "Kongress:",
    "nicht vorhanden",
  ]);
});

test("displays references", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000009");

  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: "Lit Ausschließlich Verweise" })
      .first(),
  ).toBeVisible();

  // Text section
  // Make sure the text section with references is displayed even when
  // references are the only "text" content
  const textSection = page.getByRole("region", { name: "Text" });

  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Dieser Beitrag zitiert",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByRole("heading", {
      level: 3,
      name: "Rechtsprechung",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche aktiv FooBar 1. Kammer, 3. April 2008"),
  ).toBeVisible();

  await expect(
    textSection.getByRole("heading", {
      level: 2,
      name: "Dieser Beitrag wird zitiert",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByRole("heading", {
      level: 3,
      name: "Verwaltungsvorschriften",
    }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Vergleiche passiv NaNu 1. Kammer, 2009, Az: XY"),
  ).toBeVisible();
});
