import { describe, expect, it } from "vitest";
import { convertParams } from "./SimpleSearch.logic";

describe("SimpleSearch.logic", () => {
  it("should convert the parameters correctly", async () => {
    const params = {
      query: "test",
      itemsPerPage: 10,
      pageNumber: 1,
      sort: "date",
      category: "R.Urteil",
      date: "2023-01-01",
      court: "court1",
      temporalCoverage: "0000",
    };

    const expectedParams = {
      searchTerm: "test",
      size: "10",
      pageIndex: "1",
      sort: "date",
      dateFrom: "2023-01-01",
      dateTo: "2023-01-01",
      typeGroup: "Urteil",
      court: "court1",
      temporalCoverageFrom: "0000",
      temporalCoverageTo: "0000",
    };

    expect(convertParams(params)).toEqual(expectedParams);
  });
});
