// @vitest-environment node
// @ts-nocheck
import { describe } from "vitest";
import {
  DateSearchMode,
  dateSearchFromQuery,
  dateSearchToQuery,
  useDateParams,
} from "./dateParams";

vi.mock("vue", () => ({
  ref: vi.fn((val) => ({ value: val })),
  computed: vi.fn((obj) => ({
    value: obj.get(),
    effect: obj.set,
  })),
}));

describe("dateSearchFromQuery", () => {
  it("should return Equal mode when only date is provided", () => {
    const query = { date: "2023-01-01" };
    expect(dateSearchFromQuery(query)).toEqual({
      dateSearchMode: DateSearchMode.Equal,
      date: "2023-01-01",
    });
  });

  it("should return Range mode when dateAfter and dateBefore are provided", () => {
    const query = { dateAfter: "2023-01-01", dateBefore: "2023-12-31" };
    expect(dateSearchFromQuery(query)).toEqual({
      dateSearchMode: DateSearchMode.Range,
      dateAfter: "2023-01-01",
      dateBefore: "2023-12-31",
    });
  });

  it("should return After mode when only dateAfter is provided", () => {
    const query = { dateAfter: "2023-01-01" };
    expect(dateSearchFromQuery(query)).toEqual({
      dateSearchMode: DateSearchMode.After,
      dateAfter: "2023-01-01",
    });
  });

  it("should return Before mode when only dateBefore is provided", () => {
    const query = { dateBefore: "2023-12-31" };
    expect(dateSearchFromQuery(query)).toEqual({
      dateSearchMode: DateSearchMode.Before,
      dateBefore: "2023-12-31",
    });
  });

  it("should return None mode when no date parameters are provided", () => {
    const query = {};
    expect(dateSearchFromQuery(query)).toEqual({
      dateSearchMode: DateSearchMode.None,
    });
  });
});

const sampleDates = Object.freeze({
  date: "2023-01-01",
  dateAfter: "2023-02-01",
  dateBefore: "2023-03-01",
});

describe("dateSearchToQuery", () => {
  it("should pick date, dateAfter, and dateBefore from params", () => {
    const params = {
      ...sampleDates,
      someOtherParam: "value",
    };
    expect(dateSearchToQuery(params)).toEqual(sampleDates);
  });

  it("should return an empty object if no date params are present", () => {
    const params = { someOtherParam: "value" };
    expect(dateSearchToQuery(params)).toEqual({});
  });
});

describe("useDateParams", () => {
  it("should initialize with the given state", () => {
    const initialState = {
      ...sampleDates,
      dateSearchMode: DateSearchMode.Range,
    };
    const result = useDateParams(initialState);
    expect(result.date.value).toBe("2023-01-01");
    expect(result.dateAfter.value).toBe("2023-02-01");
    expect(result.dateBefore.value).toBe("2023-03-01");
    expect(result.dateSearchMode.value).toBe(DateSearchMode.Range);
  });

  it("should update dateSearchMode and reset appropriate fields", () => {
    const initialState = {
      ...sampleDates,
      dateSearchMode: DateSearchMode.Range,
    };
    const result = useDateParams(initialState);

    result.dateSearchMode.effect(DateSearchMode.Equal);
    expect(result.date.value).toBe("2023-01-01");
    expect(result.dateAfter.value).toBeUndefined();
    expect(result.dateBefore.value).toBeUndefined();

    result.dateSearchMode.effect(DateSearchMode.After);
    expect(result.date.value).toBeUndefined();
    expect(result.dateAfter.value).toBe("2023-01-01");
    expect(result.dateBefore.value).toBeUndefined();

    result.dateSearchMode.effect(DateSearchMode.Before);
    expect(result.date.value).toBeUndefined();
    expect(result.dateAfter.value).toBeUndefined();
    expect(result.dateBefore.value).toBe("2023-01-01");

    result.dateSearchMode.effect(DateSearchMode.None);
    expect(result.date.value).toBeUndefined();
    expect(result.dateAfter.value).toBeUndefined();
    expect(result.dateBefore.value).toBeUndefined();
  });

  it("should reset to initial state", () => {
    const initialState = {
      ...sampleDates,
      dateSearchMode: DateSearchMode.Range,
    };
    const result = useDateParams(initialState);

    result.dateSearchMode.effect(DateSearchMode.Equal);
    result.date.value = "2024-01-01";

    result.reset(initialState);

    expect(result.date.value).toBe("2023-01-01");
    expect(result.dateAfter.value).toBe("2023-02-01");
    expect(result.dateBefore.value).toBe("2023-03-01");
    expect(result.dateSearchMode.value).toBe(DateSearchMode.Range);
  });
});
