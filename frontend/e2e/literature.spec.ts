import { expect, test } from "./utils/fixtures";

test("displays literature page with metadata and text tab by default", async ({
  page,
}) => {
  await page.goto("/literature/TEST000000001");

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
    page.getByRole("heading", { name: "Erstes Test-Dokument ULI" }),
  ).toBeVisible();

  // Metadata section
  await expect(page.getByLabel("Dokumenttyp")).toHaveText("Auf");
  await expect(page.getByLabel("Fundstelle")).toHaveText(
    "FooBar, 1982, 123-123",
  );
  await expect(page.getByLabel("Author")).toHaveText("Sabine Musterfrau");
  await expect(page.getByLabel("Veröffentlichungsjahr")).toHaveText("2024");

  const tabpanel = page.getByRole("tabpanel");

  // Testphase alert should be shown
  await expect(tabpanel.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  await expect(
    tabpanel.getByRole("heading", { name: "Gliederung" }),
  ).toBeVisible();
  await expect(tabpanel.getByText("I. Problemstellung.")).toBeVisible();
  await expect(tabpanel.getByText("II. Lösung.")).toBeVisible();
  await expect(tabpanel.getByText("III. Zusammenfassung.")).toBeVisible();

  await expect(
    tabpanel.getByRole("heading", { name: "Kurzreferat" }),
  ).toBeVisible();
  await expect(
    tabpanel.getByText("Dies ist ein einfaches Test-Dokument."),
  ).toBeVisible();
  await expect(tabpanel.getByText("In sem neque")).toBeVisible();
});

test("shows detailed information in the 'Details' tab", async ({ page }) => {
  await page.goto("/literature/TEST000000001");
  await page.waitForLoadState("networkidle");

  const detailsTabButton = page.getByRole("tab", {
    name: "Details zum Literaturnachweis",
  });
  await detailsTabButton.click();

  const tabpanel = page.getByRole("tabpanel", {
    name: "Details zum Literaturnachweis",
  });

  await expect(
    tabpanel.getByRole("heading", { name: "Details" }),
  ).toBeVisible();
  await expect(tabpanel.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  await expect(tabpanel.getByLabel("Norm:")).toContainText(
    "BMV-Ä, GG, Art 6 Abs 2 S 1, 1949-05-23",
  );
  await expect(tabpanel.getByLabel("Mitarbeiter:")).toContainText("Peter Foo");
  await expect(tabpanel.getByLabel("Urheber:")).toContainText("DGB");
  await expect(tabpanel.getByLabel("Sprache:")).toContainText("deu");
  await expect(tabpanel.getByLabel("Kongress:")).toContainText(
    "Internationaler Kongreß für das Recht, 1991, Athen, GRC",
  );
});

test.describe("actions menu", () => {
  test("can use 'copy link' button to copy url to clipboard", async ({
    page,
    browserName,
    context,
    isMobile,
  }) => {
    await page.goto("/literature/TEST000000001", {
      waitUntil: "networkidle",
    });

    if (browserName === "chromium") {
      await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    }

    if (isMobile) {
      await page.getByLabel("Aktionen anzeigen").click();
    }

    const button = page.getByRole("link", {
      name: "Link kopieren",
    });

    await button.isVisible();

    if (!isMobile) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", {
          name: "Link kopieren",
        }),
      ).toBeVisible();
    }

    await button.click();
    if (!isMobile) await expect(page.getByText("Kopiert!")).toBeVisible();
    if (browserName === "chromium") {
      const clipboardContents = await page.evaluate(() => {
        return navigator.clipboard.readText();
      });
      expect(clipboardContents.endsWith("/literature/TEST000000001")).toBe(
        true,
      );
    }
  });

  test("can use 'print button' to open print menu", async ({
    page,
    isMobile,
  }) => {
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });
    if (isMobile) await page.getByLabel("Aktionen anzeigen").click();

    const button = isMobile
      ? page.getByRole("menuitem", { name: "Drucken" })
      : page.getByRole("button", {
          name: "Drucken",
        });

    if (!isMobile) {
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

  test("can't use PDF action as it is disabled", async ({ page, isMobile }) => {
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });
    if (isMobile) await page.getByLabel("Aktionen anzeigen").click();
    const button = isMobile
      ? page.getByText("Als PDF speichern")
      : page.getByRole("button", {
          name: "Als PDF speichern",
        });

    if (!isMobile) {
      await button.hover();

      await expect(
        page.getByRole("tooltip", { name: "Als PDF speichern" }),
      ).toBeVisible();
    }

    if (!isMobile) await expect(button).toBeDisabled();
  });

  test("can use XML action to view norms xml file", async ({
    page,
    isMobile,
  }) => {
    await page.goto("/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu", {
      waitUntil: "networkidle",
    });

    if (isMobile) await page.getByLabel("Aktionen anzeigen").click();
    const button = page.getByRole("link", {
      name: "XML anzeigen",
    });

    if (!isMobile) {
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
