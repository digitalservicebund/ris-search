import { expect, test, navigate } from "./utils/fixtures";

test("displays administrative directive page with metadata and text tab by default", async ({
  page,
}) => {
  await navigate(page, "/administrative-directive/KSNR000000001");

  // Breadcrumb navigation
  const breadcrumb = page.getByRole("navigation", { name: "Pfadnavigation" });

  await expect(breadcrumb).toBeVisible();
  await expect(breadcrumb.getByRole("link")).toContainText([
    "Startseite",
    "Verwaltungsvorschriften",
  ]);
  const expectedTitle =
    "Verwaltungsvorschrift für das Testen des Portals zur Darstellung von Verwaltungsvorschriften";
  await expect(breadcrumb.getByText(expectedTitle)).toBeVisible();

  // Main title
  await expect(
    page
      .getByRole("main")
      .getByRole("heading", { level: 1, name: expectedTitle })
      .first(),
  ).toBeVisible();

  // Metadata section
  await expect(
    page.getByRole("term").or(page.getByRole("definition")),
  ).toHaveText([
    "Aktenzeichen",
    "Foo - 123 - 4",
    "Normgeber",
    "DEU Neuris",
    "Dokumenttyp",
    "VR",
    "Gültig ab",
    "01.07.2025",
  ]);

  // Text section
  const textSection = page.getByRole("region", { name: "Text" });
  await expect(textSection.getByRole("alert")).toContainText(
    "Dieser Service befindet sich in der Testphase",
  );

  // Short report
  await expect(
    textSection.getByRole("heading", { level: 2, name: "Kurzreferat" }),
  ).toBeVisible();
  await expect(
    textSection.getByText("Dies ist ein Testdokument. Katze und Maus. "),
  ).toBeVisible();
  await expect(
    textSection.getByText("Lorem ipsum dolor sit amet"),
  ).toBeVisible();

  // Outline
  await expect(
    textSection.getByRole("heading", { level: 2, name: "Inhalt" }),
  ).toBeVisible();
  await expect(textSection.getByText("1. Allgemeines")).toBeVisible();
  await expect(textSection.getByText("2. Genaues")).toBeVisible();
  await expect(textSection.getByText("3. Unwichtiges")).toBeVisible();
  await expect(textSection.getByText("4. Sonstiges")).toBeVisible();
});

test("can navigate to search via breadcrumb", async ({ page }) => {
  await navigate(page, "/administrative-directive/KSNR000000001");

  await page.getByRole("link", { name: "Verwaltungsvorschriften" }).click();
  await page.waitForURL("**/search?category=V");

  await expect(
    page.getByRole("heading", { level: 1, name: "Suche" }),
  ).toBeVisible();
});
