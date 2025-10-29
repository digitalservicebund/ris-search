import { expect, test } from "./utils/fixtures";

test(
  "can navigate from list to single translation",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations");
    const links = page.getByTestId("translations").getByRole("link");

    await expect(links).toHaveCount(3);

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
  "has link to german original",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations/TestV");
    await page.getByRole("link", { name: "Go to the German version" }).click();
    await expect(
      page.getByRole("heading", {
        name: "Testverordnung zur Musterregelung des öffentlichen Dienstes",
      }),
    ).toBeVisible();
  },
);

test(
  "tabs work without JavaScript",
  { tag: ["@RISDEV-8979"] },
  async ({ browser }) => {
    const context = await browser.newContext({ javaScriptEnabled: false });
    const page = await context.newPage();

    await page.goto("/translations/TestV", {
      waitUntil: "networkidle",
    });

    await page.getByRole("link", { name: "Details zur Übersetzung" }).click();
    await expect(page).toHaveURL(/#details$/);

    await page.getByRole("link", { name: "Text der Übersetzung" }).click();
    await expect(page).toHaveURL(/#text$/);

    await context.close();
  },
);
