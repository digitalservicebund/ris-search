import { formatNumberWithSeparators } from "./numberFormatting";

describe("numberFormatting", () => {
  describe("formatNumberWithSeparators", () => {
    it("returns the same number if no separators are needed", () => {
      expect(formatNumberWithSeparators(10)).toBe("10");
    });

    it("formats a number with one separator", () => {
      expect(formatNumberWithSeparators(1000)).toBe("1.000");
    });

    it("formats a number with multiple separators", () => {
      expect(formatNumberWithSeparators(100_000_000)).toBe("100.000.000");
    });
  });
});
