import { test } from "@playwright/test";
import { expect } from "./fixtures";

const expectedVersionData = [
  {
    dateFrom: "04.08.2919",
    dateTo: "-",
    status: "Zukünftig in Kraft",
  },
  {
    dateFrom: "04.08.2022",
    dateTo: "-",
    status: "Aktuell gültig",
  },
  {
    dateFrom: "04.08.2020",
    dateTo: "03.08.2022",
    status: "Außer Kraft",
  },
];

test("can browse different Fassungen of a norm", async ({ page }) => {
  await page.goto(
    "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/regelungstext-1",
    { waitUntil: "networkidle" },
  );

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
