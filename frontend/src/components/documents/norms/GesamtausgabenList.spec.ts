import {
  renderSuspended,
  registerEndpoint,
  mockNuxtImport,
} from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { expect, vi } from "vitest";
import type { JSONLDList, LegislationExpression } from "~/types/api";
import GesamtausgabenList from "./GesamtausgabenList.vue";

function createLegislationExpression(
  expressionEli: string,
  temporalCoverage: string,
  legalForce: "InForce" | "NotInForce",
): LegislationExpression {
  const workIdentifier = expressionEli.split("/").slice(0, 5).join("/");
  return {
    "@context": "http://localhost:8080/v1/context.jsonld",
    "@type": "Legislation",
    "@id": `/v1/legislation/${expressionEli}`,
    legislationIdentifier: expressionEli,
    temporalCoverage: temporalCoverage,
    legislationLegalForce: legalForce,
    exampleOfWork: {
      "@id": `/v1/legislation/${workIdentifier}`,
      "@type": "Legislation",
      legislationIdentifier: workIdentifier,
      legislationDate: "2025-01-01",
      datePublished: "2025-01-01",
      isPartOf: {
        name: "",
      },
    },
    name: "",
    alternateName: "",
    abbreviation: "",
    risAbbreviation: "",
    encoding: [],
    hasPart: [],
  };
}

export const data: JSONLDList<LegislationExpression> = {
  "@type": "hydra:Collection",
  totalItems: 3,
  member: [
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
      "2000-01-05/2019-12-31",
      "NotInForce",
    ),
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
      "2020-01-01/..",
      "InForce",
    ),
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      "2031-01-01/..",
      "NotInForce",
    ),
  ],
  view: {
    first: "",
    previous: undefined,
    next: undefined,
    last: "",
  },
};

registerEndpoint(`/v1/legislation`, () => {
  return data;
});

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} })),
}));
mockNuxtImport("useRoute", () => useRouteMock);

/** Props for the list, with the second gesamtausgabe being the displayed one. */
function props(gesamtausgaben = data.member!) {
  return {
    currentLegislationIdentifier: data.member![1]?.legislationIdentifier ?? "",
    gesamtausgaben,
  };
}

function hrefs() {
  return screen.getAllByRole("link").map((link) => link.getAttribute("href"));
}

describe("GesamtausgabenList", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));

    useRouteMock.mockReturnValue({ query: {} });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("lists gesamtausgaben, sorted by date", async () => {
    await renderSuspended(GesamtausgabenList, { props: props() });

    const gesamtausgaben = screen.getAllByRole("listitem");
    expect(gesamtausgaben).toHaveLength(3);

    expect(
      within(gesamtausgaben[0]!).getByText("01.01.2031"),
    ).toBeInTheDocument();
    expect(gesamtausgaben[0]).toHaveTextContent("Status: Zukünftig in Kraft");

    expect(
      within(gesamtausgaben[1]!).getByText("01.01.2020"),
    ).toBeInTheDocument();
    expect(gesamtausgaben[1]).toHaveTextContent("Status: Aktuell gültig");

    expect(
      within(gesamtausgaben[2]!).getByText("05.01.2000"),
    ).toBeInTheDocument();
    expect(
      within(gesamtausgaben[2]!).getByText("31.12.2019"),
    ).toBeInTheDocument();
    expect(gesamtausgaben[2]).toHaveTextContent("Status: Außer Kraft");
  });

  it("joins the dates with a dash when both exist", async () => {
    await renderSuspended(GesamtausgabenList, {
      props: props([data.member![0]!]),
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
        props: props([
          createLegislationExpression(
            "eli/bund/bgbl-1/2000/s001/2040-01-01/1/deu/regelungstext-1",
            temporalCoverage,
            "NotInForce",
          ),
        ]),
      });

      const row = screen.getByRole("listitem");
      expect(within(row).getByText("—")).toHaveClass("md:hidden");
      expect(within(row).queryByText("–")).not.toBeInTheDocument();
    },
  );

  it("renders the column labels as a header", async () => {
    await renderSuspended(GesamtausgabenList, { props: props() });

    for (const label of ["Gültig ab", "Gültig bis", "Status"]) {
      expect(screen.getByText(label)).toBeInTheDocument();
    }
  });

  it("links every gesamtausgabe, so it can be opened in a new tab", async () => {
    await renderSuspended(GesamtausgabenList, { props: props() });

    expect(hrefs()).toEqual([
      "/gesetze/eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
    ]);
  });

  it("marks the gesamtausgabe currently displayed as the current page", async () => {
    await renderSuspended(GesamtausgabenList, { props: props() });

    const links = screen.getAllByRole("link");
    expect(links[0]).not.toHaveAttribute("aria-current");
    expect(links[1]).toHaveAttribute("aria-current", "page");
    expect(links[2]).not.toHaveAttribute("aria-current");
  });

  it("keeps the from query parameter in the gesamtausgaben links", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    await renderSuspended(GesamtausgabenList, { props: props() });

    for (const href of hrefs()) {
      const url = new URL(href!, "http://localhost");
      expect(url.pathname).toMatch(/^\/gesetze\/eli\/bund\/bgbl-1\/2000\//);
      expect(url.searchParams.get("from")).toBe("/suche?q=test");
    }
  });

  it("labels the status as unknown when it can't be determined", async () => {
    const withoutCoverage = createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2040-01-01/1/deu/regelungstext-1",
      "",
      "NotInForce",
    );

    await renderSuspended(GesamtausgabenList, {
      props: props([withoutCoverage]),
    });

    expect(screen.getByRole("listitem")).toHaveTextContent("Status: Unbekannt");
  });

  it("shows a placeholder when there are no gesamtausgaben", async () => {
    await renderSuspended(GesamtausgabenList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("link")).toHaveLength(0);
  });

  it("does not announce anything before the gesamtausgaben change", async () => {
    await renderSuspended(GesamtausgabenList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the gesamtausgaben change to none", async () => {
    const { rerender } = await renderSuspended(GesamtausgabenList, {
      props: props(),
    });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many gesamtausgaben there are once there are some again", async () => {
    const { rerender } = await renderSuspended(GesamtausgabenList, {
      props: props([]),
    });

    await rerender(props([data.member![0]!]));

    expect(screen.getByRole("status")).toHaveTextContent("1 Gesamtausgabe");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Gesamtausgaben");
  });
});
