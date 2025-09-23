// @vitest-environment node
import { describe, expect } from "vitest";
import {
  addEllipsis,
  getStringOrDefault,
  removeOuterParentheses,
  removePrefix,
} from "./textFormatting";

describe("getStringOrDefault", () => {
  it("returns the default value when the input string is undefined", () => {
    expect(getStringOrDefault(undefined, "default")).toBe("default");
  });

  it("returns the default value when the input string is null", () => {
    expect(getStringOrDefault(null, "default")).toBe("default");
  });

  it("returns the default value when the input string is empty", () => {
    expect(getStringOrDefault("", "default")).toBe("default");
  });

  it("returns the input string when it is not empty", () => {
    expect(getStringOrDefault("input", "default")).toBe("input");
  });

  it("returns the input string when it contains only whitespace", () => {
    expect(getStringOrDefault("   ", "default")).toBe("default");
  });
});

describe("removeOuterParentheses", () => {
  it("returns the input string with outer parentheses removed", () => {
    expect(removeOuterParentheses("(hello world)")).toBe("hello world");
    expect(removeOuterParentheses("hello world)")).toBe("hello world");
    expect(removeOuterParentheses("(hello world")).toBe("hello world");
    // accept this case
    expect(removeOuterParentheses("(hello) (world)")).toBe("hello) (world");
  });

  it("returns the input string unchanged in other cases", () => {
    const inputs = [
      "test",
      "parentheses (not outside)",
      "(parentheses) not outside",
      "<blink>(content)</blink>",
    ];
    for (const input of inputs) {
      expect(removeOuterParentheses(input)).toBe(input);
    }
  });

  it("returns an empty string for falsy inputs", () => {
    const inputs = [null, false, undefined, ""];
    for (const input of inputs) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      expect(removeOuterParentheses(input as any)).toBe("");
    }
  });
});

describe("addEllipsis heuristics", () => {
  it("handles empty strings", () => {
    expect(addEllipsis("")).toBe("");
  });
  it("treats <mark> tags correctly", () => {
    expect(addEllipsis("<mark>Start</mark>.")).toBe("<mark>Start</mark>.");
    expect(addEllipsis("Something <mark>marked</mark>.")).toBe(
      "Something <mark>marked</mark>.",
    );
    expect(addEllipsis("Something <mark>marked</mark>")).toBe(
      "Something <mark>marked</mark> …",
    );
  });
  it("prepends ellipses to lowercase-starting strings", () => {
    expect(addEllipsis("urlaub.")).toBe("… urlaub.");
    expect(addEllipsis("überaus.")).toBe("… überaus.");
  });
  it("adds ellipses to strings that end with words or whitespace", () => {
    expect(addEllipsis("Some sentence")).toBe("Some sentence …");
    expect(addEllipsis("Ä sentence")).toBe("Ä sentence …");
    expect(addEllipsis("Some sentence ")).toBe("Some sentence  …");
    expect(addEllipsis("Daß")).toBe("Daß …");
  });
  it("adds ellipses to strings that end with counts", () => {
    expect(addEllipsis("1. first. 2.")).toBe("1. first. 2. …");
    expect(addEllipsis("I. first. II.")).toBe("I. first. II. …");
  });
});

describe("removePrefix", () => {
  it("removes prefix when present", () => {
    expect(removePrefix("Hello World", "Hello ")).toBe("World");
  });
  it("removes original string when prefix not present", () => {
    expect(removePrefix("Hello World", "Hallo ")).toBe("Hello World");
  });
});
