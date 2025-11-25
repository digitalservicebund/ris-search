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
  await expect(page.getByLabel("Dokumenttyp")).toHaveText("Auf");
  await expect(page.getByLabel("Fundstelle")).toHaveText(
    "FooBar, 1982, 123-123",
  );
  await expect(page.getByLabel("Autor")).toHaveText("Sabine Musterfrau");
  await expect(page.getByLabel("Veröffentlichungsjahr")).toHaveText("2024");

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
      name: "Dieser Beitrag zitiert",
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

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000001");
  await expect(page.getByRole("link", { name: "Details" })).toBeVisible();

  await page.getByRole("link", { name: "Details" }).first().click();

  await expect(page).toHaveURL(/#details$/);

  const detailsRegion = page.getByRole("region", { name: "Details" });
  await expect(
    detailsRegion.getByRole("heading", { name: "Details" }),
  ).toBeVisible();
});

test("shows detailed information in the 'Details' tab", async ({ page }) => {
  await navigate(page, "/literature/XXLU000000001");

  const detailsLink = page.getByRole("link", {
    name: "Details",
  });
  await detailsLink.click();

  const detailsRegion = page.getByRole("region", { name: "Details" });

  await expect(
    detailsRegion.getByRole("heading", { name: "Details" }),
  ).toBeVisible();
  await expect(detailsRegion.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );
  await expect(detailsRegion.getByLabel("Norm:")).toContainText(
    "BMV-Ä, GG, Art 6 Abs 2 S 1, 1949-05-23",
  );
  await expect(detailsRegion.getByLabel("Mitarbeiter:")).toContainText(
    "Peter Foo",
  );
  await expect(detailsRegion.getByLabel("Urheber:")).toContainText("DGB");
  await expect(detailsRegion.getByLabel("Sprache:")).toContainText("deu");
  await expect(detailsRegion.getByLabel("Kongress:")).toContainText(
    "Internationaler Kongreß für das Recht, 1991, Athen, GRC",
  );
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

    const button = page.getByRole("link", {
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

    const button = isMobileTest
      ? page.getByRole("menuitem", { name: "Drucken" })
      : page.getByRole("button", {
          name: "Drucken",
        });

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
    const button = isMobileTest
      ? page.getByText("Als PDF speichern")
      : page.getByRole("button", {
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
    const button = page.getByRole("link", {
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

// Skipped because of a client/SSR rendering mismatch, will be added again once
// that is fixed (see daily discussion from Nov 4th 2025)
test.skip("hides tabs and shows details if document is empty", async ({
  page,
}) => {
  await navigate(page, "/literature/XXLU000000005");

  await expect(
    page.getByRole("navigation", {
      name: "Ansichten des Literaturnachweises",
    }),
  ).not.toBeVisible();

  await expect(
    page.getByRole("heading", { level: 2, name: "Details" }),
  ).toBeVisible();

  await expect(page.getByRole("main").getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  await expect(page.getByLabel("Norm:")).toContainText("nicht vorhanden");
  await expect(page.getByLabel("Mitarbeiter:")).toContainText(
    "nicht vorhanden",
  );
  await expect(page.getByLabel("Urheber:")).toContainText("nicht vorhanden");
  await expect(page.getByLabel("Sprache:")).toContainText("deu");
  await expect(page.getByLabel("Kongress:")).toContainText("nicht vorhanden");
});
