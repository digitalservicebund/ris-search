import { expect, navigate, test } from "./utils/fixtures";

const expectedVersionData = [
  {
    dateFrom: "04.08.2919",
    dateTo: "-",
    status: "Zukünftig in Kraft",
  },
  {
    dateFrom: "04.08.2022",
    dateTo: "01.01.2030",
    status: "Aktuell gültig",
  },
  {
    dateFrom: "04.08.2020",
    dateTo: "03.08.2022",
    status: "Außer Kraft",
  },
];

test.beforeAll(async ({ privateFeaturesEnabled }) => {
  test.skip(
    !privateFeaturesEnabled,
    "This feature is not available for public",
  );
});

test("can browse different Fassungen of a norm", async ({ page }) => {
  await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu");

  await page.getByRole("tab", { name: "Fassungen" }).click();

  const tableBodyLocator = page.getByRole("table").getByRole("rowgroup").nth(1);

  for (const [index, expectedRowData] of expectedVersionData.entries()) {
    const cellLocator = tableBodyLocator
      .getByRole("row")
      .nth(index)
      .getByRole("cell");
    await expect(cellLocator.nth(0)).toContainText(expectedRowData.dateFrom);
    await expect(cellLocator.nth(1)).toContainText(expectedRowData.dateTo);
    await expect(cellLocator.nth(2)).toContainText(expectedRowData.status);
  }
});

test("can navigate to a Fassung by clicking the table row", async ({
  page,
}) => {
  await navigate(page, "/norms/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu");

  await expect(
    page.getByRole("heading", {
      name: "Zum Testen von Fassungen - Aktuelle Fassung",
    }),
  ).toBeVisible();

  await page.getByRole("tab", { name: "Fassungen" }).click();

  await test.step("Navigate to future fassung", async () => {
    const tableBodyLocator = page
      .getByRole("table")
      .getByRole("rowgroup")
      .nth(1);
    await tableBodyLocator.getByRole("row").nth(0).click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Zukünftige Fassung",
      }),
    ).toBeVisible();
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
      "—",
      "Status",
      "Aktuell gültig",
      "Gültig ab",
      "04.08.2022",
      "Gültig bis",
      "01.01.2030",
    ]);
  });

  test("on historic norm", async ({ page }) => {
    await navigate(page, "norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "—",
      "Status",
      "Außer Kraft",
      "Gültig ab",
      "04.08.2020",
      "Gültig bis",
      "03.08.2022",
    ]);
  });

  test("on future norm", async ({ page }) => {
    await navigate(page, "norms/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu");

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "—",
      "Status",
      "Zukünftig in Kraft",
      "Gültig ab",
      "04.08.2919",
      "Gültig bis",
      "—",
    ]);
  });
});
