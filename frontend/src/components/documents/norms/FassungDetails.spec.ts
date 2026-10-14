import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, within } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import type { LegislationExpression } from "~/types/api";
import FassungDetails from "./FassungDetails.vue";

// deliberately unsorted, so the tests can check that the newest come first
const gesamtausgaben = [2026, 2031, 2028, 2027, 2030, 2029].map(
  (year) =>
    ({
      "@id": `/v1/legislation/eli/bund/${year}`,
      legislationIdentifier: `eli/bund/${year}`,
      temporalCoverage: `${year}-01-01/..`,
    }) as LegislationExpression,
);

describe("FassungDetails", () => {
  it("shows a loading indicator while the content is loading", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: false,
        fassungEId: "art-1",
      },
    });

    expect(screen.getByRole("status", { name: "Ladestatus" })).toBeVisible();
  });

  it("shows the content once it has loaded", async () => {
    const { rerender } = await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: false,
        fassungEId: "art-1",
      },
    });

    await rerender({
      detailsContent: {
        gesamtausgaben,
        html: "<p>Norm content</p>",
        error: false,
      },
    });

    expect(
      screen.queryByRole("status", { name: "Ladestatus" }),
    ).not.toBeInTheDocument();
    expect(screen.getByText("Norm content")).toBeVisible();
  });

  it("shows an error message when loading failed", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: false,
        fassungEId: "art-1",
        detailsContent: { gesamtausgaben: [], error: true },
      },
    });

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Es ist ein Fehler aufgetreten.",
    );
  });

  it("shows the Gesamtausgaben of the current Fassung, even without content", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: true,
        fassungEId: "art-1",
        detailsContent: { gesamtausgaben, html: "", error: false },
      },
    });

    expect(
      screen.queryByRole("status", { name: "Ladestatus" }),
    ).not.toBeInTheDocument();
    expect(screen.getByRole("list", { name: "Gesamtausgaben" })).toBeVisible();
  });

  it("hides the legislation content for the current Fassung", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: true,
        fassungEId: "art-1",
        detailsContent: {
          gesamtausgaben,
          html: "<p>Norm content</p>",
          error: false,
        },
      },
    });

    expect(screen.queryByText("Norm content")).not.toBeInTheDocument();
  });

  it.each([
    ["both dates", "2020-01-01/2030-12-31", "01.01.2020 - 31.12.2030"],
    ["only a from date", "2020-01-01/..", "gültig ab 01.01.2020"],
    ["only a to date", "../2030-12-31", "gültig bis 31.12.2030"],
  ])(
    "links the only Gesamtausgabe with %s",
    async (_, temporalCoverage, validity) => {
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId: "art-1",
          detailsContent: {
            gesamtausgaben: [
              {
                legislationIdentifier: "eli/bund/2020",
                temporalCoverage,
              } as LegislationExpression,
            ],
            html: "<p>Norm content</p>",
            error: false,
          },
        },
      });

      expect(
        screen.getByRole("link", { name: `Gesamtausgabe ${validity} öffnen` }),
      ).toBeVisible();
      expect(
        screen.queryByRole("button", { name: "Gesamtausgabe auswählen" }),
      ).not.toBeInTheDocument();
    },
  );

  it("links the only Gesamtausgabe without dates when it has no validity", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: false,
        fassungEId: "art-1",
        detailsContent: {
          gesamtausgaben: [
            {
              legislationIdentifier: "eli/bund/2020",
              temporalCoverage: "../..",
            } as LegislationExpression,
          ],
          html: "<p>Norm content</p>",
          error: false,
        },
      },
    });

    expect(
      screen.getByRole("link", { name: "Gesamtausgabe öffnen" }),
    ).toBeVisible();
  });

  it.each([
    ["an unencoded", "art-z§§ 1 bis 3"],
    ["an already encoded", "art-z%C2%A7%C2%A7%201%20bis%203"],
  ])(
    "links the only Gesamtausgabe to the article with %s eId",
    async (_, fassungEId) => {
      await renderSuspended(FassungDetails, {
        route: "/?from=%2Fsuche%3Fq%3Dtest",
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId,
          detailsContent: {
            gesamtausgaben: [
              {
                legislationIdentifier: "eli/bund/2020",
                temporalCoverage: "2020-01-01/..",
              } as LegislationExpression,
            ],
            error: false,
          },
        },
      });

      expect(
        screen.getByRole("link", { name: /^Gesamtausgabe/ }),
      ).toHaveAttribute(
        "href",
        "/gesetze/eli/bund/2020/art-z§§ 1 bis 3?from=/suche?q=test",
      );
    },
  );

  it("links every Gesamtausgabe in the selection to the article", async () => {
    await renderSuspended(FassungDetails, {
      props: {
        currentLegislationIdentifier: "eli/bund/2031",
        current: true,
        fassungEId: "art-1",
        detailsContent: {
          gesamtausgaben: gesamtausgaben.slice(0, 2),
          error: false,
        },
      },
    });

    const list = screen.getByRole("list", { name: "Gesamtausgaben" });
    const hrefs = within(list)
      .getAllByRole("link")
      .map((link) => link.getAttribute("href"));
    expect(hrefs).toEqual([
      "/gesetze/eli/bund/2031/art-1",
      "/gesetze/eli/bund/2026/art-1",
    ]);
  });

  describe("desktop accordion", () => {
    it("is collapsed for a non-current Fassung", async () => {
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId: "art-1",
          detailsContent: { gesamtausgaben, error: false },
        },
      });

      expect(
        screen.queryByRole("list", { name: "Gesamtausgaben" }),
      ).not.toBeInTheDocument();
    });

    it("is expanded for the current Fassung", async () => {
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: true,
          fassungEId: "art-1",
          detailsContent: { gesamtausgaben, error: false },
        },
      });

      expect(
        screen.getByRole("list", { name: "Gesamtausgaben" }),
      ).toBeVisible();
    });

    it("shows the 5 newest Gesamtausgaben until the user asks for all", async () => {
      const user = userEvent.setup();
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId: "art-1",
          detailsContent: { gesamtausgaben, error: false },
        },
      });

      // The mobile drawer trigger has the same name but opens a dialog
      const accordionButton = screen
        .getAllByRole("button", { name: "Gesamtausgabe auswählen" })
        .find((button) => !button.hasAttribute("aria-haspopup"))!;
      await user.click(accordionButton);

      const list = screen.getByRole("list", { name: "Gesamtausgaben" });
      const links = within(list).getAllByRole("link");
      expect(links).toHaveLength(5);
      expect(links[0]).toHaveTextContent("01.01.2031");
      expect(links[4]).toHaveTextContent("01.01.2027");

      await user.click(screen.getByRole("button", { name: "Alles anzeigen" }));

      expect(within(list).getAllByRole("link")).toHaveLength(6);
      expect(within(list).getByText("01.01.2026")).toBeVisible();

      await user.click(
        screen.getByRole("button", { name: "Weniger anzeigen" }),
      );

      expect(within(list).getAllByRole("link")).toHaveLength(5);
      expect(within(list).queryByText("01.01.2026")).not.toBeInTheDocument();
    });

    it("does not offer 'Alles anzeigen' when all Gesamtausgaben fit", async () => {
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: true,
          fassungEId: "art-1",
          detailsContent: {
            gesamtausgaben: gesamtausgaben.slice(0, 5),
            error: false,
          },
        },
      });

      expect(
        screen.queryByRole("button", { name: "Alles anzeigen" }),
      ).not.toBeInTheDocument();
    });
  });

  describe("mobile drawer", () => {
    it("shows the 3 newest Gesamtausgaben until the user asks for all", async () => {
      const user = userEvent.setup();
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId: "art-1",
          detailsContent: { gesamtausgaben, error: false },
        },
      });

      // The desktop accordion header has the same name but expands in place
      const drawerButton = screen
        .getAllByRole("button", { name: "Gesamtausgabe auswählen" })
        .find((button) => button.getAttribute("aria-haspopup") === "dialog")!;
      await user.click(drawerButton);

      const drawer = screen.getByRole("dialog", { name: "Gesamtausgaben" });
      const links = within(drawer).getAllByRole("link");
      expect(links).toHaveLength(3);
      expect(links[0]).toHaveTextContent("01.01.2031");
      expect(links[2]).toHaveTextContent("01.01.2029");

      await user.click(
        within(drawer).getByRole("button", { name: "Alles anzeigen" }),
      );

      expect(within(drawer).getAllByRole("link")).toHaveLength(6);
    });

    it("does not offer 'Alles anzeigen' when all Gesamtausgaben fit", async () => {
      const user = userEvent.setup();
      await renderSuspended(FassungDetails, {
        props: {
          currentLegislationIdentifier: "eli/bund/2031",
          current: false,
          fassungEId: "art-1",
          detailsContent: {
            gesamtausgaben: gesamtausgaben.slice(0, 3),
            error: false,
          },
        },
      });

      const drawerButton = screen
        .getAllByRole("button", { name: "Gesamtausgabe auswählen" })
        .find((button) => button.getAttribute("aria-haspopup") === "dialog")!;
      await user.click(drawerButton);

      const drawer = screen.getByRole("dialog", { name: "Gesamtausgaben" });
      expect(
        within(drawer).queryByRole("button", { name: "Alles anzeigen" }),
      ).not.toBeInTheDocument();
    });
  });
});
