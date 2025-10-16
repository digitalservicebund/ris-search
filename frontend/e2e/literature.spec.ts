import { test } from "@playwright/test";
import { expect } from "./fixtures";

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
    "FooBar 1982, 123-123",
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
