import { mountSuspended, registerEndpoint } from "@nuxt/test-utils/runtime";
import { useBackendURL } from "~/composables/useBackendURL";
import NormVersionList from "./NormVersionList.vue";
import { data } from "~/components/Norm/NormVersions.spec.data";
import { vi } from "vitest";

registerEndpoint(`${useBackendURL()}/v1/legislation`, () => {
  return data;
});

describe("NormVersionList", () => {
  it("lists versions, sorted by date", async () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00"));

    const wrapper = await mountSuspended(NormVersionList, {
      props: {
        status: "success",
        currentLegislationIdentifier:
          data.member[1].item.workExample.legislationIdentifier,
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
      "Gültig von",
      "Gültig bis",
      "Status",
    ]);

    const tableBodyRows = wrapper.find("tbody").findAll("tr");
    expect(tableBodyRows).toHaveLength(3);

    const futureVersionRowCells = tableBodyRows[0].findAll("td");
    expect(futureVersionRowCells.map((cell) => cell.text())).toEqual([
      "01.01.2031",
      "-",
      "Zukünftig in Kraft",
    ]);

    const currentVersionRowCells = tableBodyRows[1].findAll("td");
    expect(currentVersionRowCells.map((cell) => cell.text())).toEqual([
      "01.01.2020",
      "-",
      "Aktuell gültig",
    ]);

    const pastVersionRowCells = tableBodyRows[2].findAll("td");
    expect(pastVersionRowCells.map((cell) => cell.text())).toEqual([
      "05.01.2000",
      "31.12.2019",
      "Außer Kraft",
    ]);

    vi.useRealTimers();
  });
});
