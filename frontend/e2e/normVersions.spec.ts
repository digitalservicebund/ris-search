import { expect, navigate, test } from "./utils/fixtures";

test.beforeAll(async ({ privateFeaturesEnabled }) => {
  test.skip(
    !privateFeaturesEnabled,
    "This feature is not available for public",
  );
});

test.describe(
  "gesamtausgaben tab",
  { tag: ["@RISDEV-10909", "@RISDEV-12189"] },
  async () => {
    test(
      "displays Gesamtausgaben in the Gesamtausgaben tab",
      { tag: ["@RISDEV-12556"] },
      async ({ page, isMobileTest }) => {
        await navigate(
          page,
          "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu",
        );

        await page.getByRole("tab", { name: "Gesamtausgaben" }).click();

        const gesamtausgaben = page
          .getByRole("list", { name: "Gesamtausgaben" })
          .getByRole("listitem");

        await test.step("desktop shows separator dashes and no placeholder for missing dates", async (step) => {
          step.skip(isMobileTest);
          await expect(gesamtausgaben).toHaveText(
            [
              "Gültig ab: 04.08.2920 Gültig bis: Status: Zukünftig in Kraft",
              "Gültig ab: 04.08.2022 – Gültig bis: 03.08.2920 Status: Aktuell gültig",
              "Gültig ab: 04.08.2020 – Gültig bis: 03.08.2022 Status: Außer Kraft",
            ],
            { useInnerText: true },
          );
        });

        await test.step("mobile doesn't show separator dashes but placeholder for missing dates", async (step) => {
          step.skip(!isMobileTest);
          await expect(gesamtausgaben).toHaveText(
            [
              "Gültig ab: 04.08.2920 Gültig bis: — Status: Zukünftig in Kraft",
              "Gültig ab: 04.08.2022 Gültig bis: 03.08.2920 Status: Aktuell gültig",
              "Gültig ab: 04.08.2020 Gültig bis: 03.08.2022 Status: Außer Kraft",
            ],
            { useInnerText: true },
          );
        });
      },
    );

    test("marks the Gesamtausgabe currently displayed as the current page", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu?view=gesamtausgaben",
      );

      await expect(
        page.getByRole("link", { name: /04\.08\.2022/ }),
      ).toHaveAttribute("aria-current", "page");
    });

    test(
      "can navigate to a Gesamtausgabe by clicking its link",
      { tag: ["@RISDEV-12556"] },
      async ({ page }) => {
        await navigate(
          page,
          "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu?view=gesamtausgaben",
        );

        await expect(
          page.getByRole("heading", {
            name: "Zum Testen von Fassungen - Aktuelle Fassung",
          }),
        ).toBeVisible();

        await page.getByRole("link", { name: /04\.08\.2920/ }).click();

        await expect(
          page.getByRole("heading", {
            name: "Zum Testen von Fassungen - Zukünftige Fassung",
          }),
        ).toBeVisible();
      },
    );

    test("can filter Gesamtausgaben by date", async ({ page }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=gesamtausgaben",
      );

      const gesamtausgaben = page
        .getByRole("list", { name: "Gesamtausgaben" })
        .getByRole("listitem");

      await expect(gesamtausgaben).toHaveCount(3);

      await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.2020");

      await expect(gesamtausgaben).toHaveText(/04\.08\.2020/);
    });

    test("shows no results placeholder when no Gesamtausgabe found", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=gesamtausgaben",
      );

      const gesamtausgaben = page
        .getByRole("list", { name: "Gesamtausgaben" })
        .getByRole("listitem");

      await expect(gesamtausgaben).toHaveCount(3);

      await page.getByRole("textbox", { name: "Gültig am" }).fill("04.08.1536");

      await expect(gesamtausgaben).toHaveText(["Keine Ergebnisse gefunden"]);
    });

    test("announces the number of Gesamtausgaben after filtering", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu?view=gesamtausgaben",
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

      await expect(announcement).toHaveText("1 Gesamtausgabe");

      await dateFilter.fill("");

      await expect(announcement).toHaveText("3 Gesamtausgaben");
    });
  },
);

test.describe("displays metadata correctly", async () => {
  test("currently valid norm", { tag: ["@RISDEV-12556"] }, async ({ page }) => {
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
      "03.08.2920",
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

  test("on future norm", { tag: ["@RISDEV-12556"] }, async ({ page }) => {
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
      "04.08.2920",
      "Gültig bis",
      "—",
    ]);
  });
});

test.describe("validity info on Einzelnorm", { tag: ["@RISDEV-12556"] }, () => {
  test("links a historic Fassung to the valid Fassung", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1234/2020-01-01/1/deu/art-z1",
    );

    await expect(
      page.getByText("Sie lesen eine historische Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(page).toHaveURL(
      /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1234\/2022-01-01\/1\/deu\/art-z1/,
    );
    await expect(page.getByText("Sie lesen eine")).toBeHidden();
  });

  test("links to the valid Fassung within the valid Gesamtausgabe if the Fassung is part of multiple Gesamtausgaben", async ({
    page,
  }) => {
    // The valid Fassung of § 2 is part of the historic (2021), the valid
    // (2022) and the future (2920) Gesamtausgabe
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1234/2020-01-01/1/deu/art-z2",
    );

    await expect(
      page.getByText("Sie lesen eine historische Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(page).toHaveURL(
      /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1234\/2022-01-01\/1\/deu\/art-z2/,
    );
  });

  test("shows a historic Fassung without link if no valid Fassung exists", async ({
    page,
  }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/1970/s1901/1990-03-13/9/deu/art-z1",
    );

    await expect(
      page.getByText("Sie lesen eine historische Fassung."),
    ).toBeVisible();
    await expect(
      page.getByRole("link", { name: "Zur aktuell gültigen Fassung" }),
    ).toBeHidden();
  });

  test("links a valid Fassung to the future Fassung", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Ab 04.08.2920 gilt eine neue Fassung."),
    ).toBeVisible();

    await page.getByRole("link", { name: "Zur zukünftigen Fassung" }).click();

    await expect(page).toHaveURL(
      /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1126\/2920-08-04\/1\/deu\/hauptteil-n1_abschnitt-n2_art-z1/,
    );
    await expect(
      page.getByText("Sie lesen eine zukünftige Fassung."),
    ).toBeVisible();
  });

  test("links a future Fassung to the valid Fassung", async ({ page }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu/hauptteil-n1_abschnitt-n2_art-z1",
    );

    await expect(
      page.getByText("Sie lesen eine zukünftige Fassung."),
    ).toBeVisible();

    await page
      .getByRole("link", { name: "Zur aktuell gültigen Fassung" })
      .click();

    await expect(page).toHaveURL(
      /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1126\/2022-08-04\/1\/deu\/hauptteil-n1_abschnitt-n2_art-z1/,
    );
    await expect(
      page.getByText("Ab 04.08.2920 gilt eine neue Fassung."),
    ).toBeVisible();
  });

  test("shows a future Fassung without link if no valid Fassung exists", async ({
    page,
  }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2025/145/2025-07-17/1/deu/art-z1",
    );

    await expect(
      page.getByText("Sie lesen eine zukünftige Fassung."),
    ).toBeVisible();
    await expect(
      page.getByRole("link", { name: "Zur aktuell gültigen Fassung" }),
    ).toBeHidden();
  });

  test("shows no info for a valid Fassung of a historic Gesamtausgabe", async ({
    page,
  }) => {
    await navigate(
      page,
      "/gesetze/eli/bund/bgbl-1/2020/s1234/2021-01-01/1/deu/art-z2",
    );

    await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
    await expect(page.getByText("Sie lesen eine")).toBeHidden();
  });
});

test.describe(
  "validity info on Gesamtausgabe",
  { tag: ["@RISDEV-12556"] },
  () => {
    test("links a historic Gesamtausgabe to the valid Gesamtausgabe", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1234/2020-01-01/1/deu",
      );

      await expect(
        page.getByText("Sie lesen eine historische Gesamtausgabe."),
      ).toBeVisible();

      await page
        .getByRole("link", { name: "Zur aktuell gültigen Gesamtausgabe" })
        .click();

      await expect(page).toHaveURL(
        /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1234\/2022-01-01\/1\/deu/,
      );
    });

    test("shows a historic Gesamtausgabe without link if no valid Gesamtausgabe exists", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/1975/s2483/1999-10-14/6/deu",
      );

      await expect(
        page.getByText("Sie lesen eine historische Gesamtausgabe."),
      ).toBeVisible();
      await expect(
        page.getByRole("link", { name: "Zur aktuell gültigen Gesamtausgabe" }),
      ).toBeHidden();
    });

    test("links a future Gesamtausgabe to the valid Gesamtausgabe", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu",
      );

      await expect(
        page.getByText("Sie lesen eine zukünftige Gesamtausgabe."),
      ).toBeVisible();

      await page
        .getByRole("link", { name: "Zur aktuell gültigen Gesamtausgabe" })
        .click();

      await expect(page).toHaveURL(
        /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1126\/2022-08-04\/1\/deu/,
      );
      await expect(
        page.getByText("Ab 04.08.2920 gilt eine neue Gesamtausgabe."),
      ).toBeVisible();
    });

    test("shows a future Gesamtausgabe without link if no valid Gesamtausgabe exists", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2025/145/2025-07-17/1/deu",
      );

      await expect(
        page.getByText("Sie lesen eine zukünftige Gesamtausgabe."),
      ).toBeVisible();
      await expect(
        page.getByRole("link", { name: "Zur aktuell gültigen Gesamtausgabe" }),
      ).toBeHidden();
    });

    test("links a valid Gesamtausgabe to the future Gesamtausgabe", async ({
      page,
    }) => {
      await navigate(
        page,
        "/gesetze/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu",
      );

      await expect(
        page.getByText("Ab 04.08.2920 gilt eine neue Gesamtausgabe."),
      ).toBeVisible();

      await page
        .getByRole("link", { name: "Zur zukünftigen Gesamtausgabe" })
        .click();

      await expect(page).toHaveURL(
        /\/gesetze\/eli\/bund\/bgbl-1\/2020\/s1126\/2920-08-04\/1\/deu/,
      );
      await expect(
        page.getByText("Sie lesen eine zukünftige Gesamtausgabe."),
      ).toBeVisible();
    });
  },
);

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
    "Suche",
    "FrSaftErfrischV",
  ]);
});
