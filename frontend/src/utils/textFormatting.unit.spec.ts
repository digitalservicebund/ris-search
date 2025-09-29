// @vitest-environment node
import { describe, expect } from "vitest";
import {
  addEllipsis,
  getStringOrDefault,
  removeOuterParentheses,
  truncateAtWord,
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

describe("truncateAtWord", () => {
  it("returns the string unchanged when length ≤ max", () => {
    expect(truncateAtWord("Hello world", 20)).toBe("Hello world");
  });

  it("truncates at the last whole word before the limit", () => {
    expect(truncateAtWord("Hello brave world", 13)).toBe("Hello brave");
    expect(truncateAtWord("Alpha Beta Gamma", 10)).toBe("Alpha Beta");
  });

  it("normalizes internal whitespace before truncating", () => {
    expect(truncateAtWord("  Hello   \n  world  ", 12)).toBe("Hello world");
    expect(truncateAtWord("\tOne   two\tthree\nfour", 9)).toBe("One two");
  });

  it("falls back to a hard cut when there is no space before the limit", () => {
    expect(truncateAtWord("Supercalifragilistic", 5)).toBe("Super");
  });

  it("handles tiny limits", () => {
    expect(truncateAtWord("Hi there", 1)).toBe("H");
    expect(truncateAtWord("Hi there", 0)).toBe("");
  });

  it("works with Unicode letters", () => {
    expect(truncateAtWord("Über schöne Dinge", 8)).toBe("Über");
    expect(truncateAtWord("Äpfel Birnen Kirschen", 12)).toBe("Äpfel Birnen");
  });
});
