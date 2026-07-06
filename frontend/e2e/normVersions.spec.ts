import { expect, navigate, test } from "./utils/fixtures";

test.beforeAll(async ({ privateFeaturesEnabled }) => {
  test.skip(
    !privateFeaturesEnabled,
    "This feature is not available for public",
  );
});

test.describe("fassungen tab", async () => {
  test("displays Fassungen table in the Fassungen tab", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu");

    await page.getByRole("tab", { name: "Fassungen" }).click();

    await expect(page.getByRole("table").getByRole("row")).toHaveText(
      [
        "Gültig ab Gültig bis Status",
        "04.08.2919 - Zukünftig in Kraft",
        "04.08.2022 01.01.2030 Aktuell gültig",
        "04.08.2020 03.08.2022 Außer Kraft",
      ],
      { useInnerText: true },
    );
  });

  test("can navigate to a Fassung by clicking the table row", async ({
    page,
  }) => {
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu?view=versions",
    );

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();

    await page
      .getByRole("row", { name: "04.08.2919 - Zukünftig in Kraft" })
      .click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Zukünftige Fassung",
      }),
    ).toBeVisible();
  });

  test("can filter Fassungen by date", async ({ page }) => {
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
    );

    const tableBody = page.getByRole("table").getByRole("rowgroup").nth(1);

    await expect(tableBody.getByRole("row")).toHaveCount(3);

    await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.2020");

    await expect(tableBody.getByRole("row")).toHaveCount(1);
    await expect(tableBody.getByRole("row")).toHaveText(
      "04.08.2020 03.08.2022 Außer Kraft",
      { useInnerText: true },
    );
  });

  test("shows no results placeholder when no Fassung found", async ({
    page,
  }) => {
    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
    );

    const tableBody = page.getByRole("table").getByRole("rowgroup").nth(1);

    await expect(tableBody.getByRole("row")).toHaveCount(3);

    await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.1536");

    await expect(tableBody.getByRole("row")).toHaveCount(1);
    await expect(tableBody.getByRole("row")).toHaveText(
      "Keine Ergebnisse gefunden",
    );
  });
});

test.describe("displays metadata correctly", async () => {
  test("currently valid norm", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTestAkt",
      "Status",
      "Aktuell gültig",
      "Gültig ab",
      "04.08.2022",
      "Gültig bis",
      "01.01.2030",
    ]);
  });

  test("on historic norm", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTestAlt",
      "Status",
      "Außer Kraft",
      "Gültig ab",
      "04.08.2020",
      "Gültig bis",
      "03.08.2022",
    ]);
  });

  test("on future norm", async ({ page }) => {
    await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTestZuk",
      "Status",
      "Zukünftig in Kraft",
      "Gültig ab",
      "04.08.2919",
      "Gültig bis",
      "—",
    ]);
  });
});

test.describe("future or historic version info", () => {
  test("shows an info about future versions on a historic norm article", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Sie lesen einen Paragrafen einer historischen Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });

  test("shows an info about previous versions on a future norm article", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(
      page,
      "/norms/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Sie lesen einen Paragrafen einer zukünftigen Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });
});

test("displays validity in breadcrumb navigation", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(!privateFeaturesEnabled);
  await navigate(page, "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

  const breadcrumb = page.getByRole("navigation", { name: "Pfadnavigation" });
  await expect(breadcrumb).toBeVisible();

  const breadcrumbLinks = breadcrumb.getByRole("listitem");
  await expect(breadcrumbLinks).toContainText([
    "Start",
    "", // Empty items are separators
    "Suche",
    "",
    "FrSaftErfrischV",
  ]);
});
