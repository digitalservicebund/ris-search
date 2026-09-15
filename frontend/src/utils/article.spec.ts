import { describe, expect, it } from "vitest";
import { tocHeadlineAdditionLabel } from "~/utils/article";

describe("tocHeadlineAdditionLabel", () => {
  it("returns an empty string if temporal coverage is undefined", () => {
    expect(tocHeadlineAdditionLabel(undefined)).toBe("");
  });

  it("returns an empty string for an open start and open end temporal coverage", () => {
    expect(tocHeadlineAdditionLabel("../..")).toBe("");
  });

  it("returns from and to date for a full temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/2025-12-01")).toBe(
      "(01.09.2025 - 01.12.2025)",
    );
  });

  it("returns only the from date for an open end temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/..")).toBe("(vom 01.09.2025)");
  });

  it("returns only the to date for an open start temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("../2025-12-01")).toBe("(bis 01.12.2025)");
  });
});
