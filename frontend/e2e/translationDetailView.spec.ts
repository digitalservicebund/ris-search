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

  const tabs = page.getByRole("tab");

  await expect(tabs).toHaveCount(2);

  const firstTabText = await tabs.nth(0).textContent();
  expect(firstTabText).toBe("Text ");

  const secondTabText = await tabs.nth(1).textContent();
  expect(secondTabText).toBe("Details ");
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

    const detailTab = page.getByRole("tab", { name: "Details " });
    await detailTab.click();

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

    await page.getByRole("tab", { name: "Details " }).click();
    await expect(section).not.toBeVisible();
  },
);

test(
  "has link to german original",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    await page.goto("/translations/TestV");
    await page.getByRole("link", { name: "Go to the German version" }).click();
    await page.waitForLoadState("networkidle");
    await expect(
      page.getByRole("heading", {
        name: "Testverordnung zur Musterregelung des öffentlichen Dienstes",
      }),
    ).toBeVisible();
  },
);

test(
  "Links in action menu are correct",
  { tag: ["@RISDEV-8950"] },
  async ({ page }) => {
    const url = "/translations/TestV";
    await page.goto(url);

    await page.waitForLoadState("networkidle");

    await expect(page.getByRole("button", { name: "Link to translation" }))
      .toBeVisible;
    await expect(page.getByRole("button", { name: "Drucken" })).toBeVisible;
    const pdfButton = page.getByRole("button", { name: "Als PDF Drucken" });

    await expect(pdfButton).toBeVisible;
    await expect(pdfButton).toBeDisabled;
  },
);
