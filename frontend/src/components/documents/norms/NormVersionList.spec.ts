import {
  mountSuspended,
  registerEndpoint,
  mockNuxtImport,
} from "@nuxt/test-utils/runtime";
import { vi } from "vitest";
import NormVersionList from "./NormVersionList.vue";
import { data } from "~/components/documents/norms/NormVersions.spec.data";

registerEndpoint(`/v1/legislation`, () => {
  return data;
});

const { mockNavigateTo } = vi.hoisted(() => ({ mockNavigateTo: vi.fn() }));
mockNuxtImport("navigateTo", () => mockNavigateTo);

describe("NormVersionList", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));

    mockNavigateTo.mockClear();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("lists versions, sorted by date", async () => {
    const wrapper = await mountSuspended(NormVersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member[1]?.legislationIdentifier ?? "",
        versions: data.member,
      },
      global: {
        stubs: {
          IncompleteDataMessage: true,
        },
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
    const wrapper = await mountSuspended(NormVersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member[1]?.legislationIdentifier ?? "",
        versions: data.member,
      },
      global: {
        stubs: {
          IncompleteDataMessage: true,
        },
      },
    });

    const futureVersionRow = wrapper.find("tbody").findAll("tr")[0];
    await futureVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledTimes(1);
    expect(mockNavigateTo).toHaveBeenCalledWith(
      "/norms/eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
    );
  });

  it("does not nvigate to fassung currently displayed", async () => {
    const wrapper = await mountSuspended(NormVersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member[1]?.legislationIdentifier ?? "",
        versions: data.member,
      },
      global: {
        stubs: {
          IncompleteDataMessage: true,
        },
      },
    });

    const futureVersionRow = wrapper.find("tbody").findAll("tr")[1];
    await futureVersionRow?.trigger("click");

    expect(mockNavigateTo).toHaveBeenCalledTimes(0);
  });
});
