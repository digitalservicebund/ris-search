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
