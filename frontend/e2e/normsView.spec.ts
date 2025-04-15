import { test } from "@playwright/test";
import { expect } from "./fixtures";
import { getResultCount } from "./utils";

const expectedNorms = [
  "Fiktive Fruchtsaft- und Erfrischungsgetränkeverordnung zu Testzwecken",
];

test("can search, filter for norms, and view a single norm", async ({
  page,
}) => {
  await test.step("Basic search", async () => {
    await page.goto("/");
    await page.getByPlaceholder("Suchbegriff eingeben").fill("Fiktiv Satz");
    await page.getByLabel("Suchen").click();

    // three current matching norms, plus matching decisions
    const expectedCaseLawCount = 4;
    expect(await getResultCount(page)).toBe(
      expectedNorms.length + expectedCaseLawCount,
    );
  });

  await test.step("Filter for norms", async () => {
    await page.getByRole("button", { name: "Gesetze & Verordnungen" }).click();
    await expect
      .poll(() => getResultCount(page), {
        message: "the count should decrease",
      })
      .toBe(expectedNorms.length);
  });
  const headings = await page
    .getByTestId("searchResult")
    .locator('a[class*="heading"]')
    .all();
  const titles = await Promise.all(
    headings.map((heading) => heading.innerText()),
  );
  expect(titles.map((t) => t.trim())).toEqual(
    expect.arrayContaining(expectedNorms),
  );

  await test.step("View a norm", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();

    await page.waitForURL(
      /norms\/eli\/(?<jurisdiction>[^/]+)\/(?<agent>[^/]+)\/(?<year>[^/]+)\/(?<naturalIdentifier>[^/]+)\/(?<pointInTime>[^/]+)\/(?<version>[^/]+)\/(?<language>[^/]+)\/(?<subtype>[^/]+)$/gi,
    );

    await expect(
      page.getByRole("heading", {
        name: expectedNorms[0],
      }),
    ).toBeVisible();
    await expect(page.locator(".akn-act")).toBeVisible();
  });
});

test("can navigate to a single norm article and between articles", async ({
  page,
}) => {
  const mainExpressionEliUrl =
    "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/regelungstext-1";
  await page.goto(mainExpressionEliUrl);

  await test.step("Navigate from main norm view to a single article", async () => {
    await page.getByRole("link", { name: "§ 1 Anwendungsbereich" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-1_abschnitt-1_art-1",
    );
    await expect(page.locator("section")).toHaveCount(2);
  });

  await test.step("Navigate between single articles", async () => {
    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-1_abschnitt-2_art-1",
    );
    await expect(page.locator("section")).toHaveCount(2);
    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-1_abschnitt-2_art-1",
    );
    await expect(page.locator("section")).toHaveCount(8);
  });

  await test.step("Navigate back to main norm view", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();
    await page.waitForURL(mainExpressionEliUrl);
  });

  await test.step("View footnotes in title", async () => {
    const marker = await page.getByRole("superscript").innerText();
    expect(marker).toBe("❃");

    const footnotes = page.locator(".dokumentenkopf-fussnoten");
    await expect(footnotes).not.toContainText("❃");

    await page.getByRole("button", { name: "Fußnote anzeigen" }).click();

    await expect(footnotes).toContainText("❃");
    await expect(footnotes).toContainText(
      "(Diese Fußnote im Titel wurde im End-to-end-Datenbestand ergänzt",
    );

    await page.getByRole("button", { name: "Fußnote ausblenden" }).click();
    await expect(footnotes).not.toContainText("❃");
  });
});

test("can navigate to and view an attachment", async ({ page }) => {
  const mainExpressionEliUrl =
    "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/regelungstext-1";
  await page.goto(mainExpressionEliUrl);

  const table = page.getByRole("table");
  const cell = table.getByRole("cell", { name: "Produktionsanforderungen" });
  await expect(cell).toBeVisible();

  await test.step("Navigate from main norm view to a single article", async () => {
    const attachmentTitle =
      "Anlage (zu § 4 Absatz 2, § 5 Absatz 1 bis 3, § 6 Absatz 2 bis 4 und § 8)";

    await page
      .getByRole("main")
      .getByRole("link", { name: attachmentTitle })
      .click();

    await expect(
      page.getByRole("heading", { name: attachmentTitle }),
    ).toBeVisible();
    await page.waitForURL(mainExpressionEliUrl + "/anlagen-1_anlage-1");
    await expect(cell).toBeVisible();
  });

  await test.step("Navigate back to main norm view", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();
    await page.waitForURL(mainExpressionEliUrl);
  });
});
