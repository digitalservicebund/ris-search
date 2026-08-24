import { expect, navigate, test } from "./utils/fixtures";

test.beforeAll(async ({ privateFeaturesEnabled }) => {
  test.skip(
    !privateFeaturesEnabled,
    "This feature is not available for public",
  );
});

test.describe(
  "fassungen tab",
  { tag: ["@RISDEV-10909", "@RISDEV-12189"] },
  async () => {
    test("displays Fassungen in the Fassungen tab", async ({ page }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu",
      );

      await page.getByRole("tab", { name: "Fassungen" }).click();

      await expect(
        page.getByRole("list", { name: "Fassungen" }).getByRole("listitem"),
      ).toHaveText([
        "Gültig ab: 04.08.2919 Gültig bis: – Status: Zukünftig in Kraft",
        "Gültig ab: 04.08.2022 Gültig bis: 01.01.2030 Status: Aktuell gültig",
        "Gültig ab: 04.08.2020 Gültig bis: 03.08.2022 Status: Außer Kraft",
      ]);
    });

    test("marks the Fassung currently displayed as the current page", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu?view=versions",
      );

      await expect(
        page.getByRole("link", { name: /04\.08\.2022/ }),
      ).toHaveAttribute("aria-current", "page");
    });

    test("can navigate to a Fassung by clicking its link", async ({ page }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu?view=versions",
      );

      await expect(
        page.getByRole("heading", {
          name: "Zum Testen von Fassungen - Aktuelle Fassung",
        }),
      ).toBeVisible();

      await page.getByRole("link", { name: /04\.08\.2919/ }).click();

      await expect(
        page.getByRole("heading", {
          name: "Zum Testen von Fassungen - Zukünftige Fassung",
        }),
      ).toBeVisible();
    });

    test("can filter Fassungen by date", async ({ page }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
      );

      const versions = page
        .getByRole("list", { name: "Fassungen" })
        .getByRole("listitem");

      await expect(versions).toHaveCount(3);

      await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.2020");

      await expect(versions).toHaveText([
        "Gültig ab: 04.08.2020 Gültig bis: 03.08.2022 Status: Außer Kraft",
      ]);
    });

    test("shows no results placeholder when no Fassung found", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
      );

      const versions = page
        .getByRole("list", { name: "Fassungen" })
        .getByRole("listitem");

      await expect(versions).toHaveCount(3);

      await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.1536");

      await expect(versions).toHaveText(["Keine Ergebnisse gefunden"]);
    });

    test("announces the number of Fassungen after filtering", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
      );

      // The tab holds a second status region, so match the element rather
      // than the status role.
      const announcement = page.locator("output[aria-live='polite']");
      const dateFilter = page.getByRole("textbox", { name: "Gültig am" });

      // Nothing to announce before the list changes
      await expect(announcement).toHaveText("");

      await dateFilter.fill("04.08.1536");

      await expect(announcement).toHaveText("Keine Ergebnisse gefunden");

      await dateFilter.fill("04.08.2020");

      await expect(announcement).toHaveText("1 Fassung");

      await dateFilter.fill("");

      await expect(announcement).toHaveText("3 Fassungen");
    });
  },
);

test.describe(
  "fassungen tab on a small screen",
  { tag: ["@RISDEV-10909", "@RISDEV-12189"] },
  () => {
    test.beforeEach(({ isMobileTest }) => {
      test.skip(!isMobileTest);
    });

    test("shows a label in front of every value instead of a header row", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=versions",
      );

      const firstVersion = page
        .getByRole("list", { name: "Fassungen" })
        .getByRole("listitem")
        .first();

      await expect(firstVersion.getByText("Gültig ab:")).toBeVisible();
      await expect(firstVersion.getByText("Gültig bis:")).toBeVisible();
      await expect(firstVersion.getByText("Status:")).toBeVisible();
    });
  },
);

test.describe("displays metadata correctly", async () => {
  test("currently valid norm", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu",
    );

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTest",
      "Status",
      "Aktuell gültig",
      "Gültig ab",
      "04.08.2022",
      "Gültig bis",
      "01.01.2030",
    ]);
  });

  test("on historic norm", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu",
    );

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTest",
      "Status",
      "Außer Kraft",
      "Gültig ab",
      "04.08.2020",
      "Gültig bis",
      "03.08.2022",
    ]);
  });

  test("on future norm", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu",
    );

    const metadataList = page.getByTestId("metadata-list");

    await expect(
      metadataList.getByRole("term").or(metadataList.getByRole("definition")),
    ).toHaveText([
      "Abkürzung",
      "RisFassTest",
      "Status",
      "Zukünftig in Kraft",
      "Gültig ab",
      "04.08.2919",
      "Gültig bis",
      "—",
    ]);
  });
});

test.describe("future or historic version info", () => {
  test("shows an info about future versions on a historic norm article", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Sie lesen einen Paragrafen einer historischen Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });

  test("shows an info about previous versions on a future norm article", async ({
    page,
    privateFeaturesEnabled,
  }) => {
    test.skip(!privateFeaturesEnabled);

    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Sie lesen einen Paragrafen einer zukünftigen Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(
      page.getByRole("heading", {
        name: "Zum Testen von Fassungen - Aktuelle Fassung",
      }),
    ).toBeVisible();
  });
});

test("displays validity in breadcrumb navigation", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(!privateFeaturesEnabled);
  await navigate(page, "/gesetze/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu");

  const breadcrumb = page.getByRole("navigation", { name: "Pfadnavigation" });
  await expect(breadcrumb).toBeVisible();

  const breadcrumbLinks = breadcrumb.getByRole("listitem");
  await expect(breadcrumbLinks).toContainText([
    "Start",
    "", // Empty items are separators
    "Suche",
    "",
    "FrSaftErfrischV",
  ]);
});
