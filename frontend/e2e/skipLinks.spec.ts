import { expect, navigate, noJsTest, test } from "./utils/fixtures";
import {
  expectPageSkipLinks,
  type SkipLinkExpectation,
} from "./utils/skipLinks";

type Scenario = {
  name: string;
  url: string;
  skipLinks: readonly SkipLinkExpectation[];
  requiresPrivateFeatures?: true;
};

const scenarios: Scenario[] = [
  {
    name: "start page",
    url: "/",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "search page",
    url: "/suche",
    skipLinks: [
      { label: "Zur Suche", target: "#search" },
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "advanced search page",
    url: "/erweiterte-suche",
    requiresPrivateFeatures: true,
    skipLinks: [
      { label: "Zur Suche", target: "#search" },
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "norm page",
    url: "/gesetze/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Gesetzestext", target: "#content" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "norm article page",
    url: "/gesetze/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/art-z1",
    skipLinks: [
      { label: "Zum Gesetzestext", target: "#content" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "case-law page",
    url: "/gerichtsentscheidungen/KORE600500000",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Entscheidungstext", target: "#content" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "literature page",
    url: "/literaturnachweise/XXLU000000001",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Text", target: "#content" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "administrative directive page",
    url: "/verwaltungsregelungen/KSNR000000001",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Text", target: "#content" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    name: "translations list",
    url: "/translations",
    skipLinks: [
      { label: "Skip to main", target: "#main" },
      { label: "Skip to footer", target: "#footer" },
    ],
  },
  {
    name: "translation detail page",
    url: "/translations/ABG",
    skipLinks: [
      { label: "Skip to main", target: "#main" },
      { label: "Skip to footer", target: "#footer" },
    ],
  },
  {
    name: "error page",
    url: "/404",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
  {
    // representative all static content pages
    name: "about page",
    url: "/ueber",
    skipLinks: [
      { label: "Zum Inhalt", target: "#main" },
      { label: "Zum Fußbereich", target: "#footer" },
    ],
  },
];

test("skip links are focused after client-side navigation", async ({
  page,
  isMobileTest,
}) => {
  test.skip(isMobileTest);

  await navigate(page, "/");

  await page.keyboard.press("Tab");
  await expect(
    page.getByRole("navigation", { name: "Sprunglinks" }).getByRole("link", {
      name: "Zum Inhalt",
    }),
  ).toBeFocused();

  await page
    .getByRole("navigation")
    .getByRole("link", { name: "Suche" })
    .click();

  await expect(
    page.getByRole("heading", { name: "Suche", level: 1, exact: true }),
  ).toBeVisible();

  const skipLinks = page.getByRole("navigation", { name: "Sprunglinks" });
  skipLinks.waitFor({ state: "attached" });

  await page.keyboard.press("Tab");
  await expect(
    skipLinks.getByRole("link", { name: "Zur Suche" }),
  ).toBeFocused();
});

test.describe("mobile", () => {
  test.beforeEach(({ isMobileTest }) => {
    test.skip(!isMobileTest);
  });

  test("skip links are focused after client-side navigation", async ({
    page,
  }) => {
    await navigate(page, "/");

    await page.keyboard.press("Tab");
    await expect(
      page.getByRole("navigation", { name: "Sprunglinks" }).getByRole("link", {
        name: "Zum Inhalt",
      }),
    ).toBeFocused();

    await page.getByRole("button", { name: "Menü" }).click();

    await page
      .getByRole("navigation")
      .getByRole("link", { name: "Suche" })
      .click();

    await expect(
      page.getByRole("heading", { name: "Suche", level: 1, exact: true }),
    ).toBeVisible();

    const skipLinks = page.getByRole("navigation", { name: "Sprunglinks" });
    skipLinks.waitFor({ state: "attached" });

    await page.keyboard.press("Tab");
    await expect(
      skipLinks.getByRole("link", { name: "Zur Suche" }),
    ).toBeFocused();
  });
});

for (const { name, url, skipLinks, requiresPrivateFeatures } of scenarios) {
  test(`skip links work on the ${name}`, async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(requiresPrivateFeatures === true && !privateFeaturesEnabled);
    await navigate(page, url);
    await expectPageSkipLinks(page, skipLinks);
  });
}

noJsTest("skip links exist without JavaScript", async ({ page }) => {
  await page.goto("/");
  await expect(
    page.getByRole("navigation", { name: "Sprunglinks" }),
  ).toBeAttached();
});
