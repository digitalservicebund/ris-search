import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { expect, vi } from "vitest";
import type { LegislationExpression } from "~/types/api";
import GesamtausgabenList from "./GesamtausgabenList.vue";

const currentLegislationIdentifier =
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu";

// deliberately unsorted, so the tests can check that the newest come first
const gesamtausgaben = [
  {
    legislationIdentifier: "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu",
    temporalCoverage: "2000-01-05/2019-12-31",
  },
  {
    legislationIdentifier: currentLegislationIdentifier,
    temporalCoverage: "2020-01-01/..",
  },
  {
    legislationIdentifier: "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu",
    temporalCoverage: "2031-01-01/..",
  },
] as LegislationExpression[];

describe("GesamtausgabenList", () => {
  beforeEach(() => {
    vi.useFakeTimers({ toFake: ["Date"] });
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("lists gesamtausgaben, sorted by date", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    const rows = screen.getAllByRole("listitem");
    expect(rows).toHaveLength(3);

    expect(within(rows[0]!).getByText("01.01.2031")).toBeInTheDocument();
    expect(rows[0]).toHaveTextContent("Status: Zukünftig in Kraft");

    expect(within(rows[1]!).getByText("01.01.2020")).toBeInTheDocument();
    expect(rows[1]).toHaveTextContent("Status: Aktuell gültig");

    expect(within(rows[2]!).getByText("05.01.2000")).toBeInTheDocument();
    expect(within(rows[2]!).getByText("31.12.2019")).toBeInTheDocument();
    expect(rows[2]).toHaveTextContent("Status: Außer Kraft");
  });

  it("joins the dates with a dash when both exist", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: {
        currentLegislationIdentifier,
        gesamtausgaben: [gesamtausgaben[0]!],
      },
    });

    expect(within(screen.getByRole("listitem")).getByText("–")).toHaveAttribute(
      "aria-hidden",
      "true",
    );
  });

  it.each([
    ["from", "../2019-12-31"],
    ["to", "2031-01-01/.."],
  ])(
    "shows no placeholder and no dash for a missing %s date on desktop",
    async (_, temporalCoverage) => {
      await renderSuspended(GesamtausgabenList, {
        props: {
          currentLegislationIdentifier,
          gesamtausgaben: [
            {
              legislationIdentifier:
                "eli/bund/bgbl-1/2000/s001/2040-01-01/1/deu",
              temporalCoverage,
            } as LegislationExpression,
          ],
        },
      });

      const row = screen.getByRole("listitem");
      expect(within(row).getByText("—")).toHaveClass("md:hidden");
      expect(within(row).queryByText("–")).not.toBeInTheDocument();
    },
  );

  it("renders the column labels as a header", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    for (const label of ["Gültig ab", "Gültig bis", "Status"]) {
      expect(screen.getByText(label)).toBeInTheDocument();
    }
  });

  it("hides the header but keeps the labels per row when hideHeader is set", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben, hideHeader: true },
    });

    expect(screen.queryByText("Gültig ab")).not.toBeInTheDocument();
    expect(screen.getAllByText("Gültig ab:")).toHaveLength(3);
  });

  it("links every gesamtausgabe, so it can be opened in a new tab", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    const hrefs = screen
      .getAllByRole("link")
      .map((link) => link.getAttribute("href"));
    expect(hrefs).toEqual([
      "/gesetze/eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu#top",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu",
    ]);
  });

  it.each([
    ["an unencoded", "art-z§§ 1 bis 3"],
    ["an already encoded", "art-z%C2%A7%C2%A7%201%20bis%203"],
  ])(
    "links every gesamtausgabe to the article with %s eId",
    async (_, articleEId) => {
      await renderSuspended(GesamtausgabenList, {
        props: { currentLegislationIdentifier, gesamtausgaben, articleEId },
      });

      const hrefs = screen
        .getAllByRole("link")
        .map((link) => link.getAttribute("href"));
      expect(hrefs).toEqual([
        "/gesetze/eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu/art-z§§ 1 bis 3",
        "/gesetze/eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/art-z§§ 1 bis 3#top",
        "/gesetze/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/art-z§§ 1 bis 3",
      ]);
    },
  );

  it("keeps the from query parameter in the gesamtausgaben links", async () => {
    await renderSuspended(GesamtausgabenList, {
      route: "/?from=%2Fsuche%3Fq%3Dtest",
      props: {
        currentLegislationIdentifier,
        gesamtausgaben: [gesamtausgaben[0]!],
      },
    });

    expect(screen.getByRole("link")).toHaveAttribute(
      "href",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu?from=/suche?q=test",
    );
  });

  it("marks the gesamtausgabe currently displayed as the current page", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    const links = screen.getAllByRole("link");
    expect(links[0]).not.toHaveAttribute("aria-current");
    expect(links[1]).toHaveAttribute("aria-current", "page");
    expect(links[2]).not.toHaveAttribute("aria-current");
  });

  it("labels the status as unknown when it can't be determined", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: {
        currentLegislationIdentifier,
        gesamtausgaben: [
          {
            legislationIdentifier: "eli/bund/bgbl-1/2000/s001/2040-01-01/1/deu",
            temporalCoverage: "",
          } as LegislationExpression,
        ],
      },
    });

    expect(screen.getByRole("listitem")).toHaveTextContent("Status: Unbekannt");
  });

  it("shows a placeholder when there are no gesamtausgaben", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben: [] },
    });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("link")).toHaveLength(0);
  });

  it("does not announce anything before the gesamtausgaben change", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the gesamtausgaben change to none", async () => {
    const { rerender } = await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben },
    });

    await rerender({ gesamtausgaben: [] });

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many gesamtausgaben there are once there are some again", async () => {
    const { rerender } = await renderSuspended(GesamtausgabenList, {
      props: { currentLegislationIdentifier, gesamtausgaben: [] },
    });

    await rerender({ gesamtausgaben: [gesamtausgaben[0]!] });

    expect(screen.getByRole("status")).toHaveTextContent("1 Gesamtausgabe");

    await rerender({ gesamtausgaben });

    expect(screen.getByRole("status")).toHaveTextContent("3 Gesamtausgaben");
  });
});
