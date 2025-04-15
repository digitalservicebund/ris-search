// @vitest-environment node
import type { LocationQueryRaw, Router } from "vue-router";
import { DateSearchMode } from "@/stores/searchParams/index";
import { dateSearchFromQuery } from "@/stores/searchParams/dateParams";
import {
  addDefaults,
  defaultParams,
  getInitialState,
} from "@/stores/searchParams/getInitialState";
import { describe, expect } from "vitest";

vi.mock("./dateParams.ts");

describe("getInitialState", () => {
  const buildMockRouter = (query: LocationQueryRaw) =>
    ({
      currentRoute: { value: { query } },
    }) as Router;

  beforeAll(() => {
    vi.mocked(dateSearchFromQuery).mockImplementation(() => ({
      dateSearchMode: DateSearchMode.None,
    }));
  });

  it("returns the correct query", () => {
    expect(
      getInitialState(buildMockRouter({ query: "this query" })),
    ).toMatchObject({ query: "this query" });
  });

  it("returns the default values", () => {
    expect(getInitialState(buildMockRouter({}))).toEqual(defaultParams);
  });

  it("parses the category", () => {
    expect(
      getInitialState(buildMockRouter({ category: "R.urteil" })),
    ).toMatchObject({ category: "R.urteil" });
  });

  it("parses int page params", () => {
    expect(
      getInitialState(buildMockRouter({ itemsPerPage: "55", pageNumber: "7" })),
    ).toMatchObject({ itemsPerPage: 55, pageNumber: 7 });
  });

  it("parses sort", () => {
    expect(getInitialState(buildMockRouter({ sort: "-date" }))).toMatchObject({
      sort: "-date",
    });
  });

  it("parses court", () => {
    expect(
      getInitialState(buildMockRouter({ court: "OLVerfG Kiel" })),
    ).toMatchObject({ court: "OLVerfG Kiel" });
    expect(
      getInitialState(buildMockRouter({ court: ["in array"] })),
    ).toMatchObject({ court: "in array" });
  });

  it("delegates date parsing to ./dateParams", () => {
    getInitialState(buildMockRouter({ date: "some date" }));
    expect(dateSearchFromQuery).toHaveBeenCalledWith({ date: "some date" });
  });

  it("returns the default data for search parameters", () => {
    expect(addDefaults({} as LocationQueryRaw)).toEqual(defaultParams);
  });
});
