import { expect, test, navigate, noJsTest } from "./utils/fixtures";

test("displays administrative directive page with metadata and text tab by default", async ({
  page,
}) => {
  await navigate(page, "/administrative-directives/KSNR000000001");

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
  await navigate(page, "/administrative-directives/KSNR000000001");

  await page.getByRole("link", { name: "Verwaltungsvorschriften" }).click();
  await page.waitForURL("**/search?category=V");

  await expect(
    page.getByRole("heading", { level: 1, name: "Suche" }),
  ).toBeVisible();
});

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await navigate(page, "/administrative-directives/KSNR000000001");
  await expect(page.getByRole("link", { name: "Details" })).toBeVisible();

  await page.getByRole("link", { name: "Details" }).first().click();

  await expect(page).toHaveURL(/#details$/);

  const detailsRegion = page.getByRole("region", { name: "Details" });
  await expect(
    detailsRegion.getByRole("heading", { name: "Details" }),
  ).toBeVisible();
});

test("shows detailed information in the 'Details' tab", async ({ page }) => {
  await navigate(page, "/administrative-directives/KSNR000000001");

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

  const detailsList = page.getByTestId("details-list");
  await expect(
    detailsList.getByRole("term").or(detailsList.getByRole("definition")),
  ).toHaveText([
    "Fundstelle:",
    "FooBar 2025, Nr 1, 123",
    "Zitierdatum:",
    "01.06.2025",
    "Gültig bis:",
    "01.07.2030",
    "Dokumenttyp Zusatz:",
    "Bekanntmachung",
    "Normen:",
    "Baz § 16c Abs 2, Lol § 15 Abs 2",
  ]);
});

test("hides tabs and shows details if document is empty", async ({ page }) => {
  await navigate(page, "/administrative-directives/KSNR000000004");

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
    "Fundstelle:",
    "BazAbCd 2002, Nr 1",
    "Zitierdaten:",
    "24.12.2012, 28.06.2013",
    "Gültig bis:",
    "nicht vorhanden",
    "Dokumenttyp Zusatz:",
    "nicht vorhanden",
    "Norm:",
    "nicht vorhanden",
  ]);
});
