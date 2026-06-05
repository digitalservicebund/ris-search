import { ref } from "vue";
import type { LegislationExpression } from "~/types/api";
import { useNormVersionFilter } from "./useNormVersionFilter";

function expressionWithCoverage(
  temporalCoverage: string,
): LegislationExpression {
  return { temporalCoverage } as LegislationExpression;
}

describe("useNormVersionFilter", () => {
  it("returns empty list when the version list is empty", () => {
    const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
      ref([]),
    );

    dateFilterValue.value = "2020-01-01";
    expect(filteredNormVersions.value).toEqual([]);
  });

  it.each([[""], [undefined]])(
    "returns the full list when the date filter is '%s'",
    (filterValue?: string) => {
      const versions = [
        expressionWithCoverage("2020-01-01/2021-12-31"),
        expressionWithCoverage("2022-01-01/2023-12-31"),
      ];
      const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
        ref(versions),
      );

      dateFilterValue.value = filterValue;
      expect(filteredNormVersions.value).toEqual(versions);
    },
  );

  it.each([["2020-01-01"], ["2020-01-02"], ["2020-01-03"]])(
    "returns matching expression when filter date '%s' falls within validity interval",
    (filterValue: string) => {
      const matchingExpression = expressionWithCoverage(
        "2020-01-01/2020-01-03",
      );
      const versions = [
        expressionWithCoverage("2019-01-01/2019-12-31"),
        matchingExpression,
        expressionWithCoverage("2020-01-04/2022-01-01"),
      ];

      const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
        ref(versions),
      );
      dateFilterValue.value = filterValue;
      expect(filteredNormVersions.value).toEqual([matchingExpression]);
    },
  );

  it("matches when expression has undefined out of force date", () => {
    const matchingExpression = expressionWithCoverage("2020-01-04/..");

    const versions = [
      expressionWithCoverage("2020-01-01/2020-01-03"),
      matchingExpression,
    ];
    const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
      ref(versions),
    );

    dateFilterValue.value = "2021-01-01";
    expect(filteredNormVersions.value).toEqual([matchingExpression]);
  });

  it.each([["2019-12-31"], ["2021-01-01"]])(
    "returns empty list when filter date '%s' is outside validity interval",
    (filterValue: string) => {
      const versions = [
        expressionWithCoverage("2020-01-01/2020-01-03"),
        expressionWithCoverage("2020-01-04/2020-12-31"),
      ];

      const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
        ref(versions),
      );
      dateFilterValue.value = filterValue;
      expect(filteredNormVersions.value).toEqual([]);
    },
  );

  it("does not match if expression has undefined in force date", () => {
    const versions = [
      expressionWithCoverage("../2019-12-31"),
      expressionWithCoverage("2020-01-01/2020-01-03"),
    ];
    const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
      ref(versions),
    );

    dateFilterValue.value = "2018-01-01";
    expect(filteredNormVersions.value).toEqual([]);
  });

  it("does not match if expression has undefined in force and out of force date", () => {
    const versions = [expressionWithCoverage("../..")];
    const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
      ref(versions),
    );

    dateFilterValue.value = "2018-01-01";
    expect(filteredNormVersions.value).toEqual([]);
  });

  it("reacts to changes of the date filter value", () => {
    const match = expressionWithCoverage("2020-01-01/2023-12-31");
    const { dateFilterValue, filteredNormVersions } = useNormVersionFilter(
      ref([match]),
    );

    expect(filteredNormVersions.value).toEqual([match]);

    dateFilterValue.value = "2025-01-01";
    expect(filteredNormVersions.value).toEqual([]);
  });
});
