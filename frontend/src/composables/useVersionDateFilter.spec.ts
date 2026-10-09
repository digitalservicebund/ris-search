import { ref } from "vue";
import {
  useVersionDateFilter,
  versionDateFilter,
} from "./useVersionDateFilter";

function versionWithCoverage(temporalCoverage: string) {
  return { temporalCoverage };
}

describe("versionDateFilter", () => {
  it("returns empty list when the version list is empty", () => {
    expect(versionDateFilter([], "2020-01-01")).toEqual([]);
  });

  it.each([[""], [undefined]])(
    "returns the full list when the date filter is '%s'",
    (date?: string) => {
      const versions = [
        versionWithCoverage("2020-01-01/2021-12-31"),
        versionWithCoverage("2022-01-01/2023-12-31"),
      ];

      expect(versionDateFilter(versions, date)).toBe(versions);
    },
  );

  it.each([["2020-01-01"], ["2020-01-02"], ["2020-01-03"]])(
    "returns matching version when filter date '%s' falls within validity interval",
    (date: string) => {
      const matchingVersion = versionWithCoverage("2020-01-01/2020-01-03");
      const versions = [
        versionWithCoverage("2019-01-01/2019-12-31"),
        matchingVersion,
        versionWithCoverage("2020-01-04/2022-01-01"),
      ];

      expect(versionDateFilter(versions, date)).toEqual([matchingVersion]);
    },
  );

  it("matches when version has undefined out of force date", () => {
    const matchingVersion = versionWithCoverage("2020-01-04/..");
    const versions = [
      versionWithCoverage("2020-01-01/2020-01-03"),
      matchingVersion,
    ];

    expect(versionDateFilter(versions, "2021-01-01")).toEqual([
      matchingVersion,
    ]);
  });

  it.each([["2019-12-31"], ["2021-01-01"]])(
    "returns empty list when filter date '%s' is outside validity interval",
    (date: string) => {
      const versions = [
        versionWithCoverage("2020-01-01/2020-01-03"),
        versionWithCoverage("2020-01-04/2020-12-31"),
      ];

      expect(versionDateFilter(versions, date)).toEqual([]);
    },
  );

  it("does not match if version has undefined in force date", () => {
    const versions = [
      versionWithCoverage("../2019-12-31"),
      versionWithCoverage("2020-01-01/2020-01-03"),
    ];

    expect(versionDateFilter(versions, "2018-01-01")).toEqual([]);
  });

  it("does not match if version has undefined in force and out of force date", () => {
    expect(
      versionDateFilter([versionWithCoverage("../..")], "2018-01-01"),
    ).toEqual([]);
  });
});

describe("useVersionDateFilter", () => {
  const versions = [
    versionWithCoverage("2020-01-01/2021-12-31"),
    versionWithCoverage("2022-01-01/2023-12-31"),
  ];

  it("returns all versions before a date is set", () => {
    const { filteredVersions } = useVersionDateFilter(ref(versions));

    expect(filteredVersions.value).toEqual(versions);
  });

  it("reacts to changes of the date filter value", () => {
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(
      ref(versions),
    );

    dateFilterValue.value = "2022-06-01";
    expect(filteredVersions.value).toEqual([versions[1]]);

    dateFilterValue.value = "2025-01-01";
    expect(filteredVersions.value).toEqual([]);
  });

  it("reacts to changes of the versions", () => {
    const versionsRef = ref([versions[0]!]);
    const { dateFilterValue, filteredVersions } =
      useVersionDateFilter(versionsRef);
    dateFilterValue.value = "2022-06-01";

    expect(filteredVersions.value).toEqual([]);

    versionsRef.value = versions;
    expect(filteredVersions.value).toEqual([versions[1]]);
  });
});
