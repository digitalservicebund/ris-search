import {
  renderSuspended,
  registerEndpoint,
  mockNuxtImport,
} from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { vi } from "vitest";
import type { JSONLDList, LegislationExpression } from "~/types/api";
import VersionList from "./VersionList.vue";

function createLegislationExpression(
  expressionEli: string,
  temporalCoverage: string,
  legalForce: "InForce" | "NotInForce",
): LegislationExpression {
  const workIdentifier = expressionEli.split("/").slice(0, 5).join("/");
  return {
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

/** Props for the list, with the second version being the displayed one. */
function props(versions = data.member!) {
  return {
    currentLegislationIdentifier: data.member![1]?.legislationIdentifier ?? "",
    versions,
  };
}

function hrefs() {
  return screen.getAllByRole("link").map((link) => link.getAttribute("href"));
}

describe("VersionList", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));

    useRouteMock.mockReturnValue({ query: {} });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("lists versions, sorted by date", async () => {
    await renderSuspended(VersionList, { props: props() });

    const versions = screen.getAllByRole("listitem");
    expect(versions).toHaveLength(3);

    expect(versions[0]).toHaveTextContent(
      "Gültig ab: 01.01.2031 Gültig bis: – Status: Zukünftig in Kraft",
    );
    expect(versions[1]).toHaveTextContent(
      "Gültig ab: 01.01.2020 Gültig bis: – Status: Aktuell gültig",
    );
    expect(versions[2]).toHaveTextContent(
      "Gültig ab: 05.01.2000 Gültig bis: 31.12.2019 Status: Außer Kraft",
    );
  });

  it("renders the column labels as a header", async () => {
    await renderSuspended(VersionList, { props: props() });

    for (const label of ["Gültig ab", "Gültig bis", "Status"]) {
      expect(screen.getByText(label)).toBeInTheDocument();
    }
  });

  it("links every version, so it can be opened in a new tab", async () => {
    await renderSuspended(VersionList, { props: props() });

    expect(hrefs()).toEqual([
      "/gesetze/eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
      "/gesetze/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
    ]);
  });

  it("marks the version currently displayed as the current page", async () => {
    await renderSuspended(VersionList, { props: props() });

    const links = screen.getAllByRole("link");
    expect(links[0]).not.toHaveAttribute("aria-current");
    expect(links[1]).toHaveAttribute("aria-current", "page");
    expect(links[2]).not.toHaveAttribute("aria-current");
  });

  it("keeps the from query parameter in the version links", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    await renderSuspended(VersionList, { props: props() });

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

    await renderSuspended(VersionList, {
      props: props([withoutCoverage]),
    });

    expect(screen.getByRole("listitem")).toHaveTextContent(
      "Gültig ab: – Gültig bis: – Status: Unbekannt",
    );
  });

  it("shows a placeholder when there are no versions", async () => {
    await renderSuspended(VersionList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("link")).toHaveLength(0);
  });

  it("does not announce anything before the versions change", async () => {
    await renderSuspended(VersionList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the versions change to none", async () => {
    const { rerender } = await renderSuspended(VersionList, {
      props: props(),
    });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many versions there are once there are some again", async () => {
    const { rerender } = await renderSuspended(VersionList, {
      props: props([]),
    });

    await rerender(props([data.member![0]!]));

    expect(screen.getByRole("status")).toHaveTextContent("1 Fassung");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Fassungen");
  });
});
