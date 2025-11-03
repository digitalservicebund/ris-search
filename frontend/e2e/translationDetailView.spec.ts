import { expect, test, noJsTest } from "./utils/fixtures";

test(
  "can navigate from list to single translation",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations");
    const translationListRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = translationListRegion.getByRole("listitem");
    await expect(items).toHaveCount(3);

    const title = "Act on Chemistry and Dentists";

    await page.getByRole("link", { name: title }).click();

    await page.waitForURL("/translations/CDe");

    await expect(
      page.getByRole("heading", {
        name: title,
      }),
    ).toBeVisible();
  },
);

test("opens the page via URL", { tag: ["@RISDEV-8950"] }, async ({ page }) => {
  await page.goto("/translations/TestV");
  await expect(
    page.getByRole("heading", {
      name: "Test Regulation for the Model Framework of the Public Service",
    }),
  ).toBeVisible();
  await expect(page).toHaveTitle(
    "Test Regulation for the Model Framework of the Public | Rechtsinformationen des Bundes",
  );
});

test("has text and detail tab", { tag: ["@RISDEV-8950"] }, async ({ page }) => {
  await page.goto("/translations/TestV");

  const textTab = page.getByRole("link", { name: "Text der Übersetzung" });
  const detailsTab = page.getByRole("link", {
    name: "Details zur Übersetzung",
  });

  await expect(textTab).toBeVisible();
  await expect(detailsTab).toBeVisible();
});

test(
  "detail tab shows details",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations/TestV");
    await page.waitForLoadState("networkidle");

    const heading = page.getByRole("heading", { name: " Details " });
    const translator = page.getByText("someone and someone.");
    const version = page.getByText(
      "The translation includes the amendment(s) to the Act by Article 1 of the Act of 8 October 2021",
    );
    await expect(heading).not.toBeVisible();
    await expect(translator).not.toBeVisible();
    await expect(version).not.toBeVisible();

    await page.getByRole("link", { name: "Details zur Übersetzung" }).click();

    await expect(heading).toBeVisible();
    await expect(translator).toBeVisible();
    await expect(version).toBeVisible();
  },
);

test(
  "text tab shows text of translated norm",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations/TestV");
    await page.waitForLoadState("networkidle");

    const section = page.getByText("Section 1Dummy data");

    await expect(section).toBeVisible();

    await page.getByRole("link", { name: "Details zur Übersetzung" }).click();
    await expect(section).not.toBeVisible();
  },
);

test(
  "german original and english version link to each other",
  { tag: ["@RISDEV-8950"] },
  async ({ page, isMobileTest }) => {
    await page.goto("/translations/TestV");
    await expect(
      page.getByRole("heading", {
        name: "Test Regulation for the Model Framework of the Public Service",
      }),
    ).toBeVisible();
    await page.getByRole("link", { name: "Go to the German version" }).click();
    await page.waitForLoadState("networkidle");
    await expect(
      page.getByRole("heading", {
        name: "Testverordnung zur Musterregelung des öffentlichen Dienstes",
      }),
    ).toBeVisible();

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }
    const translationButton = page.getByRole("link", {
      name: "Zur englischen Übersetzung",
    });

    await expect(translationButton).toBeVisible();
    translationButton.click();
    await expect(
      page.getByRole("heading", {
        name: "Test Regulation for the Model Framework of the Public Service",
      }),
    ).toBeVisible();
  },
);

test(
  "Links in action menu are correct",
  { tag: ["@RISDEV-8950"] },
  async ({ page, isMobileTest }) => {
    const url = "/translations/TestV";
    await page.goto(url);

    await page.waitForLoadState("networkidle");

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }

    await expect(
      page.getByRole("link", { name: "Link to translation" }),
    ).toBeVisible();

    if (isMobileTest) {
      await expect(page.getByText("Als PDF speichern")).toBeVisible();
      await expect(page.getByText("Drucken")).toBeVisible();
    } else {
      const printButton = page.getByRole("button", { name: "Drucken" });
      const pdfButton = page.getByRole("button", { name: "Als PDF speichern" });
      await printButton.waitFor({ state: "visible" });
      await expect(printButton).toBeVisible();

      await pdfButton.waitFor({ state: "visible" });
      await expect(pdfButton).toBeVisible();
      await expect(pdfButton).toBeDisabled();
    }
  },
);

noJsTest(
  "tabs work without JavaScript",
  { tag: ["@RISDEV-8979"] },
  async ({ page }) => {
    await page.goto("/translations/TestV", {
      waitUntil: "networkidle",
    });

    await page.getByRole("link", { name: "Details zur Übersetzung" }).click();
    await expect(page).toHaveURL(/#details$/);

    await page.getByRole("link", { name: "Text der Übersetzung" }).click();
    await expect(page).toHaveURL(/#text$/);
  },
);
