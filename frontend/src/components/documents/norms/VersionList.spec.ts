import {
  mountSuspended,
  registerEndpoint,
  mockNuxtImport,
} from "@nuxt/test-utils/runtime";
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

const { mockNavigateTo, useRouteMock } = vi.hoisted(() => ({
  mockNavigateTo: vi.fn(),
  useRouteMock: vi.fn(() => ({ query: {} })),
}));
mockNuxtImport("navigateTo", () => mockNavigateTo);
mockNuxtImport("useRoute", () => useRouteMock);

describe("VersionList", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));

    mockNavigateTo.mockClear();
    useRouteMock.mockReturnValue({ query: {} });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("lists versions, sorted by date", async () => {
    const wrapper = await mountSuspended(VersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member![1]?.legislationIdentifier ?? "",
        versions: data.member!,
      },
    });

    const headerRowCells = wrapper.find("thead").findAll("th");
    expect(headerRowCells.map((cell) => cell.text())).toEqual([
      "Gültig ab",
      "Gültig bis",
      "Status",
    ]);

    const tableBodyRows = wrapper.find("tbody").findAll("tr");
    expect(tableBodyRows).toHaveLength(3);

    const futureVersionRowCells = tableBodyRows[0]?.findAll("td");
    expect(futureVersionRowCells?.map((cell) => cell.text())).toEqual([
      "01.01.2031",
      "-",
      "Zukünftig in Kraft",
    ]);

    const currentVersionRowCells = tableBodyRows[1]?.findAll("td");
    expect(currentVersionRowCells?.map((cell) => cell.text())).toEqual([
      "01.01.2020",
      "-",
      "Aktuell gültig",
    ]);

    const pastVersionRowCells = tableBodyRows[2]?.findAll("td");
    expect(pastVersionRowCells?.map((cell) => cell.text())).toEqual([
      "05.01.2000",
      "31.12.2019",
      "Außer Kraft",
    ]);
  });

  it("navigates on row click if fassung is not currently displayed", async () => {
    const wrapper = await mountSuspended(VersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member![1]?.legislationIdentifier ?? "",
        versions: data.member!,
      },
    });

    const futureVersionRow = wrapper.find("tbody").findAll("tr")[0];
    await futureVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledTimes(1);
    expect(mockNavigateTo).toHaveBeenCalledWith({
      path: "/norms/eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      query: { from: undefined },
    });
  });

  it("does not nvigate to fassung currently displayed", async () => {
    const wrapper = await mountSuspended(VersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member![1]?.legislationIdentifier ?? "",
        versions: data.member!,
      },
    });

    const futureVersionRow = wrapper.find("tbody").findAll("tr")[1];
    await futureVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledTimes(0);
  });

  it("keeps the from query parameter when navigating to a version", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    const wrapper = await mountSuspended(VersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member![1]?.legislationIdentifier ?? "",
        versions: data.member!,
      },
    });

    const futureVersionRow = wrapper.find("tbody").findAll("tr")[0];
    await futureVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledWith({
      path: "/norms/eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      query: { from: "/suche?q=test" },
    });
  });

  it("keeps the from query parameter when navigating to a past version", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    const wrapper = await mountSuspended(VersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member![1]?.legislationIdentifier ?? "",
        versions: data.member!,
      },
    });

    const pastVersionRow = wrapper.find("tbody").findAll("tr")[2];
    await pastVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledWith({
      path: "/norms/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
      query: { from: "/suche?q=test" },
    });
  });
});
