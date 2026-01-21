import { expect, navigate, test } from "./fixtures";

export function testCopyLinkButton(
  pageUrl: string,
  buttonName: string,
  linkRegex: RegExp,
) {
  test("desktop", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);

    await navigate(page, pageUrl);

    const button = page.getByRole("menuitem", {
      name: buttonName,
    });

    await button.hover();
    await expect(
      page.getByRole("tooltip", {
        name: buttonName,
      }),
    ).toBeVisible({
      timeout: 15000,
    });

    await button.click();

    await expect(page.getByText("Kopiert!")).toBeVisible();
    expect(await page.evaluate(() => navigator.clipboard.readText())).toMatch(
      linkRegex,
    );
  });

  test("mobile", async ({ page, isMobileTest }) => {
    test.skip(!isMobileTest);
    await navigate(page, pageUrl);

    await page.getByRole("button", { name: "Aktionen anzeigen" }).click();

    await page
      .getByRole("menuitem", {
        name: buttonName,
      })
      .click();

    await expect(page.getByText("Kopiert!")).toBeVisible();

    expect(await page.evaluate(() => navigator.clipboard.readText())).toMatch(
      linkRegex,
    );
  });
}

export function testPrintButton(onPageUrl: string) {
  test("desktop", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);
    await navigate(page, onPageUrl);

    await page.evaluate(
      "(() => {window.waitForPrintDialog = new Promise(f => window.print = f);})()",
    );
    const button = page.getByRole("menuitem", { name: "Drucken" });

    await button.hover();
    await expect(page.getByRole("tooltip", { name: "Drucken" })).toBeVisible({
      timeout: 15000,
    });

    await button.click();
    await page.waitForFunction("window.waitForPrintDialog");
  });

  test("mobile", async ({ page, isMobileTest }) => {
    test.skip(!isMobileTest);
    await navigate(page, onPageUrl);
    await page.evaluate(
      "(() => {window.waitForPrintDialog = new Promise(f => window.print = f);})()",
    );

    await page.getByRole("button", { name: "Aktionen anzeigen" }).click();

    const button = page.getByRole("menuitem", { name: "Drucken" });
    await button.click();
    await page.waitForFunction("window.waitForPrintDialog");
  });
}

export function testPdfButton(pageUrl: string) {
  test("desktop", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);

    await navigate(page, pageUrl);

    const button = page
      .getByRole("menuitem", { name: "Als PDF speichern" })
      .getByRole("button");

    await button.hover();
    await expect(
      page.getByRole("tooltip", { name: "Als PDF speichern" }),
    ).toBeVisible({
      timeout: 15000,
    });

    await expect(button).toBeDisabled();
  });

  test("mobile", async ({ page, isMobileTest }) => {
    test.skip(!isMobileTest);
    await navigate(page, pageUrl);

    await page.getByRole("button", { name: "Aktionen anzeigen" }).click();

    await expect(
      page.getByRole("menuitem", { name: "Als PDF speichern" }),
    ).toBeDisabled();
  });
}

export function testXmlButton(pageUrl: string, expectedXmlUrl: string) {
  test("desktop", async ({ page, isMobileTest }) => {
    test.skip(isMobileTest);

    await navigate(page, pageUrl);

    const button = page.getByRole("menuitem", { name: "XML anzeigen" });

    await button.hover();
    await expect(
      page.getByRole("tooltip", { name: "XML anzeigen" }),
    ).toBeVisible({
      timeout: 15000,
    });

    await button.click();
    await page.waitForURL(expectedXmlUrl, { waitUntil: "commit" });
  });

  test("mobile", async ({ page, isMobileTest }) => {
    test.skip(!isMobileTest);
    await navigate(page, pageUrl);
    await page.getByRole("button", { name: "Aktionen anzeigen" }).click();
    await page.getByRole("menuitem", { name: "XML anzeigen" }).click();
    await page.waitForURL(expectedXmlUrl, { waitUntil: "commit" });
  });
}
