import { expect, test, noJsTest } from "./utils/fixtures";

// Skipped because of a client/SSR rendering mismatch, will be added again once
// that is fixed (see daily discussion from Nov 4th 2025)
test.skip("displays literature page with metadata and text tab by default", async ({
  page,
}) => {
  await page.goto("/literature/XXLU000000001");

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
  await expect(page.getByLabel("Author")).toHaveText("Sabine Musterfrau");
  await expect(page.getByLabel("Veröffentlichungsjahr")).toHaveText("2024");

  const textSection = page.getByRole("region", { name: "Text" });
  await expect(textSection.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  await expect(
    textSection.getByRole("heading", { name: "Gliederung" }),
  ).toBeVisible();
  await expect(textSection.getByText("I. Problemstellung.")).toBeVisible();
  await expect(textSection.getByText("II. Lösung.")).toBeVisible();
  await expect(textSection.getByText("III. Zusammenfassung.")).toBeVisible();

  await expect(
    textSection.getByRole("heading", { name: "Kurzreferat" }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Dies ist ein einfaches Test-Dokument."),
  ).toBeVisible();
  await expect(textSection.getByText("In sem neque")).toBeVisible();
});

test("displays all titles", async ({ page }) => {
  await page.goto("/literature/XXLU000000002");

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
  await page.goto("/literature/XXLU000000001", { waitUntil: "networkidle" });
  await expect(
    page.getByRole("link", { name: "Details zum Literaturnachweis" }),
  ).toBeVisible();

  await page
    .getByRole("link", { name: "Details zum Literaturnachweis" })
    .first()
    .click();

  await expect(page).toHaveURL(/#details$/);

  const detailsRegion = page.getByRole("region", { name: "Details" });
  await expect(
    detailsRegion.getByRole("heading", { name: "Details" }),
  ).toBeVisible();
});

// Skipped because of a client/SSR rendering mismatch, will be added again once
// that is fixed (see daily discussion from Nov 4th 2025)
test.skip("shows detailed information in the 'Details' tab", async ({
  page,
}) => {
  await page.goto("/literature/XXLU000000001");
  await page.waitForLoadState("networkidle");

  const detailsLink = page.getByRole("link", {
    name: "Details zum Literaturnachweis",
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
    await page.goto("/literature/XXLU000000001", {
      waitUntil: "networkidle",
    });

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
      ).toBeVisible();
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
    await page.goto("/literature/XXLU000000001", {
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
    await page.goto("/literature/XXLU000000001", {
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

  test("can use XML action to view literature xml file", async ({
    page,
    isMobileTest,
  }) => {
    await page.goto("/literature/XXLU000000001", {
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

    await page.waitForURL("v1/literature/XXLU000000001.xml");
  });
});

test("hides tabs and shows details only if document is empty", async ({
  page,
}) => {
  await page.goto("/literature/XXLU000000005");
  await page.waitForLoadState("networkidle");

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
