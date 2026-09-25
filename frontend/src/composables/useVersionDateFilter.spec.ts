import { ref } from "vue";
import { useVersionDateFilter } from "./useVersionDateFilter";

function versionWithCoverage(temporalCoverage: string) {
  return { temporalCoverage };
}

describe("useVersionDateFilter", () => {
  it("returns empty list when the version list is empty", () => {
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(ref([]));

    dateFilterValue.value = "2020-01-01";
    expect(filteredVersions.value).toEqual([]);
  });

  it.each([[""], [undefined]])(
    "returns the full list when the date filter is '%s'",
    (filterValue?: string) => {
      const versions = [
        versionWithCoverage("2020-01-01/2021-12-31"),
        versionWithCoverage("2022-01-01/2023-12-31"),
      ];
      const { dateFilterValue, filteredVersions } = useVersionDateFilter(
        ref(versions),
      );

      dateFilterValue.value = filterValue;
      expect(filteredVersions.value).toEqual(versions);
    },
  );

  it.each([["2020-01-01"], ["2020-01-02"], ["2020-01-03"]])(
    "returns matching version when filter date '%s' falls within validity interval",
    (filterValue: string) => {
      const matchingVersion = versionWithCoverage("2020-01-01/2020-01-03");
      const versions = [
        versionWithCoverage("2019-01-01/2019-12-31"),
        matchingVersion,
        versionWithCoverage("2020-01-04/2022-01-01"),
      ];

      const { dateFilterValue, filteredVersions } = useVersionDateFilter(
        ref(versions),
      );
      dateFilterValue.value = filterValue;
      expect(filteredVersions.value).toEqual([matchingVersion]);
    },
  );

  it("matches when version has undefined out of force date", () => {
    const matchingVersion = versionWithCoverage("2020-01-04/..");

    const versions = [
      versionWithCoverage("2020-01-01/2020-01-03"),
      matchingVersion,
    ];
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(
      ref(versions),
    );

    dateFilterValue.value = "2021-01-01";
    expect(filteredVersions.value).toEqual([matchingVersion]);
  });

  it.each([["2019-12-31"], ["2021-01-01"]])(
    "returns empty list when filter date '%s' is outside validity interval",
    (filterValue: string) => {
      const versions = [
        versionWithCoverage("2020-01-01/2020-01-03"),
        versionWithCoverage("2020-01-04/2020-12-31"),
      ];

      const { dateFilterValue, filteredVersions } = useVersionDateFilter(
        ref(versions),
      );
      dateFilterValue.value = filterValue;
      expect(filteredVersions.value).toEqual([]);
    },
  );

  it("does not match if version has undefined in force date", () => {
    const versions = [
      versionWithCoverage("../2019-12-31"),
      versionWithCoverage("2020-01-01/2020-01-03"),
    ];
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(
      ref(versions),
    );

    dateFilterValue.value = "2018-01-01";
    expect(filteredVersions.value).toEqual([]);
  });

  it("does not match if version has undefined in force and out of force date", () => {
    const versions = [versionWithCoverage("../..")];
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(
      ref(versions),
    );

    dateFilterValue.value = "2018-01-01";
    expect(filteredVersions.value).toEqual([]);
  });

  it("reacts to changes of the date filter value", () => {
    const match = versionWithCoverage("2020-01-01/2023-12-31");
    const { dateFilterValue, filteredVersions } = useVersionDateFilter(
      ref([match]),
    );

    expect(filteredVersions.value).toEqual([match]);

    dateFilterValue.value = "2025-01-01";
    expect(filteredVersions.value).toEqual([]);
  });
});
