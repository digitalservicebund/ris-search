import { expect, test, noJsTest, navigate } from "./utils/fixtures";

test(
  "can navigate from list to single translation",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await navigate(page, "/translations");
    const translationListRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = translationListRegion.getByRole("listitem");
    await expect(items).toHaveCount(3);

    const title = "Act on Chemistry and Dentists";

    await page.getByRole("link", { name: title }).click();

    await page.waitForURL("/translations/CDe", { waitUntil: "commit" });

    await expect(
      page.getByRole("heading", {
        name: title,
      }),
    ).toBeVisible();
  },
);

test("opens the page via URL", { tag: ["@RISDEV-8950"] }, async ({ page }) => {
  await navigate(page, "/translations/TestV");
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
  await navigate(page, "/translations/TestV");

  const textTab = page.getByRole("tab", { name: "Text" });
  const detailsTab = page.getByRole("tab", {
    name: "Details",
  });

  await expect(textTab).toBeVisible();
  await expect(detailsTab).toBeVisible();
});

test(
  "detail tab shows details",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await navigate(page, "/translations/TestV");

    const heading = page.getByRole("heading", { name: " Details " });
    const translator = page.getByText("someone and someone.");
    const version = page.getByText(
      "The translation includes the amendment(s) to the Act by Article 1 of the Act of 8 October 2021",
    );
    await expect(heading).not.toBeVisible();
    await expect(translator).not.toBeVisible();
    await expect(version).not.toBeVisible();

    await page.getByRole("tab", { name: "Details" }).click();

    await expect(heading).toBeVisible();
    await expect(translator).toBeVisible();
    await expect(version).toBeVisible();
  },
);

test(
  "text tab shows text of translated norm",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await navigate(page, "/translations/TestV");

    const section = page.getByText("Section 1Dummy data");

    await expect(section).toBeVisible();

    await page.getByRole("tab", { name: "Details" }).click();
    await expect(section).not.toBeVisible();
  },
);

test(
  "German original and English version link to each other",
  { tag: ["@RISDEV-8950"] },
  async ({ page, isMobileTest }) => {
    await navigate(page, "/translations/TestV");
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
    const translationButton = page.getByRole("menuitem", {
      name: "Zur englischen Übersetzung",
    });

    await expect(translationButton).toBeVisible();
    await translationButton.click();
    await expect(
      page.getByRole("heading", {
        name: "Test Regulation for the Model Framework of the Public Service",
      }),
    ).toBeVisible();
  },
);

test(
  "links in action menu are correct",
  { tag: ["@RISDEV-8950"] },
  async ({ page, isMobileTest }) => {
    const url = "/translations/TestV";
    await navigate(page, url);

    if (isMobileTest) {
      await page.getByLabel("Aktionen anzeigen").click();
    }

    await expect(
      page.getByRole("menuitem", { name: "Link to translation" }),
    ).toBeVisible();

    if (isMobileTest) {
      await expect(page.getByText("Als PDF speichern")).toBeVisible();
      await expect(page.getByText("Drucken")).toBeVisible();
    } else {
      const printButton = page.getByRole("menuitem", { name: "Drucken" });
      const pdfButton = page.getByRole("menuitem", {
        name: "Als PDF speichern",
      });
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
    await navigate(page, "/translations/TestV");

    await test.step("text", async () => {
      await expect(
        page.getByRole("tab", { name: "Text", selected: true }),
      ).toBeVisible();
    });

    await test.step("details", async () => {
      await page.getByRole("tab", { name: "Details" }).click();

      await expect(
        page.getByRole("heading", { name: "Details" }),
      ).toBeVisible();

      await expect(
        page.getByRole("tab", { name: "Details", selected: true }),
      ).toBeVisible();
    });
  },
);
