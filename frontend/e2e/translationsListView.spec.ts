import { expect, navigate, test } from "./utils/fixtures";

test("opens the page via URL", { tag: ["@RISDEV-8949"] }, async ({ page }) => {
  await navigate(page, "/translations");
  await expect(
    page.getByRole("heading", {
      name: "English Translations of German Federal Laws and Regulations",
    }),
  ).toBeVisible();
  await expect(page).toHaveTitle(
    "English Translations of German Federal Laws and Regulations | Rechtsinformationen des Bundes",
  );
});

test(
  "is linked on the landing page",
  { tag: ["@RISDEV-8954"] },
  async ({ page, privateFeaturesEnabled }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(page, "/");
    const translationsLink = page.getByRole("link", {
      name: "Go to translations",
    });
    await expect(translationsLink).toBeVisible();
    await translationsLink.click();
    await expect(page).toHaveTitle(
      "English Translations of German Federal Laws and Regulations | Rechtsinformationen des Bundes",
    );
  },
);

test(
  "should be linked in footer",
  { tag: ["@RISDEV-8954"] },
  async ({ page, privateFeaturesEnabled }) => {
    test.skip(!privateFeaturesEnabled);
    await navigate(page, "/");

    const footer = page.getByRole("contentinfo");
    const translationsLink = footer.getByRole("link", {
      name: "English translations",
    });

    await expect(translationsLink).toBeVisible();
    await translationsLink.click();
    await expect(page).toHaveTitle(
      "English Translations of German Federal Laws and Regulations | Rechtsinformationen des Bundes",
    );
  },
);

test(
  "should not be linked when publicly reachable",
  { tag: ["@RISDEV-8954"] },
  async ({ page, privateFeaturesEnabled }) => {
    test.skip(privateFeaturesEnabled);
    await navigate(page, "/");

    const footer = page.locator("footer");
    const footerTranslationsLink = footer.getByRole("link", {
      name: "English translations",
    });
    await expect(footerTranslationsLink).not.toBeVisible();

    const translationsLink = page.getByRole("link", {
      name: "Go to translations",
    });
    await expect(translationsLink).not.toBeVisible();
  },
);

test(
  "displays search bar and list",
  { tag: ["@RISDEV-8949"] },
  async ({ page }) => {
    await navigate(page, "/translations");
    const searchInput = page.getByRole("searchbox");
    await expect(searchInput).toHaveCount(1);

    const resultsRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = resultsRegion.getByRole("listitem");
    await expect(items).toHaveCount(3);
  },
);

test(
  "displays list sorted by title ascending by default",
  { tag: ["@RISDEV-8949"] },
  async ({ page }) => {
    await navigate(page, "/translations");

    const resultsRegion = page.getByRole("region", {
      name: "Translations List",
    });

    const titlesLocator = resultsRegion
      .getByRole("listitem")
      .getByRole("heading");
    const listItemTitles = await titlesLocator.allTextContents();
    const sortedTitles = [...listItemTitles].sort((a, b) => a.localeCompare(b));
    expect(listItemTitles).toEqual(sortedTitles);
  },
);

test(
  "filters translations correctly",
  { tag: ["@RISDEV-8949"] },
  async ({ page }) => {
    await navigate(page, "/translations");
    const searchTerm = "Dentist";

    const input = page.getByRole("searchbox", { name: "search term" });
    await input.fill(searchTerm);

    await expect(input).toHaveValue(searchTerm);

    await input.press("Enter");

    const translationListRegion = page.getByRole("region", {
      name: "Translations List",
    });
    const items = translationListRegion.getByRole("listitem");
    await expect(items).toHaveCount(1);

    await input.fill("not there");
    await input.press("Enter");
    await expect(translationListRegion.getByRole("listitem")).toHaveCount(0);

    await expect(page.getByText("We didn’t find anything.")).toBeVisible();
  },
);

test("sorts translations by relevance after search", async ({ page }) => {
  await navigate(page, "/translations");
  const searchTerm = "abg";
  const input = page.getByRole("searchbox", { name: "search term" });
  await input.fill(searchTerm);
  await input.press("Enter");

  const resultsRegion = page.getByRole("region", {
    name: "Translations List",
  });

  const items = resultsRegion.getByRole("listitem");
  const titlesLocator = resultsRegion
    .getByRole("listitem")
    .getByRole("heading");
  const listItemTitles = await titlesLocator.allTextContents();

  const sortedTitles = [...listItemTitles].sort((a, b) => a.localeCompare(b));
  expect(listItemTitles).not.toEqual(sortedTitles);

  const allTexts = await items.allTextContents();
  expect(allTexts.length).toBeGreaterThan(0);
  expect(allTexts[0]).toMatch(new RegExp(`^${searchTerm}`, "i"));
});
