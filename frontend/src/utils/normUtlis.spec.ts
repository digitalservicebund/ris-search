import { describe, expect } from "vitest";
import { temporalCoverageToValidityInterval } from "~/utils/normUtils";

describe("temporalCoverageToValidityInterval", () => {
  it("returns undefined if temporal coverage is undifined", () => {
    const result = temporalCoverageToValidityInterval(undefined);
    expect(result).toBeUndefined();
  });

  it("extracts from and to date from full temporal coverage string", () => {
    const expectedFrom = "01.09.2025";
    const expectedTo = "01.12.2025";
    const result = temporalCoverageToValidityInterval("2025-09-01/2025-12-01");
    expect(result?.from).toBe(expectedFrom);
    expect(result?.to).toBe(expectedTo);
  });

  it("extracts from date from open end temporal coverage string", () => {
    const expectedFrom = "01.09.2025";
    const result = temporalCoverageToValidityInterval("2025-09-01/..");
    expect(result?.from).toBe(expectedFrom);
    expect(result?.to).toBeUndefined();
  });

  it("extracts to date from open start temporal coverage string", () => {
    const expectedTo = "01.12.2025";
    const result = temporalCoverageToValidityInterval("../2025-12-01");
    expect(result?.from).toBeUndefined();
    expect(result?.to).toBe(expectedTo);
  });

  it("extracts validity interval from open start and open end temporal coverage string", () => {
    const result = temporalCoverageToValidityInterval("../..");
    expect(result?.from).toBeUndefined();
    expect(result?.to).toBeUndefined();
  });
});
