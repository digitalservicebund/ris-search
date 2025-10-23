import { describe, expect } from "vitest";
import type { LocationQueryRaw } from "vue-router";
import { dateSearchFromQuery } from "~/stores/searchParams/dateParams";
import {
  addDefaults,
  defaultParams,
  getInitialState,
} from "~/stores/searchParams/getInitialState";
import { DateSearchMode } from "~/stores/searchParams/index";

vi.mock("./dateParams.ts");

describe("getInitialState", () => {
  beforeAll(() => {
    vi.mocked(dateSearchFromQuery).mockImplementation(() => ({
      dateSearchMode: DateSearchMode.None,
    }));
  });

  it("returns the correct query", () => {
    expect(getInitialState({ query: "this query" })).toMatchObject({
      query: "this query",
    });
  });

  it("returns the default values", () => {
    expect(getInitialState({})).toEqual(defaultParams);
  });

  it("parses the category", () => {
    expect(getInitialState({ category: "R.urteil" })).toMatchObject({
      category: "R.urteil",
    });
  });

  it("parses int page params", () => {
    expect(
      getInitialState({ itemsPerPage: "55", pageNumber: "7" }),
    ).toMatchObject({ itemsPerPage: 55, pageNumber: 7 });
  });

  it("parses sort", () => {
    expect(getInitialState({ sort: "-date" })).toMatchObject({
      sort: "-date",
    });
  });

  it("parses court", () => {
    expect(getInitialState({ court: "OLVerfG Kiel" })).toMatchObject({
      court: "OLVerfG Kiel",
    });
    expect(getInitialState({ court: ["in array"] })).toMatchObject({
      court: "in array",
    });
  });

  it("delegates date parsing to ./dateParams", () => {
    getInitialState({ date: "some date" });
    expect(dateSearchFromQuery).toHaveBeenCalledWith({ date: "some date" });
  });

  it("returns the default data for search parameters", () => {
    expect(addDefaults({} as LocationQueryRaw)).toEqual(defaultParams);
  });
});
