import { computeExpandedKeys } from "./categoryFilter";
import { DocumentKind } from "~/types";

describe("computeExpandedKeys", () => {
  it("returns only DocumentKind.All when category is empty", () => {
    const result = computeExpandedKeys("");
    expect(result).toEqual({ [DocumentKind.All]: true });
  });

  it("expands a simple category", () => {
    const result = computeExpandedKeys("N");
    expect(result).toEqual({
      N: true,
    });
  });

  it("expands the 'all' child of a complex category", () => {
    const result = computeExpandedKeys("R");
    expect(result).toEqual({
      R: true,
      "R.all": true,
    });
  });

  it("expands multi-level category correctly", () => {
    const result = computeExpandedKeys("R.urteil");
    expect(result).toEqual({
      R: true,
      "R.urteil": true,
    });
  });

  it("expands literature", () => {
    const result = computeExpandedKeys("L");
    expect(result).toEqual({
      L: true,
    });
  });
});
