import { test } from "@playwright/test";
import { expect } from "./fixtures";

const expectedVersionData = [
  {
    dateFrom: "04.08.2020",
    dateTo: "03.08.2022",
    status: "Nicht in Kraft",
  },
  {
    dateFrom: "04.08.2022",
    dateTo: "nicht vorhanden",
    status: "In Kraft",
  },
  {
    dateFrom: "04.08.2919",
    dateTo: "nicht vorhanden",
    status: "Nicht in Kraft",
  },
];

test("can browse different Fassungen of a norm", async ({ page }) => {
  await page.goto(
    "/norms/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/regelungstext-1",
    { waitUntil: "networkidle" },
  );

  await page.getByRole("tab", { name: "Fassungen" }).click();

  for (const version of expectedVersionData) {
    const row = page.getByRole("row", { name: version.dateFrom });
    await expect(row.getByLabel("Gültig von")).toHaveText(version.dateFrom);
    await expect(row.getByLabel("Gültig bis")).toHaveText(version.dateTo);
    await expect(row.getByLabel("Status")).toHaveText(version.status);
  }
});
