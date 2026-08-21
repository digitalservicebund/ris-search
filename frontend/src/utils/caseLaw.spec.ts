import { describe, expect, it } from "vitest";
import { getCaselawSecondaryTitle } from "~/utils/caseLaw";
import { truncateAtWord } from "~/utils/textFormatting";

describe("caselaw", () => {
  describe("getCaselawSecondaryTitle", () => {
    it("joins all nonblank decision names", () => {
      expect(
        getCaselawSecondaryTitle({
          decisionName: ["", "Decision name", "Another decision name"],
          titleLine: "Title line",
        }),
      ).toBe("Decision name, Another decision name");
    });

    it("falls back to the Titlezeile", () => {
      expect(
        getCaselawSecondaryTitle({
          decisionName: [],
          titleLine: "Title line",
        }),
      ).toBe("Title line");
    });

    it("returns undefined without a decision name or Titlezeile", () => {
      expect(getCaselawSecondaryTitle({ decisionName: [] })).toBeUndefined();
    });

    it("truncates the secondary title to 90 characters", () => {
      expect(
        getCaselawSecondaryTitle({ decisionName: ["a".repeat(100)] }),
      ).toBe("a".repeat(90) + "…");
    });

    it("truncates the secondary title after formatting decision names", () => {
      const decisionName1 = "a".repeat(50);
      const decisionName2 = "b".repeat(50);
      expect(
        getCaselawSecondaryTitle({
          decisionName: [decisionName1, decisionName2],
        }),
      ).toBe(truncateAtWord(`${decisionName1}, ${decisionName2}`, 90, true));
    });

    it("does not truncate the title when disabled", () => {
      const fullTItle = "a".repeat(100);
      expect(
        getCaselawSecondaryTitle({ decisionName: [fullTItle] }, false),
      ).toBe(fullTItle);
    });
  });
});
