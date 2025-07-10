import { test } from "@playwright/test";
import { expect } from "./fixtures";
import { getDisplayedResultCount } from "./utils";

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
    expect(await getDisplayedResultCount(page)).toBe(
      expectedNorms.length + expectedCaseLawCount,
    );
  });

  await test.step("Filter for norms", async () => {
    await page.getByRole("button", { name: "Gesetze & Verordnungen" }).click();
    await expect
      .poll(() => getDisplayedResultCount(page), {
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

    await test.step("The content has the correct max-width", async () => {
      for (const locator of [
        "#praeambel-n1_formel-n1_text-n1",
        "#praeambel-n1_formel-n1_liste-n1",
        ".akn-section .akn-num",
        ".akn-section .akn-heading",
        ".akn-section .akn-paragraph",
      ]) {
        const boundingBox = await page.locator(locator).first().boundingBox();
        expect(boundingBox?.width, locator).toBeLessThanOrEqual(720);
      }
    });
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
      mainExpressionEliUrl + "/hauptteil-n1_abschnitt-n1_art-n1",
    );
    await expect(
      page.getByRole("heading", { name: "§ 1 Anwendungsbereich" }),
    ).toBeVisible();
  });

  await test.step("Navigate between single articles", async () => {
    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-n1_abschnitt-n2_art-n1",
    );
    await expect(
      page.getByRole("heading", {
        name: "§ 2 Zutaten, Herstellungsanforderungen",
      }),
    ).toBeVisible();
    await page.getByRole("link", { name: "Nächster Paragraf" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-n1_abschnitt-n2_art-n2",
    );
    await expect(
      page.getByRole("heading", {
        name: "§ 3 Kennzeichnung",
      }),
    ).toBeVisible();
  });

  await test.step("Navigate back between single articles", async () => {
    const mainExpressionEliUrl =
      "/norms/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/regelungstext-1";
    await page.goto(mainExpressionEliUrl + "/hauptteil-n1_abschnitt-n2_art-n2");
    await expect(
      page.getByRole("heading", {
        name: "§ 3 Kennzeichnung",
      }),
    ).toBeVisible();

    await page.getByRole("link", { name: "Vorheriger Paragraf" }).click();
    await page.waitForURL(
      mainExpressionEliUrl + "/hauptteil-n1_abschnitt-n2_art-n1",
    );
    await expect(
      page.getByRole("heading", {
        name: "§ 2 Zutaten, Herstellungsanforderungen",
      }),
    ).toBeVisible();
  });

  await test.step("Navigate back to main norm view", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();
    await page.waitForURL(mainExpressionEliUrl);
  });

  await test.step("View footnotes in title", async () => {
    const marker = await page.getByRole("superscript").innerText();
    expect(marker).toBe("❃");

    const footnotes = page.locator(".dokumentenkopf-fussnoten");
    const footnotesContent = footnotes.locator("ol");
    await expect(footnotesContent).not.toBeVisible();

    const expandButton = page.getByRole("button", { name: "Fußnote anzeigen" });

    await expect
      .poll(
        async () => {
          await expandButton.click();
          return footnotesContent.isVisible();
        },
        { message: "wait for the expand button to become interactive" },
      )
      .toBeTruthy();

    await expect(footnotes).toContainText(
      "(Diese Fußnote im Titel wurde im End-to-end-Datenbestand ergänzt",
    );

    await page.getByRole("button", { name: "Fußnote ausblenden" }).click();
    await expect(footnotesContent).not.toBeVisible();
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
      "Anlage T1 (zu § 4 Absatz 2, § 5 Absatz 1 bis 3, § 6 Absatz 2 bis 4 und § 8)";

    await page
      .getByRole("main")
      .getByRole("link", { name: attachmentTitle })
      .click();

    await expect(
      page.getByRole("heading", { name: attachmentTitle }),
    ).toBeVisible();
    await page.waitForURL(mainExpressionEliUrl + "/anlagen-n1_anlage-n1");
    await expect(cell).toBeVisible();
  });

  await test.step("Navigate back to main norm view", async () => {
    await page.getByRole("link", { name: expectedNorms[0] }).first().click();
    await page.waitForURL(mainExpressionEliUrl);
  });
});

test("can view images", async ({ page }) => {
  await page.goto(
    "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/regelungstext-1",
  );

  await page.getByRole("img", { name: "Beispielbild" }).isVisible();

  await test.step("in a single article", async () => {
    await page
      .getByRole("main")
      .getByRole("link", { name: "§ 1 Beispielhafte Illustration" })
      .click();
    await page.waitForURL(/\/hauptteil-n1_art-n1$/g);
    await page.getByRole("img", { name: "Beispielbild" }).isVisible();
  });
});

test.use({
  permissions: ["clipboard-write", "clipboard-read"],
});

test("can copy and use link to work (changing content)", async ({ page }) => {
  await page.goto(
    "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/regelungstext-1",
    { waitUntil: "networkidle" },
  );
  const button = page.getByRole("link", {
    name: "Link zur jeweils gültigen Fassung",
  });
  await button.hover();

  await expect(
    page.getByRole("tooltip", { name: "Link zur jeweils gültigen Fassung" }),
  ).toBeVisible();

  let clipboardContents: string;

  await test.step("can copy the link", async () => {
    await button.click();

    await expect(page.getByText("Kopiert!")).toBeVisible();
    clipboardContents = await page.evaluate(() => {
      return navigator.clipboard.readText();
    });
    expect(
      clipboardContents.endsWith(
        "/norms/eli/bund/bgbl-1/2024/383/regelungstext-1",
        // note the omission of 2024-12-19/1/deu/
      ),
    ).toBe(true);
  });

  await test.step("can use the copied link to get back to the original URL", async () => {
    await page.goto(clipboardContents);
    await page.waitForURL(
      "/norms/eli/bund/bgbl-1/2024/383/2024-12-19/1/deu/regelungstext-1",
    );
  });
});
