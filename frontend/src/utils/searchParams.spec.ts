import { describe, expect, it } from "vitest";
import { searchParamToString, searchParamToNumber } from "./searchParams";

describe("searchParamToString", () => {
  it("returns the value when given a string", () => {
    expect(searchParamToString("hello")).toBe("hello");
  });

  it("returns undefined when given null", () => {
    expect(searchParamToString(null)).toBeUndefined();
  });

  it("returns undefined when given undefined", () => {
    expect(searchParamToString(undefined)).toBeUndefined();
  });

  it("returns the first element when given an array", () => {
    expect(searchParamToString(["first", "second"])).toBe("first");
  });

  it("returns undefined when given an array with null as the first element", () => {
    expect(searchParamToString([null, "second"])).toBeUndefined();
  });

  it("returns undefined when given an empty array", () => {
    expect(searchParamToString([])).toBeUndefined();
  });
});

describe("searchParamToNumber", () => {
  it("parses a numeric string", () => {
    expect(searchParamToNumber("42")).toBe(42);
  });

  it("returns the fallback when given null", () => {
    expect(searchParamToNumber(null, 10)).toBe(10);
  });

  it("returns the fallback when given undefined", () => {
    expect(searchParamToNumber(undefined, 5)).toBe(5);
  });

  it("returns undefined without a fallback when given null", () => {
    expect(searchParamToNumber(null)).toBeUndefined();
  });

  it("returns the fallback for a non-numeric string", () => {
    expect(searchParamToNumber("abc", 0)).toBe(0);
  });

  it("returns undefined for a non-numeric string without a fallback", () => {
    expect(searchParamToNumber("abc")).toBeUndefined();
  });

  it("parses the first element of an array", () => {
    expect(searchParamToNumber(["7", "99"])).toBe(7);
  });

  it("returns the fallback for an empty string", () => {
    expect(searchParamToNumber("", 3)).toBe(3);
  });

  it("parses negative numbers", () => {
    expect(searchParamToNumber("-5")).toBe(-5);
  });

  it("truncates decimal strings to integers", () => {
    expect(searchParamToNumber("3.9")).toBe(3);
  });
});
